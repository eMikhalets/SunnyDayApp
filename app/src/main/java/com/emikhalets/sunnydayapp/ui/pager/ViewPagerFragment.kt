package com.emikhalets.sunnydayapp.ui.pager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.database.MatrixCursor
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class ViewPagerFragment : Fragment() {

    private val LOCATION_PERMISSIONS_REQUEST = 42
    private val CREATED = "CREATED"
    private val DELETED = "DELETED"
    private val CREATING = "CREATING"

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var searchAdapter: SimpleCursorAdapter
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState ?: checkExistingCitiesTable()
        implementObserversClickListeners()
        implementSearch()
        pagerAdapter = PagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        attachTabsAndPager()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!checkCoarseLocationPermission() && !checkFineLocationPermission()) {
            Timber.d("Location permissions not granted. Request permissions")
            requestLocationPermissions()
        } else {
            Timber.d("Location permissions was granted. Request location")
            requestLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Granting location permissions

    private fun checkCoarseLocationPermission() =
        when (ContextCompat.checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION)) {
            PERMISSION_GRANTED -> true
            else -> false
        }

    private fun checkFineLocationPermission() =
        when (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)) {
            PERMISSION_GRANTED -> true
            else -> false
        }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
            LOCATION_PERMISSIONS_REQUEST
        )
    }

    // Request location

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000 * 60 * 10
            fastestInterval = interval / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 1000f
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback(),
            Looper.getMainLooper()
        )
    }

    private fun locationCallback() = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult ?: return
            setLocation(locationResult.locations.first())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("Request Permissions Result")
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Timber.d("Location permissions have been granted")
                    requestLocation()
                } else {
                    Timber.d("Location permissions have not been granted")
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.city_list_text_location_permissions_not_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setLocation(location: Location?) {
        location?.let {
            Timber.d("Location has been updated: $location")
            val lat = location.latitude
            val lon = location.longitude
            val geo = Geocoder(requireContext(), Locale.getDefault())
            val address = geo.getFromLocation(lat, lon, 1).first()
            val query = "${address.locality}, ${address.countryName}"
            Timber.d("Location query has been updated: ($query)")
            pagerViewModel.updateLocation(lat, lon, query)
        }
    }

    private fun implementObserversClickListeners() {
        pagerViewModel.searchingCities.observe(viewLifecycleOwner, {
            val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "city_name"))
            for (i in it.indices) cursor.addRow(arrayOf(i, it[i]))
            searchAdapter.changeCursor(cursor)
            searchView.suggestionsAdapter = searchAdapter
        })

        pagerViewModel.currentQuery.observe(viewLifecycleOwner, {
            Timber.d("Query has been updated: ($it)")
            binding.toolbar.subtitle = it
            binding.viewPager.setCurrentItem(1, true)
        })

        pagerViewModel.locationQuery.observe(viewLifecycleOwner, {
            Timber.d("Location query has been updated: ($it)")
            binding.toolbar.subtitle = it
        })

        pagerViewModel.dbStatus.observe(viewLifecycleOwner, {
            when (it) {
                CREATED -> {
                    Timber.d("Cities table in database has been created")
                    setVisibilityMode(it)
                }
                DELETED -> {
                    Timber.d("Cities table has been deleted")
                    convertCitiesCitiesToDB()
                }
            }
        })

        binding.toolbar.findViewById<View>(R.id.menu_pager_preference).setOnClickListener {
            // TODO: DO CLICK ON SETTINGS!!!
            Timber.d("Settings Click")
//            Navigation.findNavController(binding.root)
//                .navigate(R.id.action_viewPagerFragment_to_preferencePagerFragment)
        }

        binding.toolbar.findViewById<View>(R.id.menu_pager_recreate_db).setOnClickListener {
            Timber.d("Recreating database click")
            setVisibilityMode(CREATING)
            pagerViewModel.deleteCitiesTable()
        }
    }

    private fun implementSearch() {
        searchView = binding.toolbar.menu.findItem(R.id.menu_pager_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                searchAdapter.changeCursor(null)
                if (newText.length >= 2) {
                    Timber.d("The search query has been changed: ($newText)")
                    pagerViewModel.getCitiesByName(newText)
                }
                return true
            }
        })

        implementSuggestionsAdapter()
    }

    private fun implementSuggestionsAdapter() {
        searchAdapter = SimpleCursorAdapter(
            requireContext(), android.R.layout.simple_list_item_1, null, arrayOf("city_name"),
            intArrayOf(android.R.id.text1), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(p0: Int): Boolean = false

            override fun onSuggestionClick(p0: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(p0) as Cursor
                val name = cursor.getString(cursor.getColumnIndex("city_name"))
                cursor.close()
                Timber.d("Select in search: $name")

                searchView.setQuery(name, false)
                searchView.onActionViewCollapsed()
                searchView.setQuery(null, false)

                binding.toolbar.subtitle = name
                pagerViewModel.updateCurrentQuery(name)
                searchAdapter.changeCursor(null)
                pagerViewModel.changeIsAddedCity(name)
                return true
            }
        })
    }

    private fun attachTabsAndPager() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_title_city_list)
                1 -> tab.text = getString(R.string.tab_title_current)
            }
            tab.select()
        }.attach()
        binding.viewPager.setCurrentItem(1, false)
    }

    private fun checkExistingCitiesTable() {
        val dataStore = requireContext().createDataStore(getString(R.string.ds_file_name))
        val isDbCreatedKey = preferencesKey<Boolean>(getString(R.string.ds_is_db_created))
        val isDbCreatedFlow: Flow<Boolean> = dataStore.data.map { it[isDbCreatedKey] ?: false }
        lifecycleScope.launch {
            isDbCreatedFlow.collect { isDbCreated ->
                if (!isDbCreated) {
                    Timber.d("Cities table not exist")
                    setVisibilityMode(CREATING)
                    convertCitiesCitiesToDB()
                    dataStore.edit { it[isDbCreatedKey] = true }
                } else {
                    Timber.d("Cities table in database was created")
                    setVisibilityMode(CREATED)
                }
            }
        }
    }

    private fun convertCitiesCitiesToDB() {
        requireContext().assets.open("cities_20000.json").bufferedReader()
            .use { bufferReader ->
                val json = bufferReader.use { it.readText() }
                pagerViewModel.parseAndInsertToDB(json)
            }
    }

    private fun setVisibilityMode(mode: String) {
        when (mode) {
            CREATING -> {
                Timber.d("Showing progressbar, notice. Hiding viewpager, tabs")
                binding.pbDbCreating.visibility = View.VISIBLE
                binding.textNotice.visibility = View.VISIBLE
                binding.viewPager.visibility = View.INVISIBLE
                binding.tabLayout.visibility = View.INVISIBLE
            }
            CREATED -> {
                Timber.d("Hiding progressbar, notice. Showing viewpager, tabs")
                binding.pbDbCreating.visibility = View.INVISIBLE
                binding.textNotice.visibility = View.INVISIBLE
                binding.viewPager.visibility = View.VISIBLE
                binding.tabLayout.visibility = View.VISIBLE
            }
        }
    }

    private class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CityListFragment()
            else -> WeatherFragment()
        }

        override fun getItemCount(): Int = 2
    }
}

package com.emikhalets.sunnydayapp.ui.pager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.database.MatrixCursor
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {

    private val LOCATION_PERMISSIONS_REQUEST = 42

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var searchAdapter: SimpleCursorAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState ?: checkCitiesTableState()
        initObservers()
        initSearchView()
        initViewPager()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkCitiesTableState() {
        val sp = requireActivity().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        val isDbCreated = sp.getBoolean(SP_IS_DB_CREATED, false)

        lifecycleScope.launch {
            if (isDbCreated) {
                Timber.d("Cities table in database was created")
                updateInterface(PagerState.Status.DB_CREATED)
            } else {
                startParsingCities()
            }
        }
    }

    private fun startParsingCities() {
        Timber.d("Cities table not exist")
        updateInterface(PagerState.Status.DB_CREATING)
        lifecycleScope.launch {
            val stream = requireContext().assets.open(CITIES_JSON)
            pagerViewModel.parseAndInsertToDB(stream.bufferedReader().readText())
        }
    }

    private fun initObservers() {
        pagerViewModel.dbCreating.observe(viewLifecycleOwner, { dbCreatingObserver(it) })
        pagerViewModel.searchingCities.observe(viewLifecycleOwner, { searchingObserver(it) })
        pagerViewModel.currentQuery.observe(viewLifecycleOwner, { currentQueryObserver(it) })
        pagerViewModel.locationQuery.observe(viewLifecycleOwner, { locationQueryObserver(it) })
        pagerViewModel.currentLocation.observe(viewLifecycleOwner, { locationObserver(it) })

        binding.toolbar.findViewById<View>(R.id.menu_pager_preference).setOnClickListener {
            onSettingsClick()
        }

//        binding.toolbar.findViewById<View>(R.id.menu_pager_recreate_db).setOnClickListener {
//            Timber.d("Recreating database click")
//            setVisibilityMode(CREATING)
//            pagerViewModel.deleteCitiesTable()
//        }
    }

    private fun initViewPager() {
        pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.run {
            adapter = pagerAdapter
            setCurrentItem(1, false)
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_title_city_list)
                1 -> tab.text = getString(R.string.tab_title_current)
            }
            tab.select()
        }.attach()
    }

    private fun dbCreatingObserver(isCreated: Boolean) {
        if (isCreated) {
            lifecycleScope.launch {
                val sp = requireActivity().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
                with(sp.edit()) {
                    putBoolean(SP_IS_DB_CREATED, true)
                    apply()
                }
                updateInterface(PagerState.Status.DB_CREATED)
            }
        } else {
            startParsingCities()
        }
    }

    private fun searchingObserver(results: Array<String>) {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "city_name"))
        for (i in results.indices) cursor.addRow(arrayOf(i, results[i]))
        searchAdapter.changeCursor(cursor)
        searchView.suggestionsAdapter = searchAdapter
    }

    private fun currentQueryObserver(query: String) {
        Timber.d("Query has been updated: ($query)")
        with(binding) {
            toolbar.subtitle = query
            viewPager.setCurrentItem(1, true)
        }
    }

    private fun locationQueryObserver(query: String) {
        Timber.d("Location query has been updated: ($query)")
        binding.toolbar.subtitle = query
    }

    private fun locationObserver(location: Location) {
        Timber.d("Location has been updated: $location")
        val lat = location.latitude
        val lon = location.longitude
        val geo = Geocoder(requireContext(), Locale.getDefault())
        val address = geo.getFromLocation(lat, lon, 1).first()
        val query = "${address.locality}, ${address.countryName}"
        Timber.d("Location query has been updated: ($query)")
        pagerViewModel.updateLocation(lat, lon, query)
    }

    private fun onSettingsClick() {
        Timber.d("Settings Click")
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_viewPagerFragment_to_preferencePagerFragment)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updateInterface(status: PagerState.Status) {
        when (status) {
            PagerState.Status.DB_CREATING -> {
                with(binding) {
                    viewPager.visibility = View.INVISIBLE
                    tabLayout.visibility = View.INVISIBLE
                    textNotice.visibility = View.VISIBLE
                    pbDbCreating.visibility = View.VISIBLE
                }
            }
            PagerState.Status.DB_CREATED -> {
                with(binding) {
                    textNotice.visibility = View.INVISIBLE
                    pbDbCreating.visibility = View.INVISIBLE
                    viewPager.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                }
            }
            PagerState.Status.DB_DELETED -> {
            }
        }
    }

    // SearchView

    private fun initSearchView() {
        searchView = binding.toolbar.menu.findItem(R.id.menu_pager_search).actionView as SearchView
        searchAdapter = SimpleCursorAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            null,
            arrayOf("city_name"),
            intArrayOf(android.R.id.text1),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        searchView.setOnQueryTextListener(searchTextListener())
        searchView.setOnSuggestionListener(searchAdapterListener())
    }

    private fun searchTextListener() = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean = false
        override fun onQueryTextChange(newText: String): Boolean {
            searchAdapter.changeCursor(null)
            if (newText.length >= 2) {
                Timber.d("The search query has been changed: ($newText)")
                pagerViewModel.getCitiesByName(newText)
            }
            return true
        }
    }

    private fun searchAdapterListener() = object : SearchView.OnSuggestionListener {
        override fun onSuggestionSelect(id: Int): Boolean = false
        override fun onSuggestionClick(id: Int): Boolean {
            val cursor = searchView.suggestionsAdapter.getItem(id) as Cursor
            val name = cursor.getString(cursor.getColumnIndex(COL_CITY_NAME))
            binding.toolbar.subtitle = name
            cursor.close()
            searchAdapter.changeCursor(null)

            Timber.d("Select in search: $name")
            searchView.run {
                setQuery(name, false)
                onActionViewCollapsed()
                setQuery(null, false)
            }
            pagerViewModel.run {
                isWeatherLoaded = false
                pagerViewModel.updateCurrentQuery(name)
            }
            return true
        }
    }

    // Location

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (pagerViewModel.checkLocationPermissions()) {
            Timber.d("Location permissions not granted. Request permissions")
            requestPermissions(
                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
                LOCATION_PERMISSIONS_REQUEST
            )
        } else {
            Timber.d("Location permissions was granted. Request location")
            pagerViewModel.requestLocation(fusedLocationClient)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Timber.d("Request Permissions Result")
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Timber.d("Location permissions have been granted")
                    pagerViewModel.requestLocation(fusedLocationClient)
                } else {
                    Timber.d("Location permissions have not been granted")
                    showToast(getString(R.string.city_list_text_location_permissions_not_granted))
                }
            }
        }
    }

    companion object {
        private const val CITIES_JSON = "city_list.json"
        private const val COL_CITY_NAME = "city_name"
        private const val SP_FILE_NAME = "sp_file_name"
        private const val SP_IS_DB_CREATED = "sp_is_database_created"
    }

    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CityListFragment()
            else -> WeatherFragment()
        }

        override fun getItemCount(): Int = 2
    }
}

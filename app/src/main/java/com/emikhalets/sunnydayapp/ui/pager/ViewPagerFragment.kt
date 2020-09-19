package com.emikhalets.sunnydayapp.ui.pager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.database.MatrixCursor
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber
import java.util.*

private const val REQUEST_PERMISSIONS_CODE = 42

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var searchAdapter: SimpleCursorAdapter
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()
    private lateinit var locationManager: LocationManager

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

        savedInstanceState ?: convertCitiesCitiesToDB()

        implementObservers()
        implementListeners()
        implementSearch()

        pagerAdapter = PagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        attachTabsAndPager()

        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            requestPermissions()
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 1000 * 10, 10f
        ) { setLocation(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setLocation(location: Location) {
        if (location.provider == LocationManager.GPS_PROVIDER ||
            location.provider == LocationManager.NETWORK_PROVIDER
        ) {
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

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                ACCESS_COARSE_LOCATION
            )
        ) {
            Timber.d("Displaying permission rationale to provide additional context")
            requestLocationPermission()
        } else {
            Timber.d("Requesting permission")
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_CODE
        )
    }

    private fun implementObservers() {
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

    private fun implementListeners() {
        binding.toolbar.findViewById<View>(R.id.menu_pager_preference).setOnClickListener {
            // TODO: DO IT!!!
            Timber.d("Settings Click")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_viewPagerFragment_to_preferencePagerFragment)
        }
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

    private fun convertCitiesCitiesToDB() {
        val sp = requireContext().getSharedPreferences(getString(R.string.sp_file_name), 0)
        if (sp.getBoolean(getString(R.string.sp_is_first_launch), true)) {
            requireContext().assets.open("cities_20000.json").bufferedReader().use { bufferReader ->
                val json = bufferReader.use { it.readText() }
                pagerViewModel.parseAndInsertToDB(json)
            }
            sp.edit().putBoolean(getString(R.string.sp_is_first_launch), false).apply()
            Timber.d("City database was created.")
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

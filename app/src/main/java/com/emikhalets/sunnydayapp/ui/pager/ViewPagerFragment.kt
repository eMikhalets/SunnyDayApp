package com.emikhalets.sunnydayapp.ui.pager

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.forecast.ForecastFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment
import com.emikhalets.sunnydayapp.utils.FragmentState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
//        initLocation()
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
                Timber.d("Cities table not exist")
                startParsingCities()
            }
        }
    }

    private fun startParsingCities() {
        lifecycleScope.launch(Dispatchers.IO) {
            updateInterface(PagerState.Status.DB_CREATING)
            requireContext().assets.open(CITIES_JSON).bufferedReader().use { reader ->
                val json = reader.readText()
                pagerViewModel.parseAndInsertToDB(json)
            }
        }
    }

    private fun initObservers() {
        pagerViewModel.dbCreating.observe(viewLifecycleOwner, { dbCreatingObserver(it) })
        pagerViewModel.weather.observe(viewLifecycleOwner, { weatherObserver(it) })
        pagerViewModel.error.observe(viewLifecycleOwner, { showToast(it) })

        binding.toolbar.findViewById<View>(R.id.menu_pager_preference).setOnClickListener {
            onSettingsClick()
        }
    }

    private fun initViewPager() {
        pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_title_city_list)
                1 -> tab.text = getString(R.string.tab_title_current)
                2 -> tab.text = getString(R.string.tab_title_forecast)
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

    private fun weatherObserver(state: FragmentState<Response>) {
        when (state.status) {
            FragmentState.Status.LOADING -> {
            }
            FragmentState.Status.LOADED -> {
                with(binding) {
                    toolbar.subtitle = pagerViewModel.currentCity
                    viewPager.setCurrentItem(1, true)
                }
            }
            FragmentState.Status.ERROR -> {
            }
        }
    }

    private fun locationObserver(location: Location) {
        Timber.d("Location has been updated: $location")
        val lat = location.latitude
        val lon = location.longitude
        val geo = Geocoder(requireContext(), Locale.getDefault())
        val address = geo.getFromLocation(lat, lon, 1).first()
        val query = "${address.locality}, ${address.countryName}"
        Timber.d("Location query has been updated: ($query)")
//        pagerViewModel.updateLocation(lat, lon, query)
    }

    private fun onSettingsClick() {
        Timber.d("Settings Click")
        this.findNavController().navigate(R.id.action_viewPagerFragment_to_preferencePagerFragment)
    }

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

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
        searchView.setOnQueryTextListener(searchTextListener())
        searchView.setOnCloseListener(searchCloseListener())
    }

    private fun searchTextListener() = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean = false
        override fun onQueryTextChange(newText: String): Boolean {
            pagerViewModel.searchCitiesInDb(newText)
            return true
        }
    }

    // TODO: need to click on close twice if text was typed
    private fun searchCloseListener() = SearchView.OnCloseListener {
        pagerViewModel.cancelSearchingCities()
        searchView.onActionViewCollapsed()
        true
    }

    // Location

//    private fun initLocation() {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//        if (pagerViewModel.checkLocationPermissions()) {
//            Timber.d("Location permissions not granted. Request permissions")
//            requestPermissions(
//                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSIONS_REQUEST
//            )
//        } else {
//            Timber.d("Location permissions was granted. Request location")
//            pagerViewModel.requestLocation(fusedLocationClient)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        Timber.d("Request Permissions Result")
//        when (requestCode) {
//            LOCATION_PERMISSIONS_REQUEST -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
//                    Timber.d("Location permissions have been granted")
//                    pagerViewModel.requestLocation(fusedLocationClient)
//                } else {
//                    Timber.d("Location permissions have not been granted")
//                    showToast(getString(R.string.city_list_text_location_permissions_not_granted))
//                }
//            }
//        }
//    }

    companion object {
        private const val CITIES_JSON = "city_list_min.json"
        private const val SP_FILE_NAME = "sp_file_name"
        private const val SP_IS_DB_CREATED = "sp_is_database_created"
        private const val FRAGMENTS_COUNT = 3
    }

    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CityListFragment()
            1 -> WeatherFragment()
            2 -> ForecastFragment()
            else -> CityListFragment()
        }

        override fun getItemCount(): Int = FRAGMENTS_COUNT
    }
}

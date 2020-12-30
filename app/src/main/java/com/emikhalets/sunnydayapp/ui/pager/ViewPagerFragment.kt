package com.emikhalets.sunnydayapp.ui.pager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.forecast.ForecastFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment
import com.emikhalets.sunnydayapp.utils.FragmentState
import com.emikhalets.sunnydayapp.utils.getCityFromLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO: settings fragment and recreating if popBackStack()
//TODO: units (just metric of imperial for request)
//TODO: language
@AndroidEntryPoint
class ViewPagerFragment : Fragment() {

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
        pagerViewModel.error.observe(viewLifecycleOwner, { showToast(it) })
        pagerViewModel.weather.observe(viewLifecycleOwner, { weatherObserver(it) })
        pagerViewModel.dbCreating.observe(viewLifecycleOwner, { dbCreatingObserver(it) })
        pagerViewModel.userLocation.observe(viewLifecycleOwner, { locationObserver(it) })
        pagerViewModel.selectSearching.observe(viewLifecycleOwner, { selectSearchingObserver() })

        pagerViewModel.hourlyScrollCallback.observe(viewLifecycleOwner, { isLetScroll ->
            binding.viewPager.isUserInputEnabled = isLetScroll
        })

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
                with(binding) {
                    searchView.onActionViewCollapsed()
                    toolbar.subtitle = pagerViewModel.currentCity
                    if (viewPager.currentItem == 0) {
                        viewPager.setCurrentItem(1, true)
                    }
                }
            }
            FragmentState.Status.LOADED -> {
            }
            FragmentState.Status.ERROR -> {
            }
        }
    }

    private fun selectSearchingObserver() {
        searchView.onActionViewCollapsed()
    }

    private fun locationObserver(location: Location) {
        pagerViewModel.currentCity = getCityFromLocation(requireContext(), location)
        with(binding) {
            if (viewPager.currentItem == 0) {
                viewPager.setCurrentItem(1, true)
            }
        }
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

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (!checkLocationPermissions()) {
            Timber.d("Location permissions not granted. Request permissions")
            requestPermissions(PERMISSIONS_ARRAY, LOCATION_PERMISSIONS_REQUEST)
        } else {
            Timber.d("Location permissions was granted. Request location")
            requestLocation()
        }
    }

    private fun checkLocationPermissions(): Boolean = ContextCompat.checkSelfPermission(
        requireContext(), ACCESS_COARSE_LOCATION
    ) == PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        requireContext(), ACCESS_FINE_LOCATION
    ) == PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun requestLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let { pagerViewModel.userLocation.postValue(it) }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSIONS_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                Timber.d("Location permissions have been granted")
                requestLocation()
            } else {
                Timber.d("Location permissions have not been granted")
                showToast(getString(R.string.cities_text_location_permissions_not_granted))
            }
        }
    }

    companion object {
        private val PERMISSIONS_ARRAY = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
        private const val LOCATION_PERMISSIONS_REQUEST = 42
        private const val FRAGMENTS_COUNT = 3
        private const val SP_IS_DB_CREATED = "sp_is_database_created"
        private const val CITIES_JSON = "city_list_min.json"
        private const val SP_FILE_NAME = "sp_file_name"
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

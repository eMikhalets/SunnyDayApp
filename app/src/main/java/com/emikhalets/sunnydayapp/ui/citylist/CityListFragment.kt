package com.emikhalets.sunnydayapp.ui.citylist

import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.data.model.Hourly
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.ui.preference.PreferencePagerFragment
import com.emikhalets.sunnydayapp.utils.getCityFromLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_city_list.*
import timber.log.Timber

@AndroidEntryPoint
class CityListFragment : Fragment(), CitiesAdapter.OnCityClick,
    DeleteCityDialog.DeleteCityListener {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private val cityListViewModel: CityListViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private lateinit var citiesAdapter: CitiesAdapter
    private lateinit var pref: SharedPreferences
    private lateinit var prefLang: String
    private lateinit var prefUnits: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initPreferences()
        initCitiesAdapter()

        if (savedInstanceState == null) {
            cityListViewModel.getSearchedCities()
            binding.textLocationCity.text = getString(
                R.string.cities_text_location_not_determined
            )
        }

        val testForecast = mutableListOf(
            Hourly(0, 10.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 11.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 13.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 17.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 19.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 20.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 18.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 14.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 12.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
            Hourly(0, 13.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, listOf(), 0.0),
        )

        binding.chartHourly.hourlyForecast = testForecast
        binding.chartHourly.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initCitiesAdapter() {
        citiesAdapter = CitiesAdapter(this)
        binding.listCities.adapter = citiesAdapter
    }

    private fun initObservers() {
        cityListViewModel.searchedCities.observe(viewLifecycleOwner, { searchedCitiesObserver(it) })

        pagerViewModel.userLocation.observe(viewLifecycleOwner, { locationObserver(it) })
        pagerViewModel.searchingCities.observe(viewLifecycleOwner, { searchingCitiesObserver(it) })

        binding.textLocationCity.setOnClickListener { onTextLocationClick() }
    }

    private fun initPreferences() {
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefLang = pref.getString(PreferencePagerFragment.KEY_PREF_LANG, "en")!!
        prefUnits = pref.getString(PreferencePagerFragment.KEY_PREF_UNITS, "metric")!!
    }

    /**
     * Cities that were once searched for in a toolbar.
     */
    private fun searchedCitiesObserver(cities: List<City>) {
        if (cities.isNotEmpty()) motion_cities.transitionToEnd()
        citiesAdapter.submitList(cities)
    }

    /**
     * Cities that are searched for in the toolbar
     */
    private fun searchingCitiesObserver(cities: List<City>) {
        if (cities.isNotEmpty()) {
            citiesAdapter.isSearchingState = true
            motion_cities.transitionToEnd()
            citiesAdapter.submitList(cities)
        } else {
            citiesAdapter.isSearchingState = false
            val list: List<City>? = cityListViewModel.searchedCities.value
            if (list == null || list.isEmpty()) motion_cities.transitionToStart()
            citiesAdapter.submitList(list)
        }
    }

    private fun locationObserver(location: Location) {
        binding.textLocationCity.text = getCityFromLocation(requireContext(), location)
    }

    private fun onTextLocationClick() {
        Timber.d("Clicked the current location in the list of cities")
        pagerViewModel.userLocation.value?.let { location ->
            pagerViewModel.sendWeatherRequest(
                location.latitude,
                location.longitude,
                prefUnits,
                prefLang
            )
        } ?: showToast(getString(R.string.cities_text_location_not_determined))
    }

    /**
     * CitiesAdapter item click listener.
     * If searching state -> Checked searched state of city.
     * Else-> Sets current city name for weather data.
     * Sending request. Inserting city in searched story.
     * Change cities list adapter searching state if need.
     */
    override fun onCityClick(city: City) {
        Timber.d("Clicked (${city.name}) in the list of cities")
        Timber.d("Searching state is '${citiesAdapter.isSearchingState}'")

        if (!citiesAdapter.isSearchingState) {
            pagerViewModel.apply {
                currentCity = "${city.name}, ${city.country}"
                sendWeatherRequest(city.lat, city.lon, prefUnits, prefLang)
            }
            citiesAdapter.isSearchingState = false
        } else {
            pagerViewModel.selectSearchingCity(city)
            cityListViewModel.checkIsSearched(city)
        }
    }

    /**
     * CitiesAdapter item long click listener
     */
    override fun onCityLongClick(city: City) {
        Timber.d("Deleting ($city)")
        val dialog = DeleteCityDialog(city, this)
        dialog.show(requireActivity().supportFragmentManager, KEY_DIALOG_DELETE_CITY)
    }

    /**
     * Delete dialog click listener
     */
    override fun onDeleteCity(city: City) = cityListViewModel.removeCityFromSearched(city)

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    companion object {
        private const val KEY_DIALOG_DELETE_CITY = "key_dialog_delete_city"
    }
}
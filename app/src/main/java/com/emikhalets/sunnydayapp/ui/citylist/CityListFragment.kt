package com.emikhalets.sunnydayapp.ui.citylist

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initCitiesAdapter()

        if (savedInstanceState == null) {
            cityListViewModel.getSearchedCities()
            binding.textLocationCity.text = getString(
                R.string.cities_text_location_not_determined
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initCitiesAdapter() {
        citiesAdapter = CitiesAdapter(this)
        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)
        binding.listCities.run {
            addItemDecoration(divider)
            adapter = citiesAdapter
        }
    }

    private fun initObservers() {
        cityListViewModel.searchedCities.observe(viewLifecycleOwner, { searchedCitiesObserver(it) })

        pagerViewModel.searchingCities.observe(viewLifecycleOwner, { searchingCitiesObserver(it) })
        pagerViewModel.userLocation.observe(viewLifecycleOwner, { locationObserver(it) })

        binding.textLocationCity.setOnClickListener { onTextLocationClick() }
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
    //TODO: если после поиска стереть поисковой запрос (вернется пустой список)
    // и если список городов в истории поиска пустой, то текстовая вью
    // с тестом "пустой список" не отобразится
    private fun searchingCitiesObserver(cities: List<City>) {
        if (cities.isNotEmpty()) {
            motion_cities.transitionToEnd()
            citiesAdapter.submitList(cities)
            citiesAdapter.isSearchingState = true
        } else {
            val list: List<City>? = cityListViewModel.searchedCities.value
            if (list == null || list.isEmpty()) motion_cities.transitionToStart()
            citiesAdapter.submitList(list)
            citiesAdapter.isSearchingState = false
        }
    }

    // TODO: get place name by coordinates and set in text view
    private fun locationObserver(location: Location) {
//        binding.textLocationCity.text =
    }

    // TODO: change "your location" on city name
    private fun onTextLocationClick() {
        Timber.d("Clicked the current location in the list of cities")
        pagerViewModel.userLocation.value?.let { location ->
            pagerViewModel.currentCity = getString(R.string.app_your_location)
            pagerViewModel.sendWeatherRequest(location.latitude, location.longitude)
        } ?: showToast(getString(R.string.cities_text_location_not_determined))
    }

    /**
     * CitiesAdapter item click listener.
     * Sets current city name for weather data.
     * Sending request. Inserting city in searched story.
     * Change cities list adapter searching state if need.
     */
    override fun onCityClick(city: City) {
        Timber.d("Clicked (${city.name}) in the list of cities")
        cityListViewModel.checkIsSearched(city)
        pagerViewModel.apply {
            currentCity = "${city.name}, ${city.country}"
            sendWeatherRequest(city.lat, city.lon)
        }

        if (citiesAdapter.isSearchingState) {
            citiesAdapter.submitList(cityListViewModel.searchedCities.value)
            citiesAdapter.isSearchingState = false
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
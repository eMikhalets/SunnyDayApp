package com.emikhalets.sunnydayapp.ui.citylist

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.ui.MainViewModel
import com.emikhalets.sunnydayapp.utils.State
import com.emikhalets.sunnydayapp.utils.getCityFromLocation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityListFragment : Fragment(), DeleteCityDialog.DeleteCityListener {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private val citiesViewModel: CityListViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var citiesAdapter: CitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        citiesAdapter = CitiesAdapter({ onCityClick(it) }, { onCityLongClick(it) })
        binding.listCities.adapter = citiesAdapter
        loadCities()
        citiesViewModel.savedCities.observe(viewLifecycleOwner) { savedCitiesObserver(it) }
        mainViewModel.location.observe(viewLifecycleOwner) { locationObserver(it) }
        mainViewModel.searching.observe(viewLifecycleOwner) { searchingObserver(it) }
        binding.textLocationCity.setOnClickListener { onLocationCityClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadCities() {
        updateInterface(State.LOADING)
        citiesViewModel.getSearchedCities()
    }

    private fun savedCitiesObserver(cities: List<City>) {
        citiesAdapter.submitList(null)
        updateInterface(State.LOADED)
        citiesAdapter.submitList(cities)
    }

    private fun locationObserver(location: Location) {
        binding.textLocationCity.text = getCityFromLocation(requireContext(), location)
    }

    private fun searchingObserver(cities: List<City>) {
        if (cities.isNotEmpty()) {
            updateInterface(State.LOADED)
            citiesAdapter.submitList(cities)
        } else {
            updateInterface(State.LOADING)
            citiesViewModel.getSearchedCities()
        }
    }

    private fun onLocationCityClick() {
        mainViewModel.isWeatherLoaded = false
        mainViewModel.selecting.value = null
        mainViewModel.location.value?.let {
            mainViewModel.sendWeatherRequest(it.latitude, it.longitude)
        }
    }

    private fun onCityClick(city: City) {
        mainViewModel.isWeatherLoaded = false
        mainViewModel.selecting.value = city
        citiesViewModel.saveCity(city)
        mainViewModel.apply {
            currentCity = "${city.name}, ${city.country}"
            sendWeatherRequest(city.lat, city.lon)
        }
    }

    private fun onCityLongClick(city: City) {
        val dialog = DeleteCityDialog(city, this)
        dialog.show(requireActivity().supportFragmentManager, KEY_DIALOG_DELETE_CITY)
    }

    override fun onDeleteCity(city: City) {
        citiesViewModel.removeCityFromSaved(city)
    }

    private fun updateInterface(state: State) {
        val duration = 500L
        with(binding) {
            when (state) {
                State.LOADING -> {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbSearching.animate().alpha(1f).setDuration(duration).start()
                    listCities.animate().alpha(0f).setDuration(duration).start()
                }
                State.LOADED -> {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbSearching.animate().alpha(0f).setDuration(duration).start()
                    listCities.animate().alpha(1f).setDuration(duration).start()
                }
                State.ERROR -> {
                    textNotice.animate().alpha(1f).setDuration(duration).start()
                    pbSearching.animate().alpha(0f).setDuration(duration).start()
                    listCities.animate().alpha(0f).setDuration(duration).start()
                }
            }
        }
    }

    companion object {
        private const val KEY_DIALOG_DELETE_CITY = "key_dialog_delete_city"
    }
}
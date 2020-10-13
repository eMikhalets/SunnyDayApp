package com.emikhalets.sunnydayapp.ui.citylist

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
import com.emikhalets.sunnydayapp.adapters.CitiesAdapter
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.CitiesListStatus
import com.emikhalets.sunnydayapp.utils.LocationHandler
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CityListFragment : Fragment(), CitiesAdapter.CityClick {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CityListViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setClickListeners()

        if (savedInstanceState == null) viewModel.getAddedCities()

        binding.textLocationCity.text = getString(R.string.city_list_text_location_not_determined)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setObservers() {
        viewModel.addedCities.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Timber.d("The list of added cities has been updated")
                setCitiesAdapter(it)
                setVisibilityMode(CitiesListStatus.CITIES)
            } else {
                Timber.d("The list of added cities is empty")
                setVisibilityMode(CitiesListStatus.NOTICE)
            }
        })

        pagerViewModel.addedCity.observe(viewLifecycleOwner, {
            Timber.d("($it) added to the list")
            updateCitiesList()
        })

        pagerViewModel.locationQuery.observe(viewLifecycleOwner, {
            Timber.d("Location query has been updated: ($it)")
            binding.textLocationCity.text = it
        })
    }

    private fun setClickListeners() {
        binding.textLocationCity.setOnClickListener {
            // TODO: change page to weather fragment when clicked
            Timber.d("Clicked the current location in the list of cities")
            val lat = pagerViewModel.location.value?.get(0)
            val lon = pagerViewModel.location.value?.get(1)

            if (lat != null && lon != null) {
                val query = LocationHandler.getCityAndCountry(requireContext(), lat, lon)
                Timber.d("Location query has been updated: ($query)")
                pagerViewModel.updateLocation(lat, lon, query)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.city_list_text_location_not_determined),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setCitiesAdapter(list: List<City>) {
        val citiesAdapter = CitiesAdapter(this)
        val citiesLayoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), citiesLayoutManager.orientation)
        binding.listCities.run {
            layoutManager = citiesLayoutManager
            addItemDecoration(divider)
            adapter = citiesAdapter
        }
        citiesAdapter.submitList(list)
    }


    override fun onCityClick(city: City) {
        Timber.d("Clicked ($city) in the list of cities")
        pagerViewModel.isWeatherLoaded = false
        pagerViewModel.updateCurrentQuery(city.getQuery())
    }

    override fun onCityLongClick(city: City) {
        // TODO: Add dialog for delete or implement itemTouchListener
        Timber.d("Delete ($city)")
        viewModel.deleteCity(city)
        updateCitiesList()
    }

    private fun updateCitiesList() {
        viewModel.getAddedCities()
        setVisibilityMode(CitiesListStatus.LOADING)
    }

    private fun setVisibilityMode(status: CitiesListStatus) {
        val durationMills = 500L
        when (status) {
            CitiesListStatus.CITIES -> {
                binding.textNotice.animate().alpha(0f).setDuration(durationMills).start()
                binding.pbLoadingCities.animate().alpha(0f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(1f).setDuration(durationMills).start()
            }
            CitiesListStatus.LOADING -> {
                binding.textNotice.animate().alpha(0f).setDuration(durationMills).start()
                binding.pbLoadingCities.animate().alpha(1f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(0f).setDuration(durationMills).start()
            }
            CitiesListStatus.NOTICE -> {
                binding.textNotice.animate().alpha(1f).setDuration(durationMills).start()
                binding.pbLoadingCities.animate().alpha(0f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(0f).setDuration(durationMills).start()
            }
        }
    }
}
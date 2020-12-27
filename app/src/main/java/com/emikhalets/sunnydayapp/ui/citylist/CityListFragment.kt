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
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CityListFragment : Fragment(), DailyAdapter.CityClick, DeleteCityDialog.DeleteCityListener {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private val cityListViewModel: CityListViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private lateinit var citiesAdapter: DailyAdapter

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
            cityListViewModel.getAddedCities()
            binding.textLocationCity.text =
                getString(R.string.city_list_text_location_not_determined)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initCitiesAdapter() {
        citiesAdapter = DailyAdapter(this)
        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)
        binding.listCities.run {
            addItemDecoration(divider)
            adapter = citiesAdapter
        }
    }

    private fun initObservers() {
        cityListViewModel.addedCities.observe(viewLifecycleOwner, { addedCitiesObserver(it) })
        pagerViewModel.addedCity.observe(viewLifecycleOwner, { addedCityObserver(it) })
        pagerViewModel.locationQuery.observe(viewLifecycleOwner, { locationQueryObserver(it) })

        binding.textLocationCity.setOnClickListener { onTextLocationClick() }
    }

    // LiveData observers

    private fun addedCitiesObserver(state: ForecastState<List<City>>) {
        when (state.status) {
            ForecastState.Status.LOADING -> {
            }
            ForecastState.Status.EMPTY -> {
            }
            ForecastState.Status.CITIES -> {
                citiesAdapter.submitList(state.data)
            }
            ForecastState.Status.ERROR -> {
                binding.textNotice.text = state.error
            }
        }

        setVisibilityState(state.status)
    }

    private fun addedCityObserver(name: String) {
        Timber.d("($name) added to the list")
        cityListViewModel.getAddedCities()
    }

    private fun locationQueryObserver(name: String) {
        Timber.d("Location query has been updated: ($name)")
        binding.textLocationCity.text = name
    }

    // Click listeners

    private fun onTextLocationClick() {
        Timber.d("Clicked the current location in the list of cities")
        val lat = pagerViewModel.location.value?.get(0)
        val lon = pagerViewModel.location.value?.get(1)

        if (lat != null && lon != null) {
            val query = pagerViewModel.getCityAndCountry(lat, lon)
            Timber.d("Location query has been updated: ($query)")
            pagerViewModel.updateLocation(lat, lon, query)
        } else {
            showToast(getString(R.string.city_list_text_location_not_determined))
        }
    }

    /**
     * CitiesAdapter item click listener
     */
    override fun onCityClick(city: City) {
        Timber.d("Clicked ($city) in the list of cities")
        pagerViewModel.isWeatherLoaded = false
        pagerViewModel.updateCurrentQuery(city.getQuery())
    }

    /**
     * CitiesAdapter item long click listener
     */
    override fun onCityLongClick(city: City) {
        Timber.d("Delete ($city)")
        val dialog = DeleteCityDialog(city, this)
        dialog.show(
            requireActivity().supportFragmentManager,
            getString(R.string.key_dialog_delete_city)
        )
    }

    /**
     * Delete dialog click listener
     */
    override fun onDeleteCity(city: City) {
        cityListViewModel.deleteCity(city)
    }

    private fun setVisibilityState(status: ForecastState.Status) {
        when (status) {
            ForecastState.Status.LOADING -> {
                with(binding) {
                    textNotice.visibility = View.INVISIBLE
                    listCities.visibility = View.INVISIBLE
                    pbLoadingCities.visibility = View.VISIBLE
                }
            }
            ForecastState.Status.EMPTY, ForecastState.Status.ERROR -> {
                with(binding) {
                    pbLoadingCities.visibility = View.INVISIBLE
                    textNotice.visibility = View.VISIBLE
                }
            }
            ForecastState.Status.CITIES -> {
                with(binding) {
                    pbLoadingCities.visibility = View.INVISIBLE
                    listCities.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
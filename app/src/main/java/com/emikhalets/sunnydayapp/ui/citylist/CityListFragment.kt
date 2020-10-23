package com.emikhalets.sunnydayapp.ui.citylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.ToastBuilder
import com.emikhalets.sunnydayapp.utils.status.CitiesResource
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CityListFragment : Fragment(), CitiesAdapter.CityClick, DeleteCityDialog.DeleteCityListener {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CityListViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private lateinit var citiesAdapter: CitiesAdapter

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
        setListeners()
        initCitiesAdapter()
        viewModel.getAddedCities()
        binding.textLocationCity.text = getString(R.string.city_list_text_location_not_determined)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setObservers() {
        viewModel.addedCities.observe(viewLifecycleOwner, {
            if (it.status == CitiesResource.Status.CITIES) citiesAdapter.submitList(it.data)
            setVisibilityMode(it.status)
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

    private fun setListeners() {
        binding.textLocationCity.setOnClickListener {
            // TODO: change fragment to weather fragment when clicked
            Timber.d("Clicked the current location in the list of cities")
            val lat = pagerViewModel.location.value?.get(0)
            val lon = pagerViewModel.location.value?.get(1)

            if (lat != null && lon != null) {
                val query = pagerViewModel.getCityAndCountry(lat, lon)
                Timber.d("Location query has been updated: ($query)")
                pagerViewModel.updateLocation(lat, lon, query)
            } else {
                ToastBuilder.build(getString(R.string.city_list_text_location_not_determined))
            }
        }
    }

    private fun initCitiesAdapter() {
        citiesAdapter = CitiesAdapter(this)
        val citiesLayoutManager = LinearLayoutManager(requireContext())
        val divider =
            DividerItemDecoration(requireContext(), citiesLayoutManager.orientation)
        binding.listCities.run {
            layoutManager = citiesLayoutManager
            addItemDecoration(divider)
            adapter = citiesAdapter
        }
    }

    override fun onCityClick(city: City) {
        Timber.d("Clicked ($city) in the list of cities")
        pagerViewModel.isWeatherLoaded = false
        pagerViewModel.updateCurrentQuery(city.getQuery())
    }

    override fun onCityLongClick(city: City) {
        Timber.d("Delete ($city)")
        val dialog = DeleteCityDialog(city, this)
        dialog.show(
            requireActivity().supportFragmentManager,
            getString(R.string.key_dialog_delete_city)
        )
    }

    override fun onDeleteCity(city: City) {
        viewModel.deleteCity(city)
    }

    private fun updateCitiesList() {
        setVisibilityMode(CitiesResource.Status.LOADING)
        viewModel.getAddedCities()
    }

    private fun setVisibilityMode(status: CitiesResource.Status) {
        val durationMills = 500L
        when (status) {
            CitiesResource.Status.CITIES -> {
                binding.textNotice.animate().alpha(0f).setDuration(durationMills).start()
                binding.pbLoadingCities.animate().alpha(0f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(1f).setDuration(durationMills).start()
            }
            CitiesResource.Status.LOADING -> {
                binding.textNotice.animate().alpha(0f).setDuration(durationMills).start()
                binding.pbLoadingCities.animate().alpha(1f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(0f).setDuration(durationMills).start()
            }
            CitiesResource.Status.EMPTY -> {
                binding.textNotice.animate().alpha(1f).setDuration(durationMills).start()
                binding.pbLoadingCities.animate().alpha(0f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(0f).setDuration(durationMills).start()
            }
        }
    }
}
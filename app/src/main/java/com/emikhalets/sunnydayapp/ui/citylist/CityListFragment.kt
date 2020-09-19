package com.emikhalets.sunnydayapp.ui.citylist

import android.location.Geocoder
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
import timber.log.Timber
import java.util.*

private const val CITIES = "CITIES"
private const val NOTICE = "NOTICE"

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
        implementObservers()
        if (savedInstanceState == null) viewModel.getAddedCities()
        binding.textLocationCity.text = getString(R.string.city_list_text_location_not_determined)
        binding.textLocationCity.setOnClickListener {
            // TODO: change page to weather fragment when clicked
            Timber.d("Clicked the current location in the list of cities")
            val lat = pagerViewModel.location.value?.get(0)
            val lon = pagerViewModel.location.value?.get(1)
            if (lat != null && lon != null) {
                val geo = Geocoder(requireContext(), Locale.getDefault())
                val address = geo.getFromLocation(lat, lon, 1).first()
                val query = "${address.locality}, ${address.countryName}"
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun implementObservers() {
        viewModel.addedCities.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Timber.d("The list of added cities has been updated")
                val citiesAdapter = CitiesAdapter(this)
                val layoutManager = LinearLayoutManager(requireContext())
                val divider = DividerItemDecoration(requireContext(), layoutManager.orientation)
                binding.listCities.layoutManager = layoutManager
                binding.listCities.addItemDecoration(divider)
                binding.listCities.adapter = citiesAdapter
                citiesAdapter.submitList(it)
                setVisibilityMode(CITIES)
            } else {
                Timber.d("The list of added cities is empty")
                setVisibilityMode(NOTICE)
            }
        })

        pagerViewModel.addedCity.observe(viewLifecycleOwner, {
            Timber.d("($it) added to the list")
            viewModel.getAddedCities()
        })

        pagerViewModel.locationQuery.observe(viewLifecycleOwner, {
            Timber.d("Location query has been updated: ($it)")
            binding.textLocationCity.text = it
        })
    }

    override fun onCityClick(city: City) {
        Timber.d("Clicked ($city) in the list of cities")
        pagerViewModel.updateCurrentQuery(city.getQuery())
    }

    override fun onCityLongClick(city: City) {
        // TODO: Add dialog for delete or implement itemTouchListener
        Timber.d("Delete ($city)")
        viewModel.deleteCity(city)
        viewModel.getAddedCities()
    }

    private fun setVisibilityMode(mode: String) {
        val durationMills = 500L
        when (mode) {
            CITIES -> {
                binding.textNotice.animate().alpha(0f).setDuration(durationMills).start()
                binding.textLocationCity.animate().alpha(1f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(1f).setDuration(durationMills).start()
            }
            else -> {
                binding.textNotice.animate().alpha(1f).setDuration(durationMills).start()
                binding.textLocationCity.animate().alpha(0f).setDuration(durationMills).start()
                binding.listCities.animate().alpha(0f).setDuration(durationMills).start()
            }
        }
    }
}
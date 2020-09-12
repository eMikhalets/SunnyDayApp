package com.emikhalets.sunnydayapp.ui.citylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.adapters.CitiesAdapter
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import timber.log.Timber

class CityListFragment : Fragment(), CitiesAdapter.CityClick {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private lateinit var citiesAdapter: CitiesAdapter
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
        // TODO: TEMP
        binding.textLocationCity.text = getString(R.string.city_list_text_your_location)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun implementObservers() {
        viewModel.addedCities.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                citiesAdapter = CitiesAdapter(this)
                citiesAdapter.submitList(it)
                binding.listCities.adapter = citiesAdapter
                binding.listCities.visibility = View.VISIBLE
                binding.textEmptyList.visibility = View.INVISIBLE
                binding.cardLocationCity.visibility = View.VISIBLE
            } else {
                binding.listCities.visibility = View.INVISIBLE
                binding.textEmptyList.visibility = View.VISIBLE
                binding.cardLocationCity.visibility = View.INVISIBLE
            }
        })

        pagerViewModel.addedCity.observe(
            viewLifecycleOwner, { viewModel.getAddedCities() }
        )
    }

    override fun onCityClick(city: City) {
        Timber.d("Clicked: $city")
        pagerViewModel.updateCurrentQuery(city.getQuery())
        Timber.d("Query updated: ${city.getQuery()}")
    }

    override fun onCityLongClick(city: City) {
        // TODO: Add dialog for delete or implement itemTouchListener
        Timber.d("Delete: $city")
        viewModel.deleteCity(city)
        viewModel.getAddedCities()
    }
}
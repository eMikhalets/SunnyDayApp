package com.emikhalets.sunnydayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emikhalets.sunnydayapp.adapters.CitiesAdapter
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.utils.ADDED_CITY
import com.emikhalets.sunnydayapp.utils.CURRENT_QUERY
import com.emikhalets.sunnydayapp.viewmodels.CityListViewModel

class CityListFragment : Fragment(), CitiesAdapter.OnCityClickListener {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CityListViewModel
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
        viewModel = ViewModelProvider(this).get(CityListViewModel::class.java)
        citiesAdapter = CitiesAdapter(ArrayList(), this)
        viewModel.cities.observe(viewLifecycleOwner, Observer { citiesObserver(it) })
        ADDED_CITY.observe(viewLifecycleOwner, Observer { viewModel.getAddedCities() })
        binding.listCities.adapter = citiesAdapter
        viewModel.getAddedCities()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCityClick(city: City) {
        CURRENT_QUERY.value = city.getQuery()
    }

    override fun onCityLongClick(city: City) {
        // TODO: Add dialog for delete or implement itemTouchListener
        viewModel.deleteCity(city)
    }

    private fun citiesObserver(cities: List<City>) {
        citiesAdapter.setList(cities)
        if (cities.isNotEmpty()) {
            hideTextEmptyList()
            showCitiesList()
        } else {
            hideCitiesList()
            showTextEmptyList()
        }
    }

    private fun showTextEmptyList() {
        binding.textEmptyList.visibility = View.VISIBLE
    }

    private fun hideTextEmptyList() {
        binding.textEmptyList.visibility = View.INVISIBLE
    }

    private fun showCitiesList() {
        binding.listCities.visibility = View.VISIBLE
    }

    private fun hideCitiesList() {
        binding.listCities.visibility = View.INVISIBLE
    }
}
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
import com.emikhalets.sunnydayapp.viewmodels.CityListViewModel

class CityListFragment : Fragment() {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CityListViewModel
    private val citiesAdapter = CitiesAdapter(ArrayList())

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
        viewModel.cities.observe(viewLifecycleOwner, Observer { citiesObserver(it) })
        binding.listCities.adapter = citiesAdapter
        //viewModel.getAllCities()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
package com.emikhalets.sunnydayapp.ui.citylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.adapters.CitiesAdapter
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.databinding.FragmentCityListBinding
import com.emikhalets.sunnydayapp.utils.ADDED_CITY
import com.emikhalets.sunnydayapp.utils.CURRENT_QUERY
import timber.log.Timber

class CityListFragment : Fragment(), CitiesAdapter.OnCityClickListener {

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private lateinit var citiesAdapter: CitiesAdapter
    private lateinit var viewModel: CityListViewModel

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
        viewModel.addedCities.observe(viewLifecycleOwner, Observer { citiesObserver(it) })
        // LiveData for added cities in cityList
        ADDED_CITY.observe(viewLifecycleOwner, Observer { viewModel.getAddedCities() })
        binding.listCities.adapter = citiesAdapter
        viewModel.getAddedCities()

        binding.textLocationCity.text = getString(R.string.city_list_text_your_location)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCityClick(city: City) {
        Timber.d("Clicked: $city")
        CURRENT_QUERY.value = city.getQuery()
        Timber.d("Query updated: ${city.getQuery()}")
    }

    override fun onCityLongClick(city: City) {
        // TODO: Add dialog for delete or implement itemTouchListener
        Timber.d("Delete: $city")
        viewModel.deleteCity(city)
    }

    private fun citiesObserver(cities: List<City>) {
        citiesAdapter.setList(cities)
        if (cities.isNotEmpty()) {
            hideTextEmptyList()
            showLocationCity()
            showCitiesList()
            Timber.d("Cities list:")
            cities.forEach { Timber.d(it.toString()) }
        } else {
            hideCitiesList()
            hideLocationCity()
            showTextEmptyList()
            Timber.d("Cities list is empty")
        }
    }

    private fun showTextEmptyList() {
        binding.textEmptyList.visibility = View.VISIBLE
    }

    private fun hideTextEmptyList() {
        binding.textEmptyList.visibility = View.INVISIBLE
    }

    private fun showLocationCity() {
        binding.cardLocationCity.visibility = View.VISIBLE
    }

    private fun hideLocationCity() {
        binding.cardLocationCity.visibility = View.INVISIBLE
    }

    private fun showCitiesList() {
        binding.listCities.visibility = View.VISIBLE
    }

    private fun hideCitiesList() {
        binding.listCities.visibility = View.INVISIBLE
    }
}
package com.emikhalets.sunnydayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emikhalets.sunnydayapp.adapters.DailyAdapter
import com.emikhalets.sunnydayapp.databinding.FragmentForecastDailyBinding
import com.emikhalets.sunnydayapp.viewmodels.ForecastDailyViewModel

class ForecastDailyFragment : Fragment() {

    private var _binding: FragmentForecastDailyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelForecast: ForecastDailyViewModel
    private lateinit var adapter: DailyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForecastDailyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelForecast = ViewModelProvider(this).get(ForecastDailyViewModel::class.java)
        adapter = DailyAdapter(ArrayList())
        binding.listForecastDaily.adapter = adapter
        //viewModelForecast.requestForecastDaily("Москва")
        viewModelForecast.forecastDaily.observe(viewLifecycleOwner, Observer {
            adapter.setList(it.data)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
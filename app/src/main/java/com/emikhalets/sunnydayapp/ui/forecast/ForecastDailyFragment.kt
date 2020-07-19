package com.emikhalets.sunnydayapp.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emikhalets.sunnydayapp.adapters.DailyAdapter
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.databinding.FragmentForecastDailyBinding
import com.emikhalets.sunnydayapp.utils.CURRENT_QUERY

class ForecastDailyFragment : Fragment() {

    private var _binding: FragmentForecastDailyBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DailyAdapter
    private lateinit var viewModel: ForecastDailyViewModel

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
        viewModel = ViewModelProvider(this).get(ForecastDailyViewModel::class.java)
        adapter = DailyAdapter(ArrayList())
        binding.listForecastDaily.adapter = adapter
        CURRENT_QUERY.observe(viewLifecycleOwner, Observer { queryObserver(it) })
        viewModel.forecastDaily.observe(viewLifecycleOwner, Observer { forecastObserver(it) })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun queryObserver(query: String) {
        hideForecastList()
        hideTextEmptyList()
        showProgressbar()
        viewModel.requestForecastDaily(query)
    }

    private fun forecastObserver(forecast: ResponseDaily) {
        adapter.setList(forecast.data)
        hideProgressbar()
        showForecastList()
    }

    private fun showProgressbar() {
        binding.pbLoadingDaily.visibility = View.VISIBLE
    }

    private fun hideProgressbar() {
        binding.pbLoadingDaily.visibility = View.INVISIBLE
    }

    private fun showTextEmptyList() {
        binding.textEmptyList.visibility = View.VISIBLE
    }

    private fun hideTextEmptyList() {
        binding.textEmptyList.visibility = View.INVISIBLE
    }

    private fun showForecastList() {
        binding.listForecastDaily.visibility = View.VISIBLE
    }

    private fun hideForecastList() {
        binding.listForecastDaily.visibility = View.INVISIBLE
    }
}
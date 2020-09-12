package com.emikhalets.sunnydayapp.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.emikhalets.sunnydayapp.adapters.DailyAdapter
import com.emikhalets.sunnydayapp.databinding.FragmentForecastDailyBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import timber.log.Timber

class ForecastDailyFragment : Fragment() {

    private var _binding: FragmentForecastDailyBinding? = null
    private val binding get() = _binding!!

    private lateinit var dailyAdapter: DailyAdapter
    private val viewModel: ForecastDailyViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

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
        implementObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun implementObservers() {
        pagerViewModel.currentQuery.observe(viewLifecycleOwner, {
            dailyAdapter = DailyAdapter()
            binding.listForecastDaily.adapter = dailyAdapter
            binding.textNotice.visibility = View.INVISIBLE
            binding.pbLoadingDaily.visibility = View.VISIBLE
            binding.listForecastDaily.visibility = View.INVISIBLE
            viewModel.requestForecastDaily(it)
        })

        viewModel.forecastDaily.observe(viewLifecycleOwner, {
            dailyAdapter.submitList(it.data)
            binding.pbLoadingDaily.visibility = View.INVISIBLE
            binding.listForecastDaily.visibility = View.VISIBLE
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, {
            Timber.d("Error loading weather.")
            Timber.d(it)
            binding.textNotice.text = it
            binding.textNotice.visibility = View.VISIBLE
            binding.pbLoadingDaily.visibility = View.INVISIBLE
        })
    }
}
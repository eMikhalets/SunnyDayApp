package com.emikhalets.sunnydayapp.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.databinding.FragmentForecastBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val pagerViewModel: ViewPagerViewModel by activityViewModels()
    private lateinit var dailyAdapter: DailyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDailyAdapter()
        pagerViewModel.weather.observe(viewLifecycleOwner, { weatherObserver(it) })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initDailyAdapter() {
        dailyAdapter = DailyAdapter()
        binding.listForecast.apply {
            setHasFixedSize(true)
            adapter = dailyAdapter
        }
    }

    private fun weatherObserver(response: Response) {
        dailyAdapter.timezone = response.timezone
        dailyAdapter.submitList(response.daily)
    }
}
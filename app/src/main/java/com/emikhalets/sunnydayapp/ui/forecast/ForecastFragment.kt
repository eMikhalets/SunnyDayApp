package com.emikhalets.sunnydayapp.ui.forecast

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.databinding.FragmentForecastBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.FragmentState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_forecast.*

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
        initObservers()
        initDailyAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initObservers() {
        pagerViewModel.weather.observe(viewLifecycleOwner, { weatherObserver(it) })
        pagerViewModel.userLocation.observe(viewLifecycleOwner, { locationObserver(it) })
    }

    private fun initDailyAdapter() {
        dailyAdapter = DailyAdapter()
        binding.listForecast.apply {
            setHasFixedSize(true)
            adapter = dailyAdapter
        }
    }

    private fun weatherObserver(state: FragmentState<Response>) {
        when (state.status) {
            FragmentState.Status.LOADING -> {
                motion_forecast.transitionToState(R.id.state_loading)
            }
            FragmentState.Status.LOADED -> {
                val response = state.data!!
                dailyAdapter.timezone = response.timezone
                dailyAdapter.submitList(response.daily)
                motion_forecast.transitionToState(R.id.state_forecast)
            }
            FragmentState.Status.ERROR -> {
            }
        }
    }

    private fun locationObserver(location: Location) {
        pagerViewModel.sendWeatherRequest(location.latitude, location.longitude)
    }
}
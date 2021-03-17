package com.emikhalets.sunnydayapp.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emikhalets.sunnydayapp.data.model.WeatherResponse
import com.emikhalets.sunnydayapp.databinding.FragmentForecastBinding
import com.emikhalets.sunnydayapp.ui.MainViewModel
import com.emikhalets.sunnydayapp.utils.OnLocationSettingsClick
import com.emikhalets.sunnydayapp.utils.State
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private val locationSettingsClick: OnLocationSettingsClick?
        get() = requireActivity() as? OnLocationSettingsClick?

    private lateinit var dailyAdapter: DailyAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dailyAdapter = DailyAdapter()
        binding.listForecast.apply {
            adapter = dailyAdapter
            setHasFixedSize(true)
        }
        with(mainViewModel) {
            weather.observe(viewLifecycleOwner) { weatherObserver(it) }
            error.observe(viewLifecycleOwner) { binding.textNotice.text = it }
            weatherState.observe(viewLifecycleOwner) { updateInterface(it) }
        }
        binding.btnLocationSettings.setOnClickListener { onLocationSettingsClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun weatherObserver(response: WeatherResponse) {
        dailyAdapter.timezone = response.timezone
        dailyAdapter.currentWeather = response.current.weather.first().icon
        dailyAdapter.submitList(response.daily)
    }

    private fun onLocationSettingsClick() {
        locationSettingsClick?.onLocationSettingsClick()
    }

    private fun updateInterface(state: State) {
        val duration = 500L
        with(binding) {
            btnLocationSettings.animate().alpha(0f).setDuration(duration).start()
            btnLocationSettings.visibility = View.GONE
            when (state) {
                State.LOADING -> {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingForecast.animate().alpha(1f).setDuration(duration).start()
                    listForecast.animate().alpha(0f).setDuration(duration).start()
                }
                State.LOADED -> {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingForecast.animate().alpha(0f).setDuration(duration).start()
                    listForecast.animate().alpha(1f).setDuration(duration).start()
                }
                State.ERROR -> {
                    textNotice.animate().alpha(1f).setDuration(duration).start()
                    pbLoadingForecast.animate().alpha(0f).setDuration(duration).start()
                    listForecast.animate().alpha(0f).setDuration(duration).start()
                }
            }
        }
    }
}
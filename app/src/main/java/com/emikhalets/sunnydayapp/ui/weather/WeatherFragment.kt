package com.emikhalets.sunnydayapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.adapters.DailyAdapter
import com.emikhalets.sunnydayapp.databinding.FragmentCurrentBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import timber.log.Timber

const val WEATHER = "WEATHER"
const val LOADING = "LOADING"
const val NOTICE = "NOTICE"

class WeatherFragment : Fragment() {

    private var _binding: FragmentCurrentBinding? = null
    private val binding get() = _binding!!

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentBinding.inflate(inflater, container, false)
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
            setVisibilityMode(LOADING)
            weatherViewModel.requestCurrent(it)
            weatherViewModel.requestForecastDaily(it)
        })

        weatherViewModel.currentWeather.observe(viewLifecycleOwner, {
            Timber.d("Current weather is loaded.")
            val weather = it.data.first()
            Picasso.get().load(buildIconUrl(weather.weather.icon)).into(binding.imageWeatherIcon)
            with(binding) {
                textCityName.text = weather.cityName
                textDate.text = "SomeDate"
                textTemp.text = getString(R.string.current_text_temp, weather.temperature.toInt())
                textDesc.text = weather.weather.description
                textWind.text =
                    getString(R.string.current_text_wind_speed, weather.windSpeed * 1000 / 3600)
                textHumidity.text =
                    getString(R.string.current_text_humidity, weather.humidity.toInt())
                textPressure.text = getString(R.string.current_text_pressure, weather.pressure)
            }
            setVisibilityMode(WEATHER)
        })

        weatherViewModel.forecastDaily.observe(viewLifecycleOwner, {
            Timber.d("Forecast daily is loaded.")
            val forecastAdapter = DailyAdapter()
            with(binding.listForecastDaily) {
                addItemDecoration(
                    DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)
                )
                adapter = forecastAdapter
            }
            forecastAdapter.submitList(it.data)
            setVisibilityMode(WEATHER)
        })

        weatherViewModel.errorMessage.observe(viewLifecycleOwner, {
            Timber.d("Error loading weather.")
            binding.textNotice.text = it
            setVisibilityMode(NOTICE)
        })
    }

    private fun setVisibilityMode(mode: String) {
        val duration = 500L
        when (mode) {
            WEATHER -> {
                with(binding) {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingCurrent.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingDaily.animate().alpha(0f).setDuration(duration).start()
                    layoutWeatherCurrent.animate().alpha(1f).setDuration(duration).start()
                    listForecastDaily.animate().alpha(1f).setDuration(duration).start()
                }
            }
            LOADING -> {
                with(binding) {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingCurrent.animate().alpha(1f).setDuration(duration).start()
                    pbLoadingDaily.animate().alpha(1f).setDuration(duration).start()
                    layoutWeatherCurrent.animate().alpha(0f).setDuration(duration).start()
                    listForecastDaily.animate().alpha(0f).setDuration(duration).start()
                }
            }
            else -> {
                with(binding) {
                    textNotice.animate().alpha(1f).setDuration(duration).start()
                    pbLoadingCurrent.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingDaily.animate().alpha(0f).setDuration(duration).start()
                    layoutWeatherCurrent.animate().alpha(0f).setDuration(duration).start()
                    listForecastDaily.animate().alpha(0f).setDuration(duration).start()
                }
            }
        }
    }
}
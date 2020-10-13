package com.emikhalets.sunnydayapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.adapters.DailyAdapter
import com.emikhalets.sunnydayapp.data.pojo.DataDaily
import com.emikhalets.sunnydayapp.databinding.FragmentCurrentBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.WeatherStatus
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherFragment : Fragment(), DailyAdapter.DailyForecastItemClick {

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
        implementObserversAndListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun implementObserversAndListeners() {
        pagerViewModel.currentQuery.observe(viewLifecycleOwner, {
            if (!pagerViewModel.isWeatherLoaded) {
                Timber.d("Query has been updated: ($it)")
                setVisibilityMode(WeatherStatus.LOADING)
                weatherViewModel.requestCurrent(it)
                weatherViewModel.requestForecastDaily(it)
                pagerViewModel.isWeatherLoaded = true
            }
        })

        pagerViewModel.location.observe(viewLifecycleOwner, {
            if (!pagerViewModel.isWeatherLoaded) {
                Timber.d("Location coordinates is updated.")
                setVisibilityMode(WeatherStatus.LOADING)
                weatherViewModel.requestCurrent(it[0], it[1])
                weatherViewModel.requestForecastDaily(it[0], it[1])
                pagerViewModel.isWeatherLoaded = true
            }
        })

        weatherViewModel.currentWeather.observe(viewLifecycleOwner, {
            pagerViewModel.updateTimezone(it.data.first().timezone)
            val weather = it.data.first()
            Timber.d("Current weather has been loaded: ($weather)")
            Picasso.get().load(buildIconUrl(weather.weather.icon))
                .into(binding.imageWeatherIcon)
            with(binding) {
                textCityName.text = weather.cityName
                val date = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(weather.timestamp * 1000),
                    ZoneId.of(weather.timezone)
                )
                val formatter = DateTimeFormatter.ofPattern("d E H:m")
                textDate.text = date.format(formatter)
                textTemp.text =
                    getString(R.string.current_text_temp, weather.temperature.toInt())
                textDesc.text = weather.weather.description
                textWind.text =
                    getString(R.string.current_text_wind_speed, weather.windSpeed)
                textHumidity.text =
                    getString(R.string.current_text_humidity, weather.humidity.toInt())
                textPressure.text = getString(R.string.current_text_pressure, weather.pressure)
            }
            setVisibilityMode(WeatherStatus.WEATHER)
        })

        weatherViewModel.forecastDaily.observe(viewLifecycleOwner, {
            pagerViewModel.updateTimezone(it.timezone)
            val forecast = it.data
            Timber.d("Forecast daily has been loaded: ($forecast)")
            val forecastAdapter = DailyAdapter(it.timezone, this)
            val layoutManager = LinearLayoutManager(requireContext())
            val divider = DividerItemDecoration(requireContext(), layoutManager.orientation)
            binding.listForecastDaily.layoutManager = layoutManager
            binding.listForecastDaily.addItemDecoration(divider)
            binding.listForecastDaily.adapter = forecastAdapter
            forecastAdapter.submitList(forecast)
            setVisibilityMode(WeatherStatus.WEATHER)
        })

        weatherViewModel.errorMessage.observe(viewLifecycleOwner, {
            Timber.d("Error when sending a request to the server")
            binding.textNotice.text = it
            setVisibilityMode(WeatherStatus.NOTICE)
        })

        binding.btnMoreWeather.setOnClickListener {
            Timber.d("Clicked on current weather details")
            weatherViewModel.currentWeather.value?.data?.let {
                Timber.d("Navigate to current weather details")
                val args = Bundle()
                args.putSerializable(
                    getString(R.string.args_current_weather),
                    it.first() as Serializable
                )
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_viewPagerFragment_to_detailsFragment, args)
            }
        }
    }

    private fun setVisibilityMode(status: WeatherStatus) {
        val duration = 500L
        when (status) {
            WeatherStatus.WEATHER -> {
                with(binding) {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingCurrent.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingDaily.animate().alpha(0f).setDuration(duration).start()
                    layoutWeatherCurrent.animate().alpha(1f).setDuration(duration).start()
                    listForecastDaily.animate().alpha(1f).setDuration(duration).start()
                }
            }
            WeatherStatus.LOADING -> {
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

    override fun onDailyForecastClick(dailyForecast: DataDaily) {
        super.onDailyForecastClick(dailyForecast)
        Timber.d("Clicked on forecast weather details")
        Timber.d("Navigate to forecast weather details")
        val args = Bundle()
        args.putSerializable(getString(R.string.args_daily_forecast), dailyForecast as Serializable)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_viewPagerFragment_to_detailsFragment, args)
    }
}
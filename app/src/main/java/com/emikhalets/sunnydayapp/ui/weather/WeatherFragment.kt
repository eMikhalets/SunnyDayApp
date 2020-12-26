package com.emikhalets.sunnydayapp.ui.weather

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.DataDaily
import com.emikhalets.sunnydayapp.data.model.ResponseCurrent
import com.emikhalets.sunnydayapp.data.model.ResponseDaily
import com.emikhalets.sunnydayapp.databinding.FragmentCurrentBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerFragmentDirections
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.emikhalets.sunnydayapp.utils.setPressureUnit
import com.emikhalets.sunnydayapp.utils.setSpeedUnit
import com.emikhalets.sunnydayapp.utils.setTempUnit
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherFragment : Fragment(), DailyAdapter.ForecastItemClick {

    private var _binding: FragmentCurrentBinding? = null
    private val binding get() = _binding!!

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private lateinit var forecastAdapter: DailyAdapter
    private lateinit var pref: SharedPreferences
    private lateinit var prefTemp: String
    private lateinit var prefPressure: String
    private lateinit var prefSpeed: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initForecastAdapter()
        initPreferences()
        initObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initForecastAdapter() {
        forecastAdapter = DailyAdapter(requireContext(), this)
        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)
        binding.listForecastDaily.run {
            addItemDecoration(divider)
            adapter = forecastAdapter
        }
    }

    private fun initPreferences() {
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefTemp = pref.getString(getString(R.string.pref_key_temp), "C")!!
        prefPressure = pref.getString(getString(R.string.pref_key_press), "mb")!!
        prefSpeed = pref.getString(getString(R.string.pref_key_speed), "ms")!!
    }

    private fun initObservers() {
        pagerViewModel.currentQuery.observe(viewLifecycleOwner, { currentQueryObserver(it) })
        pagerViewModel.location.observe(viewLifecycleOwner, { locationObserver(it) })
        weatherViewModel.currentWeather.observe(viewLifecycleOwner, { weatherObserver(it) })
        weatherViewModel.forecastDaily.observe(viewLifecycleOwner, { forecastObserver(it) })

        binding.btnDetails.setOnClickListener { onDetailsClickListener() }
    }

    private fun currentQueryObserver(query: String) {
        if (!pagerViewModel.isWeatherLoaded) {
            Timber.d("Query has been updated: ($query)")
            weatherViewModel.run {
                requestCurrent(query)
                requestForecastDaily(query)
            }
            pagerViewModel.isWeatherLoaded = true
        }
    }

    private fun locationObserver(location: List<Double>) {
        if (!pagerViewModel.isWeatherLoaded) {
            Timber.d("Location coordinates is updated.")
            updateWeatherUi(WeatherState.Status.LOADING)
            updateForecastUi(WeatherState.Status.LOADING)
            weatherViewModel.run {
                requestCurrent(location[0], location[1])
                requestForecastDaily(location[0], location[1])
            }
            pagerViewModel.isWeatherLoaded = true
        }
    }

    private fun weatherObserver(state: WeatherState<ResponseCurrent>) {
        when (state.status) {
            WeatherState.Status.LOADING -> {
            }
            WeatherState.Status.WEATHER -> {
                pagerViewModel.updateTimezone(state.data!!.data.first().timezone)
                val weather = state.data.data.first()
                Timber.d("Current weather has been loaded: ($weather)")
                Picasso.get().load(buildIconUrl(weather.weather.icon))
                    .into(binding.imageWeatherIcon)
                with(binding) {
                    textCityName.text = weather.cityName
                    textDate.text = formatDate(weather.timestamp)
                    setTempUnit(requireContext(), textTemp, weather.temperature, prefTemp)
                    textDesc.text = weather.weather.description
                    setSpeedUnit(requireContext(), textWind, weather.windSpeed, prefSpeed)
                    textHumidity.text =
                        getString(R.string.current_text_humidity, weather.humidity.toInt())
                    setPressureUnit(requireContext(), textPressure, weather.pressure, prefPressure)
                }
            }
            WeatherState.Status.ERROR -> {
                binding.textWeatherError.text = state.error
            }
        }

        updateWeatherUi(state.status)
    }

    private fun forecastObserver(state: WeatherState<ResponseDaily>) {
        when (state.status) {
            WeatherState.Status.LOADING -> {
            }
            WeatherState.Status.WEATHER -> {
                pagerViewModel.updateTimezone(state.data!!.timezone)
                forecastAdapter.updateTimeZone(state.data.timezone)
                val forecast = state.data.data
                Timber.d("Forecast daily has been loaded: ($forecast)")
                forecastAdapter.submitList(forecast)
            }
            WeatherState.Status.ERROR -> {
                binding.textForecastError.text = state.error
            }
        }
        updateForecastUi(state.status)
    }

    private fun onDetailsClickListener() {
        weatherViewModel.currentWeather.value?.data?.let {
            Timber.d("Navigate to current weather details")
            val action = ViewPagerFragmentDirections
                .actionViewPagerFragmentToDetailsFragment(it.data.first(), null)
            Navigation.findNavController(binding.root).navigate(action)
        }
    }

    override fun onDailyForecastClick(dailyForecast: DataDaily) {
        Timber.d("Navigate to forecast weather details")
        val action = ViewPagerFragmentDirections
            .actionViewPagerFragmentToDetailsFragment(null, dailyForecast)
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun formatDate(timestamp: Long): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp * 1000),
            ZoneId.systemDefault()
        )
        val formatter = DateTimeFormatter.ofPattern("d EEEE H:m")
        return date.format(formatter)
    }

    private fun updateWeatherUi(status: WeatherState.Status) {
        when (status) {
            WeatherState.Status.LOADING -> {
                with(binding) {
                    textNotice.visibility = View.INVISIBLE
                    textWeatherError.visibility = View.INVISIBLE
                    layoutWeatherCurrent.visibility = View.INVISIBLE
                    pbLoadingCurrent.visibility = View.VISIBLE
                }
            }
            WeatherState.Status.WEATHER -> {
                with(binding) {
                    pbLoadingCurrent.visibility = View.INVISIBLE
                    layoutWeatherCurrent.visibility = View.VISIBLE
                }
            }
            WeatherState.Status.ERROR -> {
                with(binding) {
                    pbLoadingCurrent.visibility = View.INVISIBLE
                    textWeatherError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateForecastUi(status: WeatherState.Status) {
        when (status) {
            WeatherState.Status.LOADING -> {
                with(binding) {
                    textNotice.visibility = View.INVISIBLE
                    textForecastError.visibility = View.INVISIBLE
                    listForecastDaily.visibility = View.INVISIBLE
                    pbLoadingDaily.visibility = View.VISIBLE
                }
            }
            WeatherState.Status.WEATHER -> {
                with(binding) {
                    pbLoadingDaily.visibility = View.INVISIBLE
                    listForecastDaily.visibility = View.VISIBLE
                }
            }
            WeatherState.Status.ERROR -> {
                with(binding) {
                    pbLoadingDaily.visibility = View.INVISIBLE
                    textForecastError.visibility = View.VISIBLE
                }
            }
        }
    }
}
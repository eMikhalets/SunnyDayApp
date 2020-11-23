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
import com.emikhalets.sunnydayapp.databinding.FragmentCurrentBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.AppHelper
import com.emikhalets.sunnydayapp.utils.status.WeatherState
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

    private lateinit var forecastAdapter: DailyAdapter
    private lateinit var pref: SharedPreferences
    private lateinit var prefTemp: String
    private lateinit var prefPressure: String
    private lateinit var prefSpeed: String

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
        initForecastAdapter()
        initPreferences()
        initObservers()
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initForecastAdapter() {
        forecastAdapter = DailyAdapter(requireContext(),this)
        val forecastLm = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), forecastLm.orientation)
        binding.listForecastDaily.run {
            layoutManager = forecastLm
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
        pagerViewModel.currentQuery.observe(viewLifecycleOwner, {
            if (!pagerViewModel.isWeatherLoaded) {
                Timber.d("Query has been updated: ($it)")
                setUiState(WeatherState.Status.LOADING)
                weatherViewModel.run {
                    requestCurrent(it)
                    requestForecastDaily(it)
                }
                pagerViewModel.isWeatherLoaded = true
            }
        })

        pagerViewModel.location.observe(viewLifecycleOwner, {
            if (!pagerViewModel.isWeatherLoaded) {
                Timber.d("Location coordinates is updated.")
                setUiState(WeatherState.Status.LOADING)
                weatherViewModel.run {
                    requestCurrent(it[0], it[1])
                    requestForecastDaily(it[0], it[1])
                }
                pagerViewModel.isWeatherLoaded = true
            }
        })

        weatherViewModel.currentWeather.observe(viewLifecycleOwner, {
            when (it.status) {
                WeatherState.Status.WEATHER -> {
                    pagerViewModel.updateTimezone(it.data!!.data.first().timezone)
                    val weather = it.data.data.first()
                    Timber.d("Current weather has been loaded: ($weather)")
                    Picasso.get().load(AppHelper.buildIconUrl(weather.weather.icon))
                        .into(binding.imageWeatherIcon)
                    with(binding) {
                        textCityName.text = weather.cityName
                        textDate.text = formatDate(weather.timestamp)
                        AppHelper.setTempUnit(
                            requireContext(), textTemp, weather.temperature, prefTemp
                        )
                        textDesc.text = weather.weather.description
                        AppHelper.setSpeedUnit(
                            requireContext(), textWind, weather.windSpeed, prefSpeed
                        )
                        textHumidity.text =
                            getString(R.string.current_text_humidity, weather.humidity.toInt())
                        AppHelper.setPressureUnit(
                            requireContext(), textPressure, weather.pressure, prefPressure
                        )
                    }
                }
                WeatherState.Status.ERROR -> binding.textNotice.text = it.message
            }
            setUiState(it.status)
        })

        weatherViewModel.forecastDaily.observe(viewLifecycleOwner, {
            when (it.status) {
                WeatherState.Status.WEATHER -> {
                    pagerViewModel.updateTimezone(it.data!!.timezone)
                    forecastAdapter.updateTimeZone(it.data.timezone)
                    val forecast = it.data.data
                    Timber.d("Forecast daily has been loaded: ($forecast)")
                    forecastAdapter.submitList(forecast)
                }
                WeatherState.Status.ERROR -> binding.textNotice.text = it.message
            }
            setUiState(it.status)
        })
    }

    private fun initListeners() {
        binding.btnDetails.setOnClickListener {
            Timber.d("Clicked on current weather details")
            weatherViewModel.currentWeather.value?.data?.let {
                Timber.d("Navigate to current weather details")
                val args = Bundle()
                args.putSerializable(
                    getString(R.string.args_current_weather),
                    it.data.first() as Serializable
                )
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_viewPagerFragment_to_detailsFragment, args)
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp * 1000),
            ZoneId.systemDefault()
        )
        val formatter = DateTimeFormatter.ofPattern("d EEEE H:m")
        return date.format(formatter)
    }

    private fun setUiState(status: WeatherState.Status) {
        val duration = 500L
        when (status) {
            WeatherState.Status.WEATHER -> {
                with(binding) {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingCurrent.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingDaily.animate().alpha(0f).setDuration(duration).start()
                    layoutWeatherCurrent.animate().alpha(1f).setDuration(duration).start()
                    listForecastDaily.animate().alpha(1f).setDuration(duration).start()
                }
            }
            WeatherState.Status.LOADING -> {
                with(binding) {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingCurrent.animate().alpha(1f).setDuration(duration).start()
                    pbLoadingDaily.animate().alpha(1f).setDuration(duration).start()
                    layoutWeatherCurrent.animate().alpha(0f).setDuration(duration).start()
                    listForecastDaily.animate().alpha(0f).setDuration(duration).start()
                }
            }
            WeatherState.Status.ERROR -> {
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
        Timber.d("Clicked on forecast weather details")
        Timber.d("Navigate to forecast weather details")
        val args = Bundle()
        args.putSerializable(getString(R.string.args_daily_forecast), dailyForecast as Serializable)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_viewPagerFragment_to_detailsFragment, args)
    }
}
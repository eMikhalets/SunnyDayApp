package com.emikhalets.sunnydayapp.ui.details

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.DataCurrent
import com.emikhalets.sunnydayapp.data.model.DataDaily
import com.emikhalets.sunnydayapp.databinding.FragmentDetailsBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.AppHelper
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private lateinit var pref: SharedPreferences
    private lateinit var prefTemp: String
    private lateinit var prefPressure: String
    private lateinit var prefSpeed: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefTemp = pref.getString(getString(R.string.pref_key_temp), "C")!!
        prefPressure = pref.getString(getString(R.string.pref_key_press), "mb")!!
        prefSpeed = pref.getString(getString(R.string.pref_key_speed), "ms")!!
        fetchArgs()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun fetchArgs() {
        arguments?.let {
            if (it.containsKey(getString(R.string.args_current_weather))) {
                val currentWeather =
                    it.getSerializable(getString(R.string.args_current_weather)) as DataCurrent
                setWeatherData(currentWeather, null)
            } else if (it.containsKey(getString(R.string.args_daily_forecast))) {
                val forecastWeather =
                    it.getSerializable(getString(R.string.args_daily_forecast)) as DataDaily
                setWeatherData(null, forecastWeather)
            }
        }
    }

    private fun setWeatherData(currentWeather: DataCurrent?, forecastWeather: DataDaily?) {
        Timber.d("Set weather data")
        currentWeather?.let {
            Timber.d("Set current weather data")
            Picasso.get().load(AppHelper.buildIconUrl(it.weather.icon))
                .into(binding.imageWeatherIcon)
            with(binding) {
                AppHelper.setTempUnit(requireContext(), textTemp, it.temperature, prefTemp)
                AppHelper.setTempUnit(requireContext(), textAppTemp, it.tempFeelsLike, prefTemp)
                textSunset.text = formatTime(it.sunset)
                textSunrise.text = formatTime(it.sunrise)
                AppHelper.setPressureUnit(requireContext(), textPressure, it.pressure, prefPressure)
                AppHelper.setSpeedUnit(requireContext(), textWindSpeed, it.windSpeed, prefSpeed)
                textWindDir.text =
                    getString(R.string.details_text_wind_dir, it.windDir.toInt(), it.windDirFull)
                textVisibility.text = getString(R.string.details_text_visibility, it.visibility)
                textClouds.text = getString(R.string.details_text_clouds, it.clouds.toInt())
                textHumidity.text = getString(R.string.details_text_humidity, it.humidity.toInt())
                textPrecip.text = getString(R.string.details_text_precip, it.precipitation)
                textSnow.text = getString(R.string.details_text_snow, it.snowfall)
                textUv.text = it.uvIndex.toString()
                textWeatherCode.text = it.weather.code
                textStation.text = it.station
            }
        }
        forecastWeather?.let {
            Timber.d("Set forecast weather data")
            val appTemp = (it.tempFeelsLikeMax + it.tempFeelsLikeMin) / 2
            Picasso.get().load(AppHelper.buildIconUrl(it.weather.icon))
                .into(binding.imageWeatherIcon)
            with(binding) {
                AppHelper.setTempUnit(requireContext(), textTemp, it.temperature, prefTemp)
                AppHelper.setTempUnit(requireContext(), textAppTemp, appTemp, prefTemp)
                textSunset.text = formatTime(it.sunsetTime)
                textSunrise.text = formatTime(it.sunriseTime)
                AppHelper.setPressureUnit(requireContext(), textPressure, it.pressure, prefPressure)
                AppHelper.setSpeedUnit(requireContext(), textWindSpeed, it.windSpeed, prefSpeed)
                textWindDir.text =
                    getString(R.string.details_text_wind_dir, it.windDir.toInt(), it.windDirFull)
                textVisibility.text = getString(R.string.details_text_visibility, it.visibility)
                textClouds.text = getString(R.string.details_text_clouds, it.clouds.toInt())
                textHumidity.text = getString(R.string.details_text_humidity, it.humidity.toInt())
                textPrecip.text = getString(R.string.details_text_precip, it.precip)
                textSnow.text = getString(R.string.details_text_snow, it.snowfall)
                textUv.text = it.uvIndex.toString()
                textWeatherCode.text = it.weather.code.toString()
                textStation.text = getString(R.string.details_text_none)
            }
        }
    }

    private fun formatTime(time: String): String {
        val arr = time.split(":")
        var localTime = LocalTime.of(arr[0].toInt(), arr[1].toInt())
        val offset = TimeZone.getTimeZone(pagerViewModel.timezone.value).rawOffset / 3600000L
        localTime = localTime.plusHours(offset)
        val formatter = DateTimeFormatter.ofPattern("H:m")
        return localTime.format(formatter)
    }

    private fun formatTime(ts: Long): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(ts * 1000),
            ZoneId.of(pagerViewModel.timezone.value)
        )
        val formatter = DateTimeFormatter.ofPattern("H:m")
        return date.format(formatter)
    }
}
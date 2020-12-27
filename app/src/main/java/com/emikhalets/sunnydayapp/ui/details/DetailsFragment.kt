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
import com.emikhalets.sunnydayapp.databinding.FragmentDetailsBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.emikhalets.sunnydayapp.utils.setPressureUnit
import com.emikhalets.sunnydayapp.utils.setSpeedUnit
import com.emikhalets.sunnydayapp.utils.setTempUnit
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.*
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPreferences()
        getNavArguments()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initPreferences() {
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefTemp = pref.getString(getString(R.string.pref_key_temp), "C")!!
        prefPressure = pref.getString(getString(R.string.pref_key_press), "mb")!!
        prefSpeed = pref.getString(getString(R.string.pref_key_speed), "ms")!!
    }

    private fun getNavArguments() {
        arguments?.let {
            DetailsFragmentArgs.fromBundle(it).weather?.let { weather -> setWeatherData(weather) }
            DetailsFragmentArgs.fromBundle(it).forecast?.let { forecast -> setForecastData(forecast) }
        }
    }

    private fun setWeatherData(weather: DataCurrent) {
        Timber.d("Set current weather data")
        Picasso.get().load(buildIconUrl(weather.weather.icon))
            .into(binding.imageWeatherIcon)
        with(binding) {
            setTempUnit(requireContext(), textTemp, weather.temperature, prefTemp)
            setTempUnit(requireContext(), textAppTemp, weather.tempFeelsLike, prefTemp)
            textSunset.text = formatTime(weather.sunset)
            textSunrise.text = formatTime(weather.sunrise)
            setPressureUnit(requireContext(), textPressure, weather.pressure, prefPressure)
            setSpeedUnit(requireContext(), textWindSpeed, weather.windSpeed, prefSpeed)
            textWindDir.text = getString(
                R.string.details_text_wind_dir, weather.windDir.toInt(), weather.windDirFull
            )
            textVisibility.text = getString(R.string.details_text_visibility, weather.visibility)
            textClouds.text = getString(R.string.details_text_clouds, weather.clouds.toInt())
            textHumidity.text = getString(R.string.details_text_humidity, weather.humidity.toInt())
            textPrecip.text = getString(R.string.details_text_precip, weather.precipitation)
            textSnow.text = getString(R.string.details_text_snow, weather.snowfall)
            textUv.text = weather.uvIndex.toString()
            textWeatherCode.text = weather.weather.code
            textStation.text = weather.station
        }
        details_container.transitionToEnd()
    }

    private fun setForecastData(forecast: DataDaily) {
        Timber.d("Set forecast weather data")
        val appTemp = (forecast.tempFeelsLikeMax + forecast.tempFeelsLikeMin) / 2
        Picasso.get().load(buildIconUrl(forecast.weather.icon))
            .into(binding.imageWeatherIcon)
        with(binding) {
            setTempUnit(requireContext(), textTemp, forecast.temperature, prefTemp)
            setTempUnit(requireContext(), textAppTemp, appTemp, prefTemp)
            textSunset.text = formatTime(forecast.sunsetTime)
            textSunrise.text = formatTime(forecast.sunriseTime)
            setPressureUnit(requireContext(), textPressure, forecast.pressure, prefPressure)
            setSpeedUnit(requireContext(), textWindSpeed, forecast.windSpeed, prefSpeed)
            textWindDir.text = getString(
                R.string.details_text_wind_dir, forecast.windDir.toInt(), forecast.windDirFull
            )
            textVisibility.text = getString(R.string.details_text_visibility, forecast.visibility)
            textClouds.text = getString(R.string.details_text_clouds, forecast.clouds.toInt())
            textHumidity.text = getString(R.string.details_text_humidity, forecast.humidity.toInt())
            textPrecip.text = getString(R.string.details_text_precip, forecast.precip)
            textSnow.text = getString(R.string.details_text_snow, forecast.snowfall)
            textUv.text = forecast.uvIndex.toString()
            textWeatherCode.text = forecast.weather.code.toString()
            textStation.text = getString(R.string.details_text_none)
        }
        details_container.transitionToEnd()
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
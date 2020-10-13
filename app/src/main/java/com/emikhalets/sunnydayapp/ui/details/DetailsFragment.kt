package com.emikhalets.sunnydayapp.ui.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.pojo.DataCurrent
import com.emikhalets.sunnydayapp.data.pojo.DataDaily
import com.emikhalets.sunnydayapp.databinding.FragmentDetailsBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

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
            Picasso.get().load(buildIconUrl(it.weather.icon)).into(binding.imageWeatherIcon)
            binding.textTemp.text = getString(R.string.details_text_temp, it.temperature.toInt())
            binding.textAppTemp.text =
                getString(R.string.details_text_temp, it.tempFeelsLike.toInt())
            binding.textSunset.text = it.sunset
            binding.textSunrise.text = it.sunrise
            binding.textPressure.text = getString(R.string.details_text_pressure, it.pressure)
            binding.textWindSpeed.text = getString(R.string.details_text_wind_speed, it.windSpeed)
            binding.textWindDir.text =
                getString(R.string.details_text_wind_dir, it.windDir.toInt(), it.windDirFull)
            binding.textVisibility.text = getString(R.string.details_text_visibility, it.visibility)
            binding.textClouds.text = getString(R.string.details_text_clouds, it.clouds.toInt())
            binding.textHumidity.text =
                getString(R.string.details_text_humidity, it.humidity.toInt())
            binding.textPrecip.text = getString(R.string.details_text_precip, it.precipitation)
            binding.textSnow.text = getString(R.string.details_text_snow, it.snowfall)
            binding.textUv.text = it.uvIndex.toString()
            binding.textWeatherCode.text = it.weather.code
            binding.textStation.text = it.station
        }
        forecastWeather?.let {
            Timber.d("Set forecast weather data")
            val appTemp = (it.tempFeelsLikeMax + it.tempFeelsLikeMin) / 2
            Picasso.get().load(buildIconUrl(it.weather.icon)).into(binding.imageWeatherIcon)
            binding.textTemp.text = getString(R.string.details_text_temp, it.temperature.toInt())
            binding.textAppTemp.text =
                getString(R.string.details_text_temp, appTemp.toInt())
            binding.textSunset.text = formatTime(it.sunsetTime)
            binding.textSunrise.text = formatTime(it.sunriseTime)
            binding.textPressure.text = getString(R.string.details_text_pressure, it.pressure)
            binding.textWindSpeed.text = getString(R.string.details_text_wind_speed, it.windSpeed)
            binding.textWindDir.text =
                getString(R.string.details_text_wind_dir, it.windDir.toInt(), it.windDirFull)
            binding.textVisibility.text = getString(R.string.details_text_visibility, it.visibility)
            binding.textClouds.text = getString(R.string.details_text_clouds, it.clouds.toInt())
            binding.textHumidity.text =
                getString(R.string.details_text_humidity, it.humidity.toInt())
            binding.textPrecip.text = getString(R.string.details_text_precip, it.precip)
            binding.textSnow.text = getString(R.string.details_text_snow, it.snowfall)
            binding.textUv.text = it.uvIndex.toString()
            binding.textWeatherCode.text = it.weather.code.toString()
            binding.textStation.text = getString(R.string.details_text_none)
        }
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
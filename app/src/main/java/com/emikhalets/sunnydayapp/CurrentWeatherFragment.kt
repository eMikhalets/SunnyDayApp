package com.emikhalets.sunnydayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emikhalets.sunnydayapp.databinding.FragmentCurrentBinding
import com.emikhalets.sunnydayapp.network.pojo.DataCurrent
import com.emikhalets.sunnydayapp.utils.CURRENT_QUERY
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.emikhalets.sunnydayapp.viewmodels.CurrentWeatherViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherViewModel: CurrentWeatherViewModel

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
        weatherViewModel = ViewModelProvider(this).get(CurrentWeatherViewModel::class.java)
        CURRENT_QUERY.observe(viewLifecycleOwner, Observer { weatherViewModel.requestCurrent(it) })
        weatherViewModel.currentWeather.observe(viewLifecycleOwner, Observer {
            setWeatherData(it.data.first())
        })
        weatherViewModel.requestCurrent("Moscow")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setWeatherData(data: DataCurrent) {
        convertTime(data.sunrise, data.timezone)
        Picasso.get().load(buildIconUrl(data.weather.icon)).into(binding.imageWeatherIcon)

        with(binding) {
            textCityName.text = data.cityName
            textDate.text = convertDate(data.unixTime)
            textTemp.text = getString(R.string.current_text_temp, data.temperature.toInt())
            textDesc.text = data.weather.description
            textSunrise.text = convertTime(data.sunrise, data.timezone)
            textSunset.text = convertTime(data.sunset, data.timezone)
            textPressure.text = getString(R.string.current_text_pressure, data.pressure)
            textWindSpeed.text = getString(R.string.current_text_wind_speed, data.windSpeed)
            textWindDir.text = data.windDirAbbr
            textFeelslike.text =
                getString(R.string.current_text_feelslike, data.tempFeelsLike.toInt())
            textHumidity.text = getString(R.string.current_text_humidity, data.humidity)
            textClouds.text = getString(R.string.current_text_clouds, data.clouds)
            textVisibility.text = getString(R.string.current_text_visibility, data.visibility)
            textPrecip.text = getString(R.string.current_text_precip, data.precipitation)
            textSnowfall.text = getString(R.string.current_text_snowfall, data.snowfall)
            textUv.text = getString(R.string.current_text_uv, data.uvIndex)
        }
    }

    private fun convertDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        // Timestamp in seconds, convert to milliseconds
        calendar.timeInMillis = timestamp * 1000
        val simpleFormat = SimpleDateFormat("d MMMM yyyy\nHH:mm", Locale.US)
        return simpleFormat.format(calendar.time)
    }

    private fun convertTime(time: String, tz: String): String {
        val splitTime = time.split(":")
        val calendar = Calendar.getInstance()
        // Returns offset in milliseconds
        val timeZone = TimeZone.getTimeZone(tz)
        // Convert to hours
        val offset = timeZone.rawOffset / 1000 / 60 / 60
        calendar.set(Calendar.HOUR_OF_DAY, splitTime[0].toInt() + offset)
        calendar.set(Calendar.MINUTE, splitTime[1].toInt())
        val simpleFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
        return simpleFormat.format(calendar.time)
    }
}
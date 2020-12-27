package com.emikhalets.sunnydayapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.databinding.FragmentWeatherBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private lateinit var hourlyAdapter: HourlyAdapter
//    private lateinit var pref: SharedPreferences
//    private lateinit var prefTemp: String
//    private lateinit var prefPressure: String
//    private lateinit var prefSpeed: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHourlyAdapter()
//        initPreferences()
        initObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initHourlyAdapter() {
        hourlyAdapter = HourlyAdapter()
        binding.listHourly.adapter = hourlyAdapter
    }

//    private fun initPreferences() {
//        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        prefTemp = pref.getString(getString(R.string.pref_key_temp), "C")!!
//        prefPressure = pref.getString(getString(R.string.pref_key_press), "mb")!!
//        prefSpeed = pref.getString(getString(R.string.pref_key_speed), "ms")!!
//    }

    private fun initObservers() {
        pagerViewModel.weather.observe(viewLifecycleOwner, { weatherObserver(it) })
//        pagerViewModel.location.observe(viewLifecycleOwner, { locationObserver(it) })
    }

    private fun weatherObserver(response: Response) {
        with(binding.layoutWeatherCurrent) {
            Picasso.get().load(buildIconUrl(response.current.weather.first().icon)).into(imageIcon)
            textHeader.text = getString(
                R.string.weather_text_header,
                formatDate(response.current.dt, response.timezone),
                pagerViewModel.currentCity.name
            )
            textTemp.text = response.current.temp.toString()
            textDesc.text = getString(
                R.string.weather_text_header,
                response.current.weather.first().main,
                response.current.weather.first().description
            )
            textCloud.text = getString(
                R.string.weather_text_cloud,
                response.current.clouds
            )
            textHumidity.text = getString(
                R.string.weather_text_humidity,
                response.current.humidity
            )
            textFeelsLike.text = response.current.feels_like.toString()
            textWind.text = getString(
                R.string.weather_text_wind,
                response.current.wind_speed
            )
            // TODO(): create converter
            textWindDir.text = response.current.wind_deg.toString()
            textPressure.text = getString(
                R.string.weather_text_pressure,
                response.current.pressure
            )
        }

        with(binding.layoutSunTime) {
            textSunrise.text = formatTime(response.current.sunrise, response.timezone)
            textSunset.text = formatTime(response.current.sunset, response.timezone)
        }

        hourlyAdapter.timezone = response.timezone
        hourlyAdapter.submitList(response.hourly)

    }

//    private fun locationObserver(location: List<Double>) {
//        if (!pagerViewModel.isWeatherLoaded) {
//            Timber.d("Location coordinates is updated.")
//            updateWeatherUi(WeatherState.Status.LOADING)
//            updateForecastUi(WeatherState.Status.LOADING)
//            weatherViewModel.run {
//                requestCurrent(location[0], location[1])
//                requestForecastDaily(location[0], location[1])
//            }
//            pagerViewModel.isWeatherLoaded = true
//        }
//    }

    private fun formatDate(timestamp: Long, timezone: String): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.of(timezone)
        )
        return date.format(DateTimeFormatter.ofPattern("E, d M"))
    }

    private fun formatTime(timestamp: Long, timezone: String): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.of(timezone)
        )
        return date.format(DateTimeFormatter.ofPattern("E, d M"))
    }
}
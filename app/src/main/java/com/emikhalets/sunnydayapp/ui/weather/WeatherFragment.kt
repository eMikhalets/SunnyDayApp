package com.emikhalets.sunnydayapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.WeatherResponse
import com.emikhalets.sunnydayapp.databinding.FragmentWeatherBinding
import com.emikhalets.sunnydayapp.ui.MainViewModel
import com.emikhalets.sunnydayapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val locationSettingsClick: OnLocationSettingsClick?
        get() = requireActivity() as? OnLocationSettingsClick?

    private val themeListener: OnThemeListener?
        get() = requireActivity() as? OnThemeListener?

    private lateinit var hourlyAdapter: HourlyAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hourlyAdapter = HourlyAdapter()
        binding.listHourly.apply {
            adapter = hourlyAdapter
            setHasFixedSize(true)
            addOnItemTouchListener(recyclerScrollListener())
        }
        with(mainViewModel) {
            weather.observe(viewLifecycleOwner) { setWeatherData(it) }
            error.observe(viewLifecycleOwner) { binding.textNotice.text = it }
            weatherState.observe(viewLifecycleOwner) { updateInterface(it) }
        }
        binding.btnLocationSettings.setOnClickListener { onLocationSettingsClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
                    pbLoadingWeather.animate().alpha(1f).setDuration(duration).start()
                    weatherScroll.animate().alpha(0f).setDuration(duration).start()
                }
                State.LOADED -> {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbLoadingWeather.animate().alpha(0f).setDuration(duration).start()
                    weatherScroll.animate().alpha(1f).setDuration(duration).start()
                }
                State.ERROR -> {
                    textNotice.animate().alpha(1f).setDuration(duration).start()
                    pbLoadingWeather.animate().alpha(0f).setDuration(duration).start()
                    weatherScroll.animate().alpha(0f).setDuration(duration).start()
                }
            }
        }
    }

    private fun setWeatherData(response: WeatherResponse) {
        val weather = response.current.weather.first()
        when {
            weather.icon.contains("n") -> {
                themeListener?.onThemeChange(true)
            }
            weather.icon == "11d" -> {
                themeListener?.onThemeChange(true)
                setBackgrounds(weather.icon)
            }
            else -> {
                themeListener?.onThemeChange(false)
                setBackgrounds(weather.icon)
            }
        }
        setMainData(response)
        setViewSunTimeData(response)
        setRecyclerData(response)
    }

    private fun setBackgrounds(icon: String) {
        with(binding) {
            layoutWeatherMain.root.setBackgroundColor(getBackgroundColor(requireContext(), icon))
            viewSunTime.setBackgroundColor(getBackgroundColor(requireContext(), icon))
        }
    }

    private fun setMainData(response: WeatherResponse) {
        with(binding.layoutWeatherMain) {
            imageIcon.load(buildIconUrl(response.current.weather.first().icon))
            textCity.text = mainViewModel.currentCity
            textDate.text = formatDate(response.current.dt, response.timezone)
            textTemp.text = response.current.temp.toInt().toString()
            setTemperatureUnit(requireContext(), textTempUnit)
            textDesc.text = response.current.weather.first().description
            textCloud.text = getString(
                R.string.weather_text_cloud,
                response.current.clouds.toInt()
            )
            textHumidity.text = getString(
                R.string.weather_text_humidity,
                response.current.humidity.toInt()
            )
            setTemperature(requireContext(), textFeelsLike, response.current.feels_like.toInt())
            setWindSpeed(requireContext(), textWind, response.current.wind_speed.toInt())
            textWindDir.text = convertWingDegree(response.current.wind_deg)
            textPressure.text = getString(
                R.string.weather_text_pressure,
                response.current.pressure.toInt()
            )
        }
    }

    private fun formatDate(timestamp: Long, timezone: String): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp * 1000),
            ZoneId.of(timezone)
        )
        return date.format(DateTimeFormatter.ofPattern("E, d MMM"))
    }

    private fun setViewSunTimeData(response: WeatherResponse) {
        binding.viewSunTime.setTime(
            formatTime(response.current.dt, response.timezone),
            formatTime(response.current.sunrise, response.timezone),
            formatTime(response.current.sunset, response.timezone)
        )
    }

    private fun formatTime(timestamp: Long, timezone: String): String {
        val time = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp * 1000),
            ZoneId.of(timezone)
        )
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun setRecyclerData(response: WeatherResponse) {
        hourlyAdapter.apply {
            timezone = response.timezone
            currentWeather = response.current.weather.first().icon
            submitList(response.hourly)
        }
    }

    private fun convertWingDegree(degree: Double): String {
        return when (degree) {
            in 25.0..65.0 -> "N-E"
            in 65.0..115.0 -> "E"
            in 115.0..155.0 -> "S-E"
            in 155.0..205.0 -> "S"
            in 205.0..245.0 -> "S-W"
            in 245.0..295.0 -> "W"
            in 295.0..335.0 -> "N-W"
            else -> "N"
        }
    }

    private fun recyclerScrollListener() = object : CustomItemTouchListener() {
        var lastX = 0
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_MOVE -> {
                    val isScrollingRight = e.x < lastX
                    val layoutManager = binding.listHourly.layoutManager as LinearLayoutManager
                    val itemCount = binding.listHourly.adapter?.itemCount?.minus(1)
                    mainViewModel.hourlyScrollCallback.value = isScrollingRight &&
                            layoutManager.findLastCompletelyVisibleItemPosition() == itemCount ||
                            !isScrollingRight &&
                            layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                }
                MotionEvent.ACTION_UP -> {
                    lastX = 0
                    mainViewModel.hourlyScrollCallback.value = true
                }
                MotionEvent.ACTION_DOWN -> {
                    lastX = e.x.toInt()
                }
            }
            return false
        }
    }
}
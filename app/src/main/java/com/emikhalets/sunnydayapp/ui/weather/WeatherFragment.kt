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

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val locationSettingsClick: OnLocationSettingsClick?
        get() = requireActivity() as? OnLocationSettingsClick?

//    private val themeListener: OnThemeListener?
//        get() = requireActivity() as? OnThemeListener?

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
            searchingState.observe(viewLifecycleOwner) { updateInterface(it) }
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
        val data = response.current
        val weather = response.current.weather.first()
//        setColors(weather.icon)
        with(binding.layoutWeatherMain) {
            imageIcon.load(buildIconUrl(data.weather.first().icon))
            textCity.text = mainViewModel.currentCity
            textDate.text = formatDate(data.dt, response.timezone)
            textTemp.text = data.temp.toInt().toString()
            setTemperatureUnit(requireContext(), textTempUnit)
            textDesc.text = weather.description
            textCloud.text = getString(
                R.string.weather_text_cloud,
                data.clouds.toInt()
            )
            textHumidity.text = getString(
                R.string.weather_text_humidity,
                data.humidity.toInt()
            )
            setTemperature(requireContext(), textFeelsLike, data.feels_like.toInt())
            setWindSpeed(requireContext(), textWind, data.wind_speed.toInt())
            // TODO(): create converter
            textWindDir.text = response.current.wind_deg.toInt().toString()
            textPressure.text = getString(
                R.string.weather_text_pressure,
                data.pressure.toInt()
            )
        }
        binding.viewSunTime.setTime(
            formatTime(data.dt, response.timezone),
            formatTime(data.sunrise, response.timezone),
            formatTime(data.sunset, response.timezone)
        )
        hourlyAdapter.timezone = response.timezone
        hourlyAdapter.submitList(response.hourly)
    }

//    private fun setColors(weather: String) {
//        if (weather.contains("n")) {
//            themeListener?.onThemeChange()
//            return
//        }
//        when (weather) {
//            "01d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClear)
//            )
//            "02d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//            )
//            "03d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//            )
//            "04d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//            )
//            "09d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryRain)
//            )
//            "10d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryRain)
//            )
//            "11d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorTextNight),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryStorm)
//            )
//            "13d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimarySnow)
//            )
//            "50d" -> setColors(
//                ContextCompat.getColor(binding.root.context, R.color.colorText),
//                ContextCompat.getColor(binding.root.context, R.color.colorPrimaryMist)
//            )
//        }
//    }
//
//    private fun setColors(text: Int, bg: Int) {
//        with(binding) {
//            layoutWeatherMain.textCity.setTextColor(text)
//            layoutWeatherMain.textDate.setTextColor(text)
//            layoutWeatherMain.textTemp.setTextColor(text)
//            layoutWeatherMain.textTempUnit.setTextColor(text)
//            layoutWeatherMain.textDesc.setTextColor(text)
//            layoutWeatherMain.textCloud.setTextColor(text)
//            layoutWeatherMain.textHumidity.setTextColor(text)
//            layoutWeatherMain.textFeelsLike.setTextColor(text)
//            layoutWeatherMain.textWind.setTextColor(text)
//            layoutWeatherMain.textWindDir.setTextColor(text)
//            layoutWeatherMain.textPressure.setTextColor(text)
//            layoutWeatherMain.root.setBackgroundColor(bg)
//            viewSunTime.setBackgroundColor(bg)
//        }
//    }

    private fun recyclerScrollListener() = object : CustomItemTouchListener() {
        var lastX = 0
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_MOVE -> {
                    val isScrollingRight = e.x < lastX
                    val layoutManager = binding.listHourly.layoutManager as LinearLayoutManager
                    val itemCount = binding.listHourly.adapter?.itemCount?.minus(1)
                    mainViewModel.scrollCallback.value = isScrollingRight &&
                            layoutManager.findLastCompletelyVisibleItemPosition() == itemCount ||
                            !isScrollingRight &&
                            layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                }
                MotionEvent.ACTION_UP -> {
                    lastX = 0
                    mainViewModel.scrollCallback.value = true
                }
                MotionEvent.ACTION_DOWN -> {
                    lastX = e.x.toInt()
                }
            }
            return false
        }
    }
}
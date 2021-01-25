package com.emikhalets.sunnydayapp.ui.weather

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Response
import com.emikhalets.sunnydayapp.databinding.FragmentWeatherBinding
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.*
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_weather.*
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private lateinit var hourlyAdapter: HourlyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("CREATING FRAGMENT WEATHER FRAGMENT")
        initHourlyAdapter()
        initObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initHourlyAdapter() {
        hourlyAdapter = HourlyAdapter(requireContext())
        binding.listHourly.apply {
            adapter = hourlyAdapter
            addOnItemTouchListener(recyclerScrollListener())
        }
    }

    private fun initObservers() {
        pagerViewModel.weather.observe(viewLifecycleOwner, { weatherObserver(it) })
        pagerViewModel.userLocation.observe(viewLifecycleOwner, { locationObserver(it) })
    }

    private fun weatherObserver(state: FragmentState<Response>) {
        when (state.status) {
            FragmentState.Status.LOADING -> {
                motion_weather.transitionToState(R.id.state_loading)
            }
            FragmentState.Status.LOADED -> {
                setWeatherData(state.data!!)
                motion_weather.transitionToState(R.id.state_weather)
            }
            FragmentState.Status.ERROR -> {
            }
        }
    }

    private fun locationObserver(location: Location) {
        pagerViewModel.sendWeatherRequest(
            location.latitude,
            location.longitude,
            pagerViewModel.prefUnits,
            pagerViewModel.prefLang
        )
    }

    private fun setWeatherData(response: Response) {
        val data = response.current
        val weather = response.current.weather.first()
        hourlyAdapter.submitList(null)

        with(binding.layoutWeatherCurrent) {
            Picasso.get().load(buildIconUrl(data.weather.first().icon))
                .into(imageIcon)
            textCity.text = pagerViewModel.currentCity
            textDate.text = formatDate(data.dt, response.timezone)
            textTemp.text = data.temp.toInt().toString()
            setTemperatureUnit(requireContext(), textTempUnit, pagerViewModel.prefUnits)
            textDesc.text = weather.description
            textCloud.text = getString(
                R.string.weather_text_cloud,
                data.clouds.toInt()
            )
            textHumidity.text = getString(
                R.string.weather_text_humidity,
                data.humidity.toInt()
            )
            setTemperature(
                requireContext(),
                textFeelsLike,
                data.feels_like.toInt(),
                pagerViewModel.prefUnits
            )
            setWindSpeed(
                requireContext(),
                textWind,
                data.wind_speed.toInt(),
                pagerViewModel.prefUnits
            )
            // TODO(): create converter
            textWindDir.text = response.current.wind_deg.toInt().toString()
            textPressure.text = getString(
                R.string.weather_text_pressure,
                data.pressure.toInt()
            )
        }

        with(binding.layoutSunTime) {
            textSunrise.text = formatTime(data.sunrise, response.timezone)
            textSunset.text = formatTime(data.sunset, response.timezone)
        }

        hourlyAdapter.units = pagerViewModel.prefUnits
        hourlyAdapter.timezone = response.timezone
        hourlyAdapter.submitList(response.hourly)
    }

    private fun formatDate(timestamp: Long, timezone: String): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp * 1000),
            ZoneId.of(timezone)
        )
        return date.format(DateTimeFormatter.ofPattern("E, d MMM"))
    }

    private fun formatTime(timestamp: Long, timezone: String): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp * 1000),
            ZoneId.of(timezone)
        )
        return date.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun recyclerScrollListener() = object : CustomItemTouchListener() {
        var lastX = 0
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_MOVE -> {
                    val isScrollingRight = e.x < lastX
                    val layoutManager = binding.listHourly.layoutManager as LinearLayoutManager
                    val itemCount = binding.listHourly.adapter?.itemCount?.minus(1)
                    pagerViewModel.scrollCallback.value = isScrollingRight &&
                            layoutManager.findLastCompletelyVisibleItemPosition() == itemCount ||
                            !isScrollingRight &&
                            layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                }
                MotionEvent.ACTION_UP -> {
                    lastX = 0
                    pagerViewModel.scrollCallback.value = true
                }
                MotionEvent.ACTION_DOWN -> {
                    lastX = e.x.toInt()
                }
            }
            return false
        }
    }
}
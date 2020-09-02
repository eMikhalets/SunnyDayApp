package com.emikhalets.sunnydayapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentCurrentBinding
import com.emikhalets.sunnydayapp.network.pojo.DataCurrent
import com.emikhalets.sunnydayapp.ui.pager.ViewPagerViewModel
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CurrentWeatherFragment : Fragment() {

    private var _binding: FragmentCurrentBinding? = null
    private val binding get() = _binding!!

    private val pagerViewModel: ViewPagerViewModel by viewModels()
    private val weatherViewModel: CurrentWeatherViewModel by viewModels()

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
        implementObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun implementObservers() {
        pagerViewModel.currentQuery.observe(viewLifecycleOwner, Observer {
            hideInterface()
            hideTextNotice()
            showProgressbar()
            weatherViewModel.requestCurrent(it)
        })

        weatherViewModel.currentWeather.observe(viewLifecycleOwner, Observer {
            Timber.d("Current weather is loaded.")
            Timber.d(it.toString())
            hideProgressbar()
            setWeatherData(it.data.first())
            showInterface()
        })

        weatherViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Timber.d("Error loading weather.")
            Timber.d(it)
            hideProgressbar()
            binding.textNotice.text = it
            showTextNotice()
        })
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
            textHumidity.text = getString(R.string.current_text_humidity, data.humidity.toInt())
            textClouds.text = getString(R.string.current_text_clouds, data.clouds.toInt())
            textVisibility.text = getString(R.string.current_text_visibility, data.visibility)
            textPrecip.text = getString(R.string.current_text_precip, data.precipitation)
            textSnowfall.text = getString(R.string.current_text_snowfall, data.snowfall.toInt())
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

    private fun showTextNotice() {
        binding.textNotice.visibility = View.VISIBLE
    }

    private fun hideTextNotice() {
        binding.textNotice.visibility = View.INVISIBLE
    }

    private fun showProgressbar() {
        binding.pbLoadingWeather.visibility = View.VISIBLE
    }

    private fun hideProgressbar() {
        binding.pbLoadingWeather.visibility = View.INVISIBLE
    }

    private fun showInterface() {
        val alpha = 1f
        val duration = 300L
        val translation = 0f
        binding.textCityName.animate().alpha(alpha).setDuration(duration).start()
        binding.textDate.animate().alpha(alpha).setDuration(duration).start()
        binding.textTemp.animate().alpha(alpha).setDuration(duration).start()
        binding.textDesc.animate().alpha(alpha).setDuration(duration).start()
        binding.imageWeatherIcon.animate().alpha(alpha).setDuration(duration).start()
        binding.cardSunrise.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardSunset.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardPressure.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardWindSpeed.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardWindDir.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardFeelslike.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardHumidity.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardClouds.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardVisibility.animate().translationY(translation).alpha(alpha)
            .setDuration(duration).start()
        binding.cardPrecip.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardSnowfall.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardUv.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
    }

    private fun hideInterface() {
        val alpha = 0f
        val duration = 300L
        val translation = -500f
        binding.textCityName.animate().alpha(alpha).setDuration(duration).start()
        binding.textDate.animate().alpha(alpha).setDuration(duration).start()
        binding.textTemp.animate().alpha(alpha).setDuration(duration).start()
        binding.textDesc.animate().alpha(alpha).setDuration(duration).start()
        binding.imageWeatherIcon.animate().alpha(alpha).setDuration(duration).start()
        binding.cardSunrise.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardSunset.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardPressure.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardWindSpeed.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardWindDir.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardFeelslike.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardHumidity.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardClouds.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardVisibility.animate().translationY(translation).alpha(alpha)
            .setDuration(duration).start()
        binding.cardPrecip.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardSnowfall.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
        binding.cardUv.animate().translationY(translation).alpha(alpha).setDuration(duration)
            .start()
    }
}
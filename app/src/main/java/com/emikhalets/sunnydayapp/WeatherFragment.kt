package com.emikhalets.sunnydayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.emikhalets.sunnydayapp.adapters.WeatherDailyAdapter
import com.emikhalets.sunnydayapp.adapters.WeatherHourlyAdapter
import com.emikhalets.sunnydayapp.databinding.FragmentWeatherBinding
import com.emikhalets.sunnydayapp.network.pojo.DataCurrent
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.emikhalets.sunnydayapp.viewmodels.WeatherViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.lang.Exception

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val dailyAdapter = WeatherDailyAdapter(ArrayList())
    private val hourlyAdapter = WeatherHourlyAdapter(ArrayList())
    private lateinit var viewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        setToolbar()
        weatherDetailsListener()
        binding.listWeatherForecastDaily.adapter = dailyAdapter
        binding.listWeatherForecastHourly.adapter = hourlyAdapter

        viewModel.currentWeather.observe(viewLifecycleOwner, Observer {
            setCurrentWeather(it.data.first())
        })

        viewModel.forecastDaily.observe(viewLifecycleOwner, Observer {
            dailyAdapter.setList(it.data)
        })

        viewModel.forecastHourly.observe(viewLifecycleOwner, Observer {
            hourlyAdapter.setList(it.data)
        })

        viewModel.requestCurrent("Москва")
        viewModel.requestForecastDaily("Москва")
        viewModel.requestForecastHourly("Москва")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setToolbar() {
        val appActivity = activity as AppCompatActivity
        appActivity.let {
            appActivity.setSupportActionBar(binding.toolbar)
            binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
            binding.toolbar.setNavigationOnClickListener(cityListClickListener())
        }
    }

    private fun cityListClickListener(): View.OnClickListener {
        return View.OnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.cityListFragment)
        }
    }

    private fun weatherDetailsListener() {
        binding.textDetails.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.currentFragment)
        }
    }

    private fun setCurrentWeather(data: DataCurrent) {
        // TODO: icon loading don't work on api 21
        Picasso.get().load(buildIconUrl(data.weather.icon)).into(binding.imageWeatherIcon)
        with(binding) {
            textCity.text = data.cityName
            textTemp.text = getString(R.string.weather_text_temp, data.temperature.toInt())
            textDesc.text = data.weather.description
            textPressure.text = getString(R.string.weather_text_pressure, data.pressure)
            textHumidity.text = getString(R.string.weather_text_humidity, data.humidity)
            textWind.text = getString(R.string.weather_text_wind, data.windSpeed)
        }
    }
}
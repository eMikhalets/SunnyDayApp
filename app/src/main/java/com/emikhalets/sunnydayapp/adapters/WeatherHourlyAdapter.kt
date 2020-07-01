package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.ItemWeatherForecastHourlyBinding
import com.emikhalets.sunnydayapp.network.pojo.DataHourly
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso

class WeatherHourlyAdapter(private var forecastList: List<DataHourly>) :
    RecyclerView.Adapter<WeatherHourlyAdapter.ViewHolder>() {

    fun setList(list: List<DataHourly>) {
        forecastList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeatherForecastHourlyBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = if (forecastList.isEmpty()) 0 else 3

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bind(forecastList[p1])

    inner class ViewHolder(private val binding: ItemWeatherForecastHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataHourly) {
            Picasso.get().load(buildIconUrl(item.weather.icon)).into(binding.imageWeather)

            with(binding) {
                textTime.text = item.timeLocal
                textTemp.text = binding.root.context.getString(
                    R.string.weather_forecast_hourly_text_temp, item.temperature
                )
            }
        }
    }
}
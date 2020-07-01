package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.ItemWeatherForecastDailyBinding
import com.emikhalets.sunnydayapp.network.pojo.DataDaily
import com.emikhalets.sunnydayapp.network.pojo.DataHourly
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso

class WeatherDailyAdapter(private var forecastList: List<DataDaily>) :
    RecyclerView.Adapter<WeatherDailyAdapter.ViewHolder>() {

    fun setList(list: List<DataDaily>) {
        forecastList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWeatherForecastDailyBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = if (forecastList.isEmpty()) 0 else 3

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bind(forecastList[p1])

    inner class ViewHolder(private val binding: ItemWeatherForecastDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataDaily) {
            Picasso.get().load(buildIconUrl(item.weather.icon)).into(binding.imageWeather)

            with(binding) {
                textDate.text = item.date
                textTemp.text = binding.root.context.getString(
                    R.string.weather_forecast_daily_text_temp,
                    item.temperatureHigh,
                    item.temperatureMin
                )
            }
        }
    }
}
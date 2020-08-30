package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.network.pojo.DataHourly
import com.emikhalets.sunnydayapp.databinding.ItemForecastHourlyBinding
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso

class HourlyAdapter : ListAdapter<DataHourly, HourlyAdapter.ViewHolder>(HourlyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemForecastHourlyBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemForecastHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataHourly) {
            Picasso.get().load(buildIconUrl(item.weather.icon)).into(binding.imageWeather)

            with(binding) {
                textTime.text = item.timeLocal
                textDesc.text = item.weather.description
                textTemp.text = binding.root.context.getString(
                    R.string.forecast_hourly_text_temp,
                    item.temperature
                )
                textFeelslike.text = binding.root.context.getString(
                    R.string.forecast_daily_text_feelslike,
                    item.tempFeelsLike
                )
                textPressure.text = binding.root.context.getString(
                    R.string.forecast_daily_text_pressure,
                    item.pressure
                )
                textHumidity.text = binding.root.context.getString(
                    R.string.forecast_daily_text_humidity,
                    item.humidity
                )
                textWind.text = binding.root.context.getString(
                    R.string.forecast_daily_text_wind,
                    item.windSpeed
                )
                textWindDir.text = item.windDirAbbr
            }
        }
    }
}
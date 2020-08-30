package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.network.pojo.DataDaily
import com.emikhalets.sunnydayapp.databinding.ItemForecastDailyBinding
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter : ListAdapter<DataDaily, DailyAdapter.ViewHolder>(DailyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemForecastDailyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemForecastDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataDaily) {
            Picasso.get().load(buildIconUrl(item.weather.icon)).into(binding.imageWeather)

            with(binding) {
                textDate.text = convertDate(item.timestamp)
                textDesc.text = item.weather.description
                textTemp.text = binding.root.context.getString(
                    R.string.forecast_daily_text_temp,
                    item.temperatureHigh.toInt(),
                    item.temperatureMin.toInt()
                )
                textFeelslike.text = binding.root.context.getString(
                    R.string.forecast_daily_text_feelslike,
                    item.tempFeelsLikeMax.toInt(),
                    item.tempFeelsLikeMin.toInt()
                )
                textPressure.text = binding.root.context.getString(
                    R.string.forecast_daily_text_pressure,
                    item.pressure
                )
                textHumidity.text = binding.root.context.getString(
                    R.string.forecast_daily_text_humidity,
                    item.humidity.toInt()
                )
                textWind.text = binding.root.context.getString(
                    R.string.forecast_daily_text_wind,
                    item.windSpeed
                )
                textWindDir.text = item.windDirAbbr
            }
        }

        private fun convertDate(timestamp: Long): String {
            val calendar = Calendar.getInstance()
            // Timestamp in seconds, convert to milliseconds
            calendar.timeInMillis = timestamp * 1000
            val simpleFormat = SimpleDateFormat("d MMM", Locale.US)
            return simpleFormat.format(calendar.time)
        }
    }
}
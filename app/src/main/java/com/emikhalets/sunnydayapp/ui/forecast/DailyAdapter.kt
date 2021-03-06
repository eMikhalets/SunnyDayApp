package com.emikhalets.sunnydayapp.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Daily
import com.emikhalets.sunnydayapp.databinding.ItemDailyBinding
import com.emikhalets.sunnydayapp.utils.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DailyAdapter : ListAdapter<Daily, DailyAdapter.ViewHolder>(DailyDiffCallback()) {

    var timezone: String = ""
    var currentWeather: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDailyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Daily) {
            with(binding) {
                setBackground(item.weather.first().icon)
                imageIcon.load(buildIconUrl(item.weather.first().icon))
                textDate.text = formatDateWithWeek(item.dt, timezone)
                setTemperature(root.context, textTempDay, item.temp.day.toInt())
                setTemperature(root.context, textTempNight, item.temp.night.toInt())
                setFeelsLike(root.context, textFeelsLike, item.feels_like.averageFeelsLike)
                textDesc.text = item.weather.first().description
                textPressure.text = root.context.getString(
                    R.string.forecast_text_pressure,
                    item.pressure.toInt()
                )
                textHumidity.text = root.context.getString(
                    R.string.forecast_text_humidity,
                    item.humidity.toInt()
                )
                setWindSpeed(root.context, textWind, item.wind_speed.toInt())
            }
        }

        private fun setBackground(icon: String) {
            if (!currentWeather.contains("n")) {
                binding.root.setBackgroundColor(getBackgroundColor(binding.root.context, icon))
            }
        }

        private fun formatDateWithWeek(timestamp: Long, timezone: String): String {
            val date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp * 1000),
                ZoneId.of(timezone)
            )
            return date.format(DateTimeFormatter.ofPattern("E, d MMM y"))
        }
    }

    private class DailyDiffCallback : DiffUtil.ItemCallback<Daily>() {

        override fun areItemsTheSame(oldItem: Daily, newItem: Daily) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily) = oldItem == newItem
    }
}
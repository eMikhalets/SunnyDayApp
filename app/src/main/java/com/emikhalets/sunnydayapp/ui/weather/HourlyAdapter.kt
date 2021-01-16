package com.emikhalets.sunnydayapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Hourly
import com.emikhalets.sunnydayapp.databinding.ItemHourlyBinding
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HourlyAdapter : ListAdapter<Hourly, HourlyAdapter.ViewHolder>(HourlyDiffCallback()) {

    var timezone: String = ""
    var maxTemp = 0.0
    var minTemp = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHourlyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.bind(getItem(position), position, null, getItem(position + 1))
            }
            itemCount -> {
                holder.bind(getItem(position), position, getItem(position - 1), null)
            }
            else -> {
                holder.bind(
                    getItem(position),
                    position,
                    getItem(position - 1),
                    getItem(position + 1)
                )
            }
        }
    }

    inner class ViewHolder(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hourly, position: Int, prev: Hourly?, next: Hourly?) {
            Picasso.get().load(buildIconUrl(item.weather.first().icon)).into(binding.imageIcon)

            with(binding) {
                textTime.text = formatTime(item.dt, timezone)
                textTemp.text = root.context.getString(
                    R.string.weather_text_temp, item.temp.toInt()
                )
                textFeelsLike.text = root.context.getString(
                    R.string.weather_text_feels_like,
                    item.feels_like.toInt()
                )

                chart.prevTemp = prev?.temp ?: 0.0
                chart.currentTemp = item.temp
                chart.nextTemp = next?.temp ?: 0.0
                chart.tempText = root.context.getString(
                    R.string.weather_text_temp, item.temp.toInt()
                )
                chart.maxTemp = maxTemp
                chart.minTemp = minTemp
            }
        }

        private fun formatTime(timestamp: Long, timezone: String): String {
            val time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp * 1000),
                ZoneId.of(timezone)
            )
            return time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    private class HourlyDiffCallback : DiffUtil.ItemCallback<Hourly>() {

        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly) = oldItem == newItem
    }
}
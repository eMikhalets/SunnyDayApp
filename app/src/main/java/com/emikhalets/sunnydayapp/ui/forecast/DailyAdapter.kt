package com.emikhalets.sunnydayapp.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.Daily
import com.emikhalets.sunnydayapp.data.model.FellsLike
import com.emikhalets.sunnydayapp.databinding.ItemDailyBinding
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DailyAdapter : ListAdapter<Daily, DailyAdapter.ViewHolder>(DailyDiffCallback()) {

    var timezone: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDailyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), timezone)
    }

    class ViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Daily, timezone: String) {
            with(binding) {
                Picasso.get().load(buildIconUrl(item.weather.first().icon)).into(imageIcon)

                textDate.text = formatDate(item.dt, timezone)
                textTempDay.text = root.context.getString(
                    R.string.forecast_text_temp_day,
                    item.temp.day.toInt()
                )
                textTempNight.text = root.context.getString(
                    R.string.forecast_text_temp_night,
                    item.temp.night.toInt()
                )
                textFeelsLike.text = root.context.getString(
                    R.string.forecast_text_feels_like,
                    averageFeelsLike(item.feels_like)
                )
                textDesc.text = item.weather.first().description
                textPressure.text = root.context.getString(
                    R.string.forecast_text_pressure,
                    item.pressure.toInt()
                )
                textHumidity.text = root.context.getString(
                    R.string.forecast_text_humidity,
                    item.humidity.toInt()
                )
                textWind.text = root.context.getString(
                    R.string.forecast_text_wind,
                    item.wind_speed.toInt()
                )
            }
        }

        private fun formatDate(timestamp: Long, timezone: String): String {
            val date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp * 1000),
                ZoneId.of(timezone)
            )
            return date.format(DateTimeFormatter.ofPattern("E, d MMM y"))
        }

        private fun averageFeelsLike(fl: FellsLike): Int =
            (fl.morn + fl.day + fl.eve + fl.night).toInt() / 4
    }

    private class DailyDiffCallback : DiffUtil.ItemCallback<Daily>() {

        override fun areItemsTheSame(oldItem: Daily, newItem: Daily) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily) = oldItem == newItem
    }
}
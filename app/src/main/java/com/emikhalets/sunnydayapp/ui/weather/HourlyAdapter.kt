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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHourlyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), timezone)
    }

    inner class ViewHolder(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hourly, timezone: String) {
            Picasso.get().load(buildIconUrl(item.weather.first().icon)).into(binding.imageIcon)

            with(binding) {
                textTime.text = formatTime(item.dt, timezone)
                textTemp.text = root.context.getString(R.string.weather_text_temp, item.temp)
                textFeelsLike.text = root.context.getString(
                    R.string.weather_text_feels_like,
                    item.feels_like
                )
            }
        }

        private fun formatTime(timestamp: Long, timezone: String): String {
            val time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.of(timezone)
            )
            return time.format(DateTimeFormatter.ofPattern("H:m"))
        }
    }

    private class HourlyDiffCallback : DiffUtil.ItemCallback<Hourly>() {

        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly) = oldItem == newItem
    }
}
package com.emikhalets.sunnydayapp.ui.weather

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.data.model.Hourly
import com.emikhalets.sunnydayapp.databinding.ItemHourlyBinding
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.emikhalets.sunnydayapp.utils.setTemperature
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HourlyAdapter(private val context: Context) :
    ListAdapter<Hourly, HourlyAdapter.ViewHolder>(HourlyDiffCallback()) {

    var timezone: String = ""
    var units = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHourlyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hourly) {
            Picasso.get().load(buildIconUrl(item.weather.first().icon)).into(binding.imageIcon)

            with(binding) {
                textTime.text = formatTime(item.dt, timezone)
                setTemperature(context, textTemp, item.temp.toInt(), units)
                setTemperature(context, textFeelsLike, item.feels_like.toInt(), units)
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
package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.ItemForecastDailyBinding
import com.emikhalets.sunnydayapp.network.pojo.DataDaily
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DailyAdapter(private val timezone: String, private val dailyClick: DailyForecastItemClick) :
    ListAdapter<DataDaily, DailyAdapter.ViewHolder>(DailyDiffCallback()) {

    interface DailyForecastItemClick {
        fun onDailyForecastClick(dailyForecast: DataDaily) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemForecastDailyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), timezone, dailyClick)
    }

    class ViewHolder(private val binding: ItemForecastDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataDaily, timezone: String, dailyClick: DailyForecastItemClick) {
            Picasso.get().load(buildIconUrl(item.weather.icon)).into(binding.imageIcon)

            with(binding) {
                textDate.text = formatDate(item.timestamp, timezone)
                textTempMax.text =
                    root.context.getString(
                        R.string.forecast_daily_text_temp,
                        item.temperatureMax.toInt()
                    )
                textTempMin.text =
                    root.context.getString(
                        R.string.forecast_daily_text_temp,
                        item.temperatureMin.toInt()
                    )

                root.setOnClickListener { dailyClick.onDailyForecastClick(item) }
            }
        }

        private fun formatDate(timestamp: Long, timezone: String): String {
            val dateNow = LocalDateTime.now()
            val dateTomorrow = dateNow.plusDays(1)
            val date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp * 1000),
                ZoneId.of(timezone)
            )
            val formatter = DateTimeFormatter.ofPattern("E, d")

            return when {
                dateNow.format(formatter) == date.format(formatter) -> {
                    binding.root.context.getString(R.string.forecast_daily_text_today)
                }
                dateTomorrow.format(formatter) == date.format(formatter) -> {
                    binding.root.context.getString(R.string.forecast_daily_text_tomorrow)
                }
                else -> {
                    date.format(formatter)
                }
            }
        }
    }
}
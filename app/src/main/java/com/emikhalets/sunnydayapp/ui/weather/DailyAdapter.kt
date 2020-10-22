package com.emikhalets.sunnydayapp.ui.weather

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.model.DataDaily
import com.emikhalets.sunnydayapp.databinding.ItemForecastDailyBinding
import com.emikhalets.sunnydayapp.utils.AppHelper
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DailyAdapter(
    private val context: Context,
    private val timezone: String,
    private val dailyClick: DailyForecastItemClick
) :
    ListAdapter<DataDaily, DailyAdapter.ViewHolder>(DailyDiffCallback()) {

    private val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val prefTemp = pref.getString(context.getString(R.string.pref_key_temp), "C")!!

    interface DailyForecastItemClick {
        fun onDailyForecastClick(dailyForecast: DataDaily)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemForecastDailyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), timezone, dailyClick)
    }

    inner class ViewHolder(private val binding: ItemForecastDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataDaily, timezone: String, dailyClick: DailyForecastItemClick) {
            Picasso.get().load(AppHelper.buildIconUrl(item.weather.icon)).into(binding.imageIcon)

            with(binding) {
                textDate.text = formatDate(item.timestamp, timezone)
                AppHelper.setTempUnit(root.context, textTempMax, item.temperatureMax, prefTemp)
                AppHelper.setTempUnit(root.context, textTempMin, item.temperatureMin, prefTemp)

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
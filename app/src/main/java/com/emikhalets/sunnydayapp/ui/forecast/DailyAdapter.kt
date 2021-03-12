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

class DailyAdapter : ListAdapter<Daily, DailyAdapter.ViewHolder>(DailyDiffCallback()) {

    var timezone: String = ""
//    var currentWeather: String = ""

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
//                setColors(item.weather.first().icon)
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

//        private fun setColors(weather: String) {
//            if (!currentWeather.contains("n")) {
//                when (weather) {
//                    "01d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClear)
//                    )
//                    "02d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//                    )
//                    "03d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//                    )
//                    "04d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//                    )
//                    "09d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryRain)
//                    )
//                    "10d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryRain)
//                    )
//                    "11d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorTextNight),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryStorm)
//                    )
//                    "13d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimarySnow)
//                    )
//                    "50d" -> setColors(
//                        ContextCompat.getColor(binding.root.context, R.color.colorText),
//                        ContextCompat.getColor(binding.root.context, R.color.colorPrimaryMist)
//                    )
//                }
//            }
//        }
//
//        private fun setColors(text: Int, bg: Int) {
//            with(binding) {
//                textDate.setTextColor(text)
//                textDesc.setTextColor(text)
//                textTempDay.setTextColor(text)
//                textTempNight.setTextColor(text)
//                textFeelsLike.setTextColor(text)
//                textPressure.setTextColor(text)
//                textHumidity.setTextColor(text)
//                textWind.setTextColor(text)
//                root.setBackgroundColor(bg)
//            }
//        }
    }

    private class DailyDiffCallback : DiffUtil.ItemCallback<Daily>() {

        override fun areItemsTheSame(oldItem: Daily, newItem: Daily) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily) = oldItem == newItem
    }
}
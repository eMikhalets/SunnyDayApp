package com.emikhalets.sunnydayapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.emikhalets.sunnydayapp.data.model.Hourly
import com.emikhalets.sunnydayapp.databinding.ItemHourlyBinding
import com.emikhalets.sunnydayapp.utils.buildIconUrl
import com.emikhalets.sunnydayapp.utils.formatTime
import com.emikhalets.sunnydayapp.utils.setTemperature

class HourlyAdapter : ListAdapter<Hourly, HourlyAdapter.ViewHolder>(HourlyDiffCallback()) {

    var timezone: String = ""

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
            with(binding) {
//                setColors(item.weather.first().icon)
                imageIcon.load(buildIconUrl(item.weather.first().icon))
                textTime.text = formatTime(item.dt, timezone)
                setTemperature(root.context, textTemp, item.temp.toInt())
            }
        }

//        private fun setColors(weather: String) {
//            when (weather) {
//                "01d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClear)
//                )
//                "02d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//                )
//                "03d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//                )
//                "04d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryClouds)
//                )
//                "09d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryRain)
//                )
//                "10d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryRain)
//                )
//                "11d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorTextNight),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryStorm)
//                )
//                "13d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimarySnow)
//                )
//                "50d" -> setColors(
//                    ContextCompat.getColor(binding.root.context, R.color.colorText),
//                    ContextCompat.getColor(binding.root.context, R.color.colorPrimaryMist)
//                )
//            }
//        }
//
//        private fun setColors(text: Int, bg: Int) {
//            with(binding) {
//                textTime.setTextColor(text)
//                textTemp.setTextColor(text)
//                root.setBackgroundColor(bg)
//            }
//        }
    }

    private class HourlyDiffCallback : DiffUtil.ItemCallback<Hourly>() {

        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly) = oldItem == newItem
    }
}
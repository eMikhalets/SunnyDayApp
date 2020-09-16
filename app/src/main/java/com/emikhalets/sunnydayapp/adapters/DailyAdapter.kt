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
            Picasso.get().load(buildIconUrl(item.weather.icon)).into(binding.imageIcon)

            with(binding) {
                textDate.text = "SomeDate"
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
            }
        }

//        private fun formatDate(timestamp: Long): String {
//            val date = Instant.ofEpochSecond(timestamp)
//            val formatter = DateTimeFormatter.ofPattern("dd\nE")
//            return date.format(formatter)
//        }
    }
}
package com.emikhalets.sunnydayapp.adapters

import androidx.recyclerview.widget.DiffUtil
import com.emikhalets.sunnydayapp.network.pojo.DataHourly

class HourlyDiffCallback : DiffUtil.ItemCallback<DataHourly>() {

    override fun areItemsTheSame(oldItem: DataHourly, newItem: DataHourly) =
        oldItem.datetime == newItem.datetime

    override fun areContentsTheSame(oldItem: DataHourly, newItem: DataHourly) = oldItem == newItem

}
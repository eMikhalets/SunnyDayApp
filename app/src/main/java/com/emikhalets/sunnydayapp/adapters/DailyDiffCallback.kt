package com.emikhalets.sunnydayapp.adapters

import androidx.recyclerview.widget.DiffUtil
import com.emikhalets.sunnydayapp.network.pojo.DataDaily

class DailyDiffCallback : DiffUtil.ItemCallback<DataDaily>() {

    override fun areItemsTheSame(oldItem: DataDaily, newItem: DataDaily) =
        oldItem.hashCode() == newItem.hashCode()

    override fun areContentsTheSame(oldItem: DataDaily, newItem: DataDaily) = oldItem == newItem

}
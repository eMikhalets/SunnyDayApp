package com.emikhalets.sunnydayapp.adapters

import androidx.recyclerview.widget.DiffUtil
import com.emikhalets.sunnydayapp.data.database.City

class CitiesDiffCallback : DiffUtil.ItemCallback<City>() {

    override fun areItemsTheSame(oldItem: City, newItem: City) =
        oldItem.hashCode() == newItem.hashCode()

    override fun areContentsTheSame(oldItem: City, newItem: City) = oldItem == newItem

}
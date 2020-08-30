package com.emikhalets.sunnydayapp.adapters

import androidx.recyclerview.widget.DiffUtil
import com.emikhalets.sunnydayapp.data.database.City

class CitiesDiffCallback : DiffUtil.ItemCallback<City>() {

    override fun areItemsTheSame(oldItem: City, newItem: City) = oldItem.cityId == newItem.cityId

    override fun areContentsTheSame(oldItem: City, newItem: City) = oldItem == newItem

}
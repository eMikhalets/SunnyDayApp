package com.emikhalets.sunnydayapp.ui.citylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.ItemCityBinding

class CitiesAdapter(private val click: OnCityClick) :
    ListAdapter<City, CitiesAdapter.ViewHolder>(CitiesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCityBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), click)
    }

    class ViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: City, click: OnCityClick) {
            with(binding) {
                textName.text = root.context.getString(
                    R.string.cities_text_list_item,
                    item.name,
                    item.country
                )
                root.setOnClickListener { click.onCityClick(item) }
                root.setOnLongClickListener {
                    click.onCityLongClick(item)
                    true
                }
            }
        }
    }

    interface OnCityClick {
        fun onCityClick(city: City)
        fun onCityLongClick(city: City)
    }

    private class CitiesDiffCallback : DiffUtil.ItemCallback<City>() {

        override fun areItemsTheSame(oldItem: City, newItem: City) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: City, newItem: City) =
            oldItem == newItem
    }
}
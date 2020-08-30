package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.ItemCityBinding

class CitiesAdapter(private val click: CityClick) :
    ListAdapter<City, CitiesAdapter.ViewHolder>(CitiesDiffCallback()) {

    interface CityClick {
        fun onCityClick(city: City)
        fun onCityLongClick(city: City)
    }

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

        fun bind(item: City, click: CityClick) {
            with(binding) {
                textName.text = binding.root.context.getString(
                    R.string.cities_adapter_text_query,
                    item.cityName,
                    item.countryFull
                )

                root.setOnClickListener { click.onCityClick(item) }
                root.setOnLongClickListener {
                    click.onCityLongClick(item)
                    true
                }
            }
        }
    }
}
package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.databinding.ItemCityBinding

class CitiesAdapter(private var citiesList: List<City>, val listener: OnCityClickListener) :
    RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    fun setList(list: List<City>) {
        citiesList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCityBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = citiesList.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bind(citiesList[p1])

    interface OnCityClickListener {
        fun onCityClick(city: City)
        fun onCityLongClick(city: City)
    }

    inner class ViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: City) {
            with(binding) {
                textName.text = binding.root.context.getString(
                    R.string.cities_adapter_text_query,
                    item.cityName,
                    item.countryFull
                )
            }

            binding.root.setOnClickListener { listener.onCityClick(item) }
            binding.root.setOnLongClickListener {
                listener.onCityLongClick(item)
                true
            }
        }
    }
}
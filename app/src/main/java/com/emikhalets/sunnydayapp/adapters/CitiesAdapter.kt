package com.emikhalets.sunnydayapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.databinding.ItemCityBinding
import com.emikhalets.sunnydayapp.network.pojo.DataDaily

class CitiesAdapter(private var citiesList: List<City>) :
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

    inner class ViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: City) {
            with(binding) {
                textName.text = item.cityName
            }
        }
    }
}
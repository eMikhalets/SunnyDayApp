package com.emikhalets.sunnydayapp.ui.citylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.database.City
import com.emikhalets.sunnydayapp.databinding.ItemCityBinding

class CitiesAdapter(
    private val click: (City) -> Unit,
    private val longClick: (City) -> Unit
) : ListAdapter<City, CitiesAdapter.ViewHolder>(CitiesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCityBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.bind(getItem(position))
            holder.itemView.setOnClickListener { click.invoke(getItem(position)) }
            holder.itemView.setOnLongClickListener {
                longClick.invoke(getItem(position))
                true
            }
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }

    class ViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: City) {
            binding.textName.text = binding.root.context.getString(
                R.string.cities_text_list_item,
                item.name,
                item.country
            )
        }
    }

    private class CitiesDiffCallback : DiffUtil.ItemCallback<City>() {

        override fun areItemsTheSame(oldItem: City, newItem: City) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: City, newItem: City) =
            oldItem == newItem
    }
}
package com.emikhalets.sunnydayapp.ui.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CityListFragment()
        else -> WeatherFragment()
    }

    override fun getItemCount(): Int = 2
}
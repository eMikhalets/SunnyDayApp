package com.emikhalets.sunnydayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.data.City
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.viewmodels.ViewPagerViewModel
import timber.log.Timber

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PagerAdapter(this)
        binding.viewPager.adapter = adapter
//        val viewModel = ViewModelProvider(this).get(ViewPagerViewModel::class.java)
//        viewModel.cities.observe(viewLifecycleOwner, Observer {
//            cities = it as ArrayList<City>
//            adapter.countPages = it.size
//        })
//        viewModel.getAllCities()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private inner class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        var countPages = 1
        override fun createFragment(position: Int): Fragment = WeatherFragment()
        override fun getItemCount(): Int = countPages
    }
}
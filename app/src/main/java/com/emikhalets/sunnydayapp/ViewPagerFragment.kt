package com.emikhalets.sunnydayapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding

public class ViewPagerFragment : Fragment() {

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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private inner class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment {
            TODO("return fragment by position in DB")
        }

        override fun getItemCount(): Int {
            TODO("return count of DB rows")
        }
    }
}
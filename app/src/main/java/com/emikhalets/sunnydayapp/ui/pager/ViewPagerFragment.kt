package com.emikhalets.sunnydayapp.ui.pager

import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.forecast.ForecastDailyFragment
import com.emikhalets.sunnydayapp.ui.weather.CurrentWeatherFragment
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var searchAdapter: SimpleCursorAdapter
    private val viewModel: ViewPagerViewModel by viewModels()

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

        savedInstanceState?.let { convertCitiesCitiesToDB() }
        implementObservers()
        implementListeners()
        implementSearch()

        pagerAdapter = PagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        attachTabsAndPager()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun implementObservers() {
        viewModel.searchingCities.observe(viewLifecycleOwner, Observer {
            val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "city_name"))
            for (i in it.indices) cursor.addRow(arrayOf(i, it[i]))
            searchAdapter.changeCursor(cursor)
            searchView.suggestionsAdapter = searchAdapter
        })

        viewModel.currentQuery.observe(viewLifecycleOwner, Observer {
            binding.toolbar.subtitle = it
            // TODO: App crash if page is changed, I DON'T KNOW WHY!!!!
            //binding.viewPager.setCurrentItem(1, true)
        })
    }

    private fun implementSearch() {
        searchView = binding.toolbar.menu.findItem(R.id.menu_pager_search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                searchAdapter.changeCursor(null)
                if (newText.length >= 2) viewModel.getCitiesByName(newText)
                return true
            }
        })

        implementSuggestionsAdapter()
    }

    private fun implementListeners() {
        binding.toolbar.findViewById<View>(R.id.menu_pager_preference).setOnClickListener {
            Timber.d("Settings Click")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_viewPagerFragment_to_preferencePagerFragment)
        }
    }

    private fun implementSuggestionsAdapter() {
        searchAdapter = SimpleCursorAdapter(
            requireContext(), android.R.layout.simple_list_item_1, null, arrayOf("city_name"),
            intArrayOf(android.R.id.text1), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(p0: Int): Boolean = false

            override fun onSuggestionClick(p0: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(p0) as Cursor
                val name = cursor.getString(cursor.getColumnIndex("city_name"))
                cursor.close()
                Timber.d("Select in search: $name")

                searchView.setQuery(name, false)
                searchView.onActionViewCollapsed()
                searchView.setQuery(null, false)

                binding.toolbar.subtitle = name
                viewModel.updateCurrentQuery(name)
                searchAdapter.changeCursor(null)
                viewModel.changeIsAddedCity(name)
                Timber.d("Query updated: $name")
                return true
            }
        })
    }

    private fun attachTabsAndPager() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_title_city_list)
                1 -> tab.text = getString(R.string.tab_title_current)
                2 -> tab.text = getString(R.string.tab_title_forecast)
            }
            tab.select()
        }.attach()
    }

    private fun convertCitiesCitiesToDB() {
        val sp = requireContext().getSharedPreferences(getString(R.string.sp_file_name), 0)
        if (sp.getBoolean(getString(R.string.sp_is_first_launch), true)) {
            requireContext().assets.open("cities_20000.json").bufferedReader().use { bufferReader ->
                val json = bufferReader.use { it.readText() }
                viewModel.parseAndInsertToDB(json)
            }
            sp.edit().putBoolean(getString(R.string.sp_is_first_launch), false).apply()
        }
        Timber.d("Cities database is created.")
    }

    private class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CityListFragment()
            1 -> CurrentWeatherFragment()
            else -> ForecastDailyFragment()
        }

        override fun getItemCount(): Int = 3
    }
}

package com.emikhalets.sunnydayapp

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.utils.CURRENT_QUERY
import com.emikhalets.sunnydayapp.utils.SP_FILE_NAME
import com.emikhalets.sunnydayapp.utils.SP_FIRST_LAUNCH
import com.emikhalets.sunnydayapp.viewmodels.ViewPagerViewModel
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PagerAdapter
    private lateinit var searchView: SearchView
    private lateinit var viewModel: ViewPagerViewModel
    private lateinit var searchAdapter: SimpleCursorAdapter

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

        viewModel = ViewModelProvider(this).get(ViewPagerViewModel::class.java)
        adapter = PagerAdapter(this)
        binding.viewPager.adapter = adapter

        observeLiveData()
        implementToolbar()
        attachTabsAndPager()

        if (savedInstanceState == null) {
            convertCitiesCitiesToDB()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeLiveData() {
        viewModel.searchingCities.observe(viewLifecycleOwner, Observer {
            val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "city_name"))
            for (i in it.indices) {
                cursor.addRow(arrayOf(i, it[i]))
            }
            searchAdapter.changeCursor(cursor)
            searchView.suggestionsAdapter = searchAdapter
        })
    }

    private fun implementToolbar() {
        with(binding.toolbar) {
            title = getString(R.string.app_name)
            subtitle = getString(R.string.toolbar_subtitle)
            inflateMenu(R.menu.menu_view_pager)
        }

        searchView = binding.toolbar.menu.findItem(R.id.menu_pager_search).actionView as SearchView
        searchView.queryHint = "Search"

        searchTextListener()
        implementSuggestionsAdapter()
        searchSuggestionListener()
    }

    private fun implementSuggestionsAdapter() {
        val from = arrayOf("city_name")
        val to = intArrayOf(android.R.id.text1)
        searchAdapter = SimpleCursorAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
    }

    private fun searchTextListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                searchAdapter.changeCursor(null)
                if (newText.length >= 3) {
                    viewModel.getCitiesByName(newText)
                }
                return false
            }
        })
    }

    private fun searchSuggestionListener() {
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(p0: Int): Boolean = false

            override fun onSuggestionClick(p0: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(p0) as Cursor
                val name = cursor.getString(cursor.getColumnIndex("city_name"))
                cursor.close()

                searchView.setQuery(name, false)
                searchView.onActionViewCollapsed()
                searchView.setQuery(null, false)

                binding.toolbar.subtitle = name
                CURRENT_QUERY.value = name
                searchAdapter.changeCursor(null)
                viewModel.insertCity(name)
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
        val sp = requireContext().getSharedPreferences(SP_FILE_NAME, 0)
        if (sp.getBoolean(SP_FIRST_LAUNCH, true)) {
            requireContext().assets.open("cities_20000.json").bufferedReader().use { bufferReader ->
                val json = bufferReader.use { it.readText() }
                viewModel.parseAndInsertToDB(json)
            }
            sp.edit().putBoolean(SP_FIRST_LAUNCH, false).apply()
        }
    }

    private inner class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CityListFragment()
            1 -> CurrentWeatherFragment()
            else -> ForecastDailyFragment()
        }

        override fun getItemCount(): Int = 3
    }
}

package com.emikhalets.sunnydayapp.ui.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.MainViewModel
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.forecast.ForecastFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment
import com.emikhalets.sunnydayapp.utils.Conf
import com.emikhalets.sunnydayapp.utils.CustomSearchQueryListener
import com.emikhalets.sunnydayapp.utils.State
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initSearchView()
        mainViewModel.database.observe(viewLifecycleOwner) { updateInterface(it) }
        mainViewModel.error.observe(viewLifecycleOwner) { errorObserver(it) }
        mainViewModel.weather.observe(viewLifecycleOwner) { weatherObserver() }
        mainViewModel.prefs.observe(viewLifecycleOwner) { preferencesObserver(it) }
        mainViewModel.location.observe(viewLifecycleOwner) { locationObserver() }
        mainViewModel.selecting.observe(viewLifecycleOwner) { selectSearchingObserver() }
        mainViewModel.scrollCallback.observe(viewLifecycleOwner) { scrollCallbackObserver(it) }
        binding.toolbar.findViewById<View>(R.id.menu_pager_preference)
            .setOnClickListener { onSettingsClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initViewPager() {
        pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_title_city_list)
                1 -> tab.text = getString(R.string.tab_title_current)
                else -> tab.text = getString(R.string.tab_title_forecast)
            }
        }.attach()
    }

    private fun initSearchView() {
        searchView = binding.toolbar.menu.findItem(R.id.menu_pager_search).actionView as SearchView
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.viewPager.setCurrentItem(0, true)
        }
        searchView.setOnQueryTextListener(searchTextListener())
        searchView.setOnCloseListener(searchCloseListener())
    }

    private fun errorObserver(message: String) {
        binding.textNotice.text = message
        updateInterface(State.ERROR)
    }

    private fun weatherObserver() {
        with(binding) {
            toolbar.subtitle = mainViewModel.currentCity
            if (viewPager.currentItem == 0) viewPager.setCurrentItem(1, true)
        }
    }

    private fun preferencesObserver(map: Map<String, String>) {
        val lang = map[KEY_LANG] ?: ""
        val units = map[KEY_UNITS] ?: ""
        if (units != mainViewModel.currentUnits || lang != mainViewModel.currentLang) {
            mainViewModel.currentLang = lang
            mainViewModel.currentUnits = units
            Conf.lang = lang
            Conf.units = units
            if (mainViewModel.currentLat != 0.0 && mainViewModel.currentLong != 0.0) {
                mainViewModel.sendWeatherRequest(
                    mainViewModel.currentLat,
                    mainViewModel.currentLong
                )
            }
        }
    }

    private fun selectSearchingObserver() {
        searchView.setQuery("", false)
        searchView.onActionViewCollapsed()
        binding.viewPager.setCurrentItem(1, true)
    }

    private fun locationObserver() {
        binding.viewPager.setCurrentItem(1, true)
    }

    private fun scrollCallbackObserver(isCanScroll: Boolean) {
        binding.viewPager.isUserInputEnabled = isCanScroll
    }

    private fun onSettingsClick() {
        val action = ViewPagerFragmentDirections.actionViewPagerFragmentToPreferencePagerFragment()
        binding.root.findNavController().navigate(action)
    }

    private fun searchTextListener() = object : CustomSearchQueryListener() {
        override fun onQueryTextChange(newText: String): Boolean {
            mainViewModel.searchCitiesInDb(newText)
            return true
        }
    }

    // TODO: need to click on close twice if text was typed
    private fun searchCloseListener() = SearchView.OnCloseListener {
        mainViewModel.cancelSearchingCities()
        searchView.setQuery("", false)
        searchView.onActionViewCollapsed()
        true
    }

    private fun updateInterface(state: State) {
        val duration = 500L
        with(binding) {
            when (state) {
                State.LOADING -> {
                    textNotice.animate().alpha(1f).setDuration(duration).start()
                    pbDbCreating.animate().alpha(1f).setDuration(duration).start()
                    viewPager.animate().alpha(0f).setDuration(duration).start()
                    tabs.animate().alpha(0f).setDuration(duration).start()
                }
                State.LOADED -> {
                    textNotice.animate().alpha(0f).setDuration(duration).start()
                    pbDbCreating.animate().alpha(0f).setDuration(duration).start()
                    viewPager.animate().alpha(1f).setDuration(duration).start()
                    tabs.animate().alpha(1f).setDuration(duration).start()
                }
                State.ERROR -> {
                    textNotice.animate().alpha(1f).setDuration(duration).start()
                    pbDbCreating.animate().alpha(0f).setDuration(duration).start()
                    viewPager.animate().alpha(0f).setDuration(duration).start()
                    tabs.animate().alpha(0f).setDuration(duration).start()
                }
            }
        }
    }

    private inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CityListFragment()
            1 -> WeatherFragment()
            else -> ForecastFragment()
        }
    }

    companion object {
        private const val KEY_LANG = "key_language"
        private const val KEY_UNITS = "key_units"
    }
}

package com.emikhalets.sunnydayapp.ui.pager

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.ui.MainViewModel
import com.emikhalets.sunnydayapp.ui.citylist.CityListFragment
import com.emikhalets.sunnydayapp.ui.forecast.ForecastFragment
import com.emikhalets.sunnydayapp.ui.weather.WeatherFragment
import com.emikhalets.sunnydayapp.utils.*
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var pagerAdapter: ViewPagerAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("ViewPager перезагружен")
        initPreferences()
        initViewPager()
        initSearchView()
        with(mainViewModel) {
            database.observe(viewLifecycleOwner) { databaseObserver(it) }
            error.observe(viewLifecycleOwner) { errorObserver(it) }
            location.observe(viewLifecycleOwner) { locationObserver(it) }
            selectingCityCallback.observe(viewLifecycleOwner) { selectingCityObserver() }
            weather.observe(viewLifecycleOwner) { weatherObserver() }
            hourlyScrollCallback.observe(viewLifecycleOwner) { hourlyScrollObserver(it) }
        }
        binding.toolbar.findViewById<View>(R.id.menu_pager_preference)
            .setOnClickListener { onSettingsClick() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initPreferences() {
        Timber.d("Получение настроек приложения")
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        var prefLang = pref.getString(
            getString(R.string.key_pref_lang),
            getString(R.string.pref_lang_en_val)
        ) ?: getString(R.string.pref_lang_en_val)
        var prefUnits = pref.getString(
            getString(R.string.key_pref_units),
            getString(R.string.pref_unit_metric_val)
        ) ?: getString(R.string.pref_unit_metric_val)
        if (prefUnits == "1") prefLang = getString(R.string.pref_lang_en_val)
        if (prefUnits == "1") prefUnits = getString(R.string.pref_unit_metric_val)
        RequestConfig.lang = prefLang
        RequestConfig.units = prefUnits
        Timber.d("Получены настройки приложения: lang='$prefLang' units='$prefUnits'")
        if (mainViewModel.isPreferencesChanged) {
            Timber.d("Смена языка")
            setLocale(requireActivity(), prefLang)
            mainViewModel.sendWeatherRequest()
        }
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

    /**
     * Observes the successful addition of cities to the database.
     * @param state Interface visibility state
     */
    private fun databaseObserver(state: State) {
        Timber.d("База городов создана")
        if (state == State.LOADED) requireActivity()
            .getSharedPreferences(Tags.SP_FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(Tags.SP_DB_STATUS, true).apply()
        updateInterface(state)
    }

    /**
     * Inserting text error when adding cities to the database.
     * @param message Text error
     */
    private fun errorObserver(message: String) {
        Timber.e("Error while adding cities to the database: $message")
        binding.textNotice.text = message
    }

    /**
     * Sending a request when receiving a location.
     * @param location Received location
     */
    private fun locationObserver(location: Location) {
        if (!mainViewModel.isWeatherLoaded) {
            mainViewModel.currentCity = getCityFromLocation(requireContext(), location)
            binding.viewPager.setCurrentItem(1, true)
            mainViewModel.sendWeatherRequest(location.latitude, location.longitude)
        }
    }

    /**
     * Closes the SearchView and scrolls to the second page
     */
    private fun selectingCityObserver() {
        binding.viewPager.setCurrentItem(1, true)
        searchView.setQuery("", false)
        searchView.onActionViewCollapsed()
    }

    /**
     * Sets the city name in the toolbar and scrolls on the second page
     */
    private fun weatherObserver() {
        binding.toolbar.subtitle = mainViewModel.currentCity
        binding.viewPager.setCurrentItem(1, true)
    }

    private fun hourlyScrollObserver(isCanScroll: Boolean) {
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
}

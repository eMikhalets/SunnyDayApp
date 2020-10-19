package com.emikhalets.sunnydayapp.ui.pager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
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
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.FragmentPagerBinding
import com.emikhalets.sunnydayapp.utils.ToastBuilder
import com.emikhalets.sunnydayapp.utils.status.PagerStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {

    private val LOCATION_PERMISSIONS_REQUEST = 42

    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var searchAdapter: SimpleCursorAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val pagerViewModel: ViewPagerViewModel by activityViewModels()

    private val colCityName = "city_name"

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
        savedInstanceState ?: checkExistingCitiesTable()
        initObservers()
        initSearchView()
        initViewPager()
        initLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkExistingCitiesTable() {
        val dataStore = requireContext().createDataStore(getString(R.string.ds_file_name))
        val isDbCreatedKey = preferencesKey<Boolean>(getString(R.string.ds_is_db_created))
        val isDbCreatedFlow: Flow<Boolean> = dataStore.data.map { it[isDbCreatedKey] ?: false }
        lifecycleScope.launch {
            isDbCreatedFlow.collect { isDbCreated ->
                if (!isDbCreated) {
                    Timber.d("Cities table not exist")
                    setVisibility(PagerStatus.DB_CREATING)
                    convertCitiesToDB()
                    dataStore.edit { it[isDbCreatedKey] = true }
                } else {
                    Timber.d("Cities table in database was created")
                    setVisibility(PagerStatus.DB_CREATED)
                }
            }
        }
    }

    private fun convertCitiesToDB() {
        requireContext().assets.open("cities_20000.json").bufferedReader()
            .use { bufferReader ->
                val json = bufferReader.use { it.readText() }
                pagerViewModel.parseAndInsertToDB(json)
            }
    }

    private fun initObservers() {
        pagerViewModel.searchingCities.observe(viewLifecycleOwner, {
            val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "city_name"))
            for (i in it.indices) cursor.addRow(arrayOf(i, it[i]))
            searchAdapter.changeCursor(cursor)
            searchView.suggestionsAdapter = searchAdapter
        })

        pagerViewModel.currentQuery.observe(viewLifecycleOwner, {
            Timber.d("Query has been updated: ($it)")
            with(binding) {
                toolbar.subtitle = it
                viewPager.setCurrentItem(1, true)
            }
        })

        pagerViewModel.locationQuery.observe(viewLifecycleOwner, {
            Timber.d("Location query has been updated: ($it)")
            binding.toolbar.subtitle = it
        })

        pagerViewModel.dbStatus.observe(viewLifecycleOwner, {
            when (it) {
                PagerStatus.DB_CREATED -> {
                    Timber.d("Cities table in database has been created")
                    setVisibility(it)
                }
                PagerStatus.DB_DELETED -> {
                    Timber.d("Cities table has been deleted")
                    convertCitiesToDB()
                }
            }
        })

        binding.toolbar.findViewById<View>(R.id.menu_pager_preference).setOnClickListener {
            Timber.d("Settings Click")
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_viewPagerFragment_to_preferencePagerFragment)
        }

//        binding.toolbar.findViewById<View>(R.id.menu_pager_recreate_db).setOnClickListener {
//            Timber.d("Recreating database click")
//            setVisibilityMode(CREATING)
//            pagerViewModel.deleteCitiesTable()
//        }
    }

    private fun initViewPager() {
        pagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.run {
            adapter = pagerAdapter
            setCurrentItem(1, false)
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_title_city_list)
                1 -> tab.text = getString(R.string.tab_title_current)
            }
            tab.select()
        }.attach()
    }

    private fun setVisibility(status: PagerStatus) {
        when (status) {
            PagerStatus.DB_CREATING -> {
                Timber.d("Showing progressbar, notice. Hiding viewpager, tabs")
                with(binding) {
                    tabLayout.visibility = View.INVISIBLE
                    viewPager.visibility = View.INVISIBLE
                    textNotice.visibility = View.VISIBLE
                    pbDbCreating.visibility = View.VISIBLE
                }
            }
            PagerStatus.DB_CREATED -> {
                Timber.d("Hiding progressbar, notice. Showing viewpager, tabs")
                with(binding) {
                    viewPager.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                    textNotice.visibility = View.INVISIBLE
                    pbDbCreating.visibility = View.INVISIBLE
                }
            }
        }
    }

    // SearchView

    private fun initSearchView() {
        searchView = binding.toolbar.menu.findItem(R.id.menu_pager_search).actionView as SearchView
        searchAdapter = SimpleCursorAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            null,
            arrayOf("city_name"),
            intArrayOf(android.R.id.text1),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        searchView.setOnQueryTextListener(searchTextListener())
        searchView.setOnSuggestionListener(searchAdapterListener())
    }

    private fun searchTextListener() = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean = false
        override fun onQueryTextChange(newText: String): Boolean {
            searchAdapter.changeCursor(null)
            if (newText.length >= 2) {
                Timber.d("The search query has been changed: ($newText)")
                pagerViewModel.getCitiesByName(newText)
            }
            return true
        }
    }

    private fun searchAdapterListener() = object : SearchView.OnSuggestionListener {
        override fun onSuggestionSelect(id: Int): Boolean = false
        override fun onSuggestionClick(id: Int): Boolean {
            val cursor = searchView.suggestionsAdapter.getItem(id) as Cursor
            val name = cursor.getString(cursor.getColumnIndex(colCityName))
            binding.toolbar.subtitle = name
            cursor.close()
            searchAdapter.changeCursor(null)

            Timber.d("Select in search: $name")
            searchView.run {
                setQuery(name, false)
                onActionViewCollapsed()
                setQuery(null, false)
            }
            pagerViewModel.run {
                isWeatherLoaded = false
                pagerViewModel.updateCurrentQuery(name)
            }
            return true
        }
    }

    // Location

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (pagerViewModel.checkLocationPermissions()) {
            Timber.d("Location permissions not granted. Request permissions")
            requestPermissions(
                arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION),
                LOCATION_PERMISSIONS_REQUEST
            )
        } else {
            Timber.d("Location permissions was granted. Request location")
            pagerViewModel.requestLocation(fusedLocationClient)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Timber.d("Request Permissions Result")
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Timber.d("Location permissions have been granted")
                    pagerViewModel.requestLocation(fusedLocationClient)
                } else {
                    Timber.d("Location permissions have not been granted")
                    ToastBuilder.build(getString(R.string.city_list_text_location_permissions_not_granted))
                }
            }
        }
    }
}

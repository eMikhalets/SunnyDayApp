package com.emikhalets.sunnydayapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.emikhalets.sunnydayapp.BuildConfig
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.ActivityMainBinding
import com.emikhalets.sunnydayapp.utils.OnLocationSettingsClick
import com.emikhalets.sunnydayapp.utils.OnThemeListener
import com.emikhalets.sunnydayapp.utils.State
import com.emikhalets.sunnydayapp.utils.getCityFromLocation
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnLocationSettingsClick, OnThemeListener {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val locationSettingsResult = registerForActivityResult(StartActivityForResult()) {
        if (isLocationEnabled()) requestLocation()
    }
    private val locationPermissionResult = registerForActivityResult(RequestMultiplePermissions()) {
        var isSuccess = true
        it.forEach { p -> if (!p.value) isSuccess = false }
        if (isSuccess && isLocationEnabled()) requestLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPreferences()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        if (savedInstanceState == null) checkDatabase()
        initLocation()
        mainViewModel.database.observe(this) { databaseObserver(it) }
        mainViewModel.location.observe(this) { locationObserver(it) }
        mainViewModel.prefs.observe(this) { preferencesObserver(it) }
    }

    private fun initPreferences() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
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
        mainViewModel.prefs.value = mapOf(KEY_LANG to prefLang, KEY_UNITS to prefUnits)
        setLocale(prefLang)
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun checkDatabase() {
        val sp = getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        val isDbCreated = sp.getBoolean(SP_DB_STATUS, false)
        if (!isDbCreated) mainViewModel.parseCitiesJson(assets.open(CITIES_JSON))
        else mainViewModel.setDatabaseStateLoaded()
    }

    private fun databaseObserver(state: State) {
        if (state == State.LOADED) getSharedPreferences(SP_FILE, Context.MODE_PRIVATE).edit()
            .putBoolean(SP_DB_STATUS, true).apply()
    }

    private fun locationObserver(location: Location) {
        mainViewModel.currentCity = getCityFromLocation(this, location)
        mainViewModel.currentLat = location.latitude
        mainViewModel.currentLong = location.longitude
        mainViewModel.sendWeatherRequest(location.latitude, location.longitude)
    }

    private fun preferencesObserver(map: Map<String, String>) {
        setLocale(map[KEY_LANG] ?: "en")
    }

    // =================== Location

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (permissionFineLocation() && permissionCoarseLocation()) {
            if (isLocationEnabled()) requestLocation()
        } else {
            locationPermissionResult.launch(PERMISSION_ARRAY)
        }
    }

    private fun permissionCoarseLocation(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun permissionFineLocation(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        fusedLocationClient.lastLocation.apply {
            addOnSuccessListener {
                if (it != null) mainViewModel.location.postValue(it)
                else requestLocationUpdates()
            }
            addOnFailureListener { requestLocationUpdates() }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10000)
            .setFastestInterval(5000)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                mainViewModel.location.postValue(locationResult.locations.first())
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onLocationSettingsClick() {
        locationSettingsResult.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun onThemeChange(isNight: Boolean) {
        when (isNight) {
            true -> {
                if (!mainViewModel.isNightTheme) {
                    mainViewModel.isNightTheme = true
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    window.setWindowAnimations(R.style.ThemeChangingAnim)
                    recreate()
                }
            }
            false -> {
                if (mainViewModel.isNightTheme) {
                    mainViewModel.isNightTheme = false
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    window.setWindowAnimations(R.style.ThemeChangingAnim)
                    recreate()
                }
            }
        }
    }

    companion object {
        private val PERMISSION_ARRAY = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val CITIES_JSON = "city_list_min.json"
        private const val SP_FILE = "SunnyDayApp_shared_preferences"
        private const val SP_DB_STATUS = "sp_database_status"
        private const val KEY_LANG = "key_language"
        private const val KEY_UNITS = "key_units"
    }
}
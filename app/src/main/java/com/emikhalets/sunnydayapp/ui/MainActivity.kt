package com.emikhalets.sunnydayapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.emikhalets.sunnydayapp.BuildConfig
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.databinding.ActivityMainBinding
import com.emikhalets.sunnydayapp.utils.Conf
import com.emikhalets.sunnydayapp.utils.State
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPreferences()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        if (savedInstanceState == null) checkDatabase()
        initLocation()
        mainViewModel.database.observe(this) { databaseObserver(it) }
        mainViewModel.prefLang.observe(this) { prefLangObserver(it) }
        mainViewModel.prefUnits.observe(this) { prefUnitsObserver(it) }
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
        mainViewModel.prefLang.value = prefLang
        mainViewModel.prefUnits.value = prefUnits
        setLocale(mainViewModel.prefLang.value ?: getString(R.string.pref_lang_en_val))
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

    private fun prefLangObserver(lang: String) {
        setLocale(lang)
        if (mainViewModel.currentLat != 0.0 && mainViewModel.currentLong != 0.0) {
            mainViewModel.sendWeatherRequest(mainViewModel.currentLat, mainViewModel.currentLong)
        }
    }

    private fun prefUnitsObserver(units: String) {
        Conf.units = units
        if (mainViewModel.currentLat != 0.0 && mainViewModel.currentLong != 0.0) {
            mainViewModel.sendWeatherRequest(mainViewModel.currentLat, mainViewModel.currentLong)
        }
    }

    // =================== Location

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val requestLocationPermission = registerForActivityResult(RequestPermission()) {
            if (it) requestLocation()
        }
        if (isGpsEnabled() && permissionCoarseLocation()) {
            requestLocation()
        } else {
            if (permissionFineLocation()) {
                requestLocation()
            } else {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun isGpsEnabled(): Boolean {
        return (getSystemService(LOCATION_SERVICE) as LocationManager)
            .isProviderEnabled(LocationManager.GPS_PROVIDER)
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
    fun requestLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let { mainViewModel.location.postValue(it) }
        }
//        fusedLocationClient.requestLocationUpdates(LocationRequest(), LocationCallback, Looper.getMainLooper())
    }

    companion object {
        private const val CITIES_JSON = "city_list_min.json"
        private const val SP_FILE = "SunnyDayApp_shared_preferences"
        private const val SP_DB_STATUS = "sp_database_status"
    }
}
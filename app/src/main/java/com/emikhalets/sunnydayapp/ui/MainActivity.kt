package com.emikhalets.sunnydayapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.emikhalets.sunnydayapp.utils.*
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnLocationSettingsClick, OnThemeListener {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val locationSettingsResult = registerForActivityResult(StartActivityForResult()) {
        if (isLocationEnabled()) requestLastLocation()
    }
    private val locationPermissionResult = registerForActivityResult(RequestMultiplePermissions()) {
        var isSuccess = true
        it.forEach { p -> if (!p.value) isSuccess = false }
        if (isSuccess && isLocationEnabled()) requestLastLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppLocale()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        if (savedInstanceState == null) checkDatabase()
        initLocation()
    }

    private fun setAppLocale() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var prefLang = pref.getString(
            getString(R.string.key_pref_lang),
            getString(R.string.pref_lang_en_val)
        ) ?: getString(R.string.pref_lang_en_val)
        if (prefLang == "1") prefLang = getString(R.string.pref_lang_en_val)
        setLocale(this, prefLang)
        Timber.d("Установка сохраненнего языка: lang='$prefLang'")
    }

    private fun checkDatabase() {
        val sp = getSharedPreferences(Tags.SP_FILE_NAME, Context.MODE_PRIVATE)
        val isDbCreated = sp.getBoolean(Tags.SP_DB_STATUS, false)
        if (!isDbCreated) mainViewModel.parseCitiesJson(assets.open(CITIES_JSON))
        else mainViewModel.setDatabaseStateLoaded()
    }

    // =================== Location section

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (isLocationPermissionEnabled()) {
            if (isLocationEnabled()) requestLastLocation()
        } else {
            locationPermissionResult.launch(PERMISSION_ARRAY)
        }
    }

    private fun isLocationPermissionEnabled(): Boolean {
        val isCoarseLocation = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val isFineLocation = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return isCoarseLocation && isFineLocation
    }

    @SuppressLint("MissingPermission")
    private fun requestLastLocation() {
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

    // =================== End Location section

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
    }
}
package com.emikhalets.sunnydayapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emikhalets.sunnydayapp.BuildConfig
import com.emikhalets.sunnydayapp.data.AppDatabase
import com.emikhalets.sunnydayapp.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppDatabase.implement(this)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
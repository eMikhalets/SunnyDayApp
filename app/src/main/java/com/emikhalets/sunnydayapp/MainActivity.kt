package com.emikhalets.sunnydayapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.BuildConfig
import com.emikhalets.sunnydayapp.databinding.ActivityMainBinding
import timber.log.Timber

public class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
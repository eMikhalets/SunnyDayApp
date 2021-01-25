package com.emikhalets.sunnydayapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emikhalets.sunnydayapp.BuildConfig
import com.emikhalets.sunnydayapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

//    override fun onBackPressed() {
//        val navController = Navigation.findNavController(binding.root)
//
//        when (navController.currentDestination?.id) {
//            R.id.preferencePagerFragment -> {
//                navController.popBackStack()
//            }
//            else -> {
//                super.onBackPressed()
//            }
//        }
//    }
}
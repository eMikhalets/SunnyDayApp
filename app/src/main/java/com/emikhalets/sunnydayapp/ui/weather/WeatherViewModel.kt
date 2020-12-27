package com.emikhalets.sunnydayapp.ui.weather

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.repository.WeatherRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class WeatherViewModel @ViewModelInject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable -> Timber.e(throwable) }
    private val coroutineContext = Dispatchers.IO + SupervisorJob()
}
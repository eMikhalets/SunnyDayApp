package com.emikhalets.sunnydayapp.ui.preference

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.model.ResponseUsage
import com.emikhalets.sunnydayapp.data.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class PreferenceViewModel @ViewModelInject constructor(private val repository: PreferenceRepository) :
    ViewModel() {

    private val _apiStatistics = MutableLiveData<ResponseUsage>()
    val apiStatistics: LiveData<ResponseUsage> get() = _apiStatistics

    private var _notice = MutableLiveData<String>()
    val notice: LiveData<String> get() = _notice

    var currentLang: String = Locale.getDefault().language

    fun getApiStatistics() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repository.requestApiUsage()
                _apiStatistics.postValue(data)
            } catch (ex: Exception) {
                Timber.e(ex)
                _notice.postValue(ex.message)
            }
        }
    }
}
package com.emikhalets.sunnydayapp.ui.preference

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.api.AppResponse
import com.emikhalets.sunnydayapp.data.model.ResponseUsage
import com.emikhalets.sunnydayapp.data.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            when (val data = repository.requestApiUsage()) {
                is AppResponse.Success ->
                    _apiStatistics.postValue(data.response)
                is AppResponse.Error ->
                    _notice.postValue("Code: ${data.code}, Data: ${data.error?.error}")
                is AppResponse.NetworkError ->
                    _notice.postValue(data.toString())
            }
        }
    }
}
package com.emikhalets.sunnydayapp.ui.preference

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.api.AppResponse
import com.emikhalets.sunnydayapp.data.pojo.ResponseUsage
import com.emikhalets.sunnydayapp.data.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreferenceViewModel @ViewModelInject constructor(private val repository: PreferenceRepository) :
    ViewModel() {

    private var _apiStatistics = MutableLiveData<ResponseUsage>()
    val apiStatistics get() = _apiStatistics

    private var _notice = MutableLiveData<String>()
    val notice get() = _notice

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
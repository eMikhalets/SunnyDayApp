package com.emikhalets.sunnydayapp.ui.preference

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.PreferenceRepository
import com.emikhalets.sunnydayapp.network.AppResponse
import com.emikhalets.sunnydayapp.network.pojo.ResponseUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreferenceViewModel : ViewModel() {

    private val repository = PreferenceRepository()

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
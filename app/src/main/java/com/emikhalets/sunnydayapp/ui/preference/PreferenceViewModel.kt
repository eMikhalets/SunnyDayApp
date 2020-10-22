package com.emikhalets.sunnydayapp.ui.preference

import androidx.hilt.lifecycle.ViewModelInject
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

    val apiStatistics = MutableLiveData<ResponseUsage>()

    // TODO: create pref status like a weather and viewpager. remove notice livedata
    private var _notice = MutableLiveData<String>()
    val notice get() = _notice

    var currentLang: String = Locale.getDefault().language

    fun getApiStatistics() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val data = repository.requestApiUsage()) {
                is AppResponse.Success ->
                    apiStatistics.postValue(data.response)
                is AppResponse.Error ->
                    _notice.postValue("Code: ${data.code}, Data: ${data.error?.error}")
                is AppResponse.NetworkError ->
                    _notice.postValue(data.toString())
            }
        }
    }
}
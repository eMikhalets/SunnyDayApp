package com.emikhalets.sunnydayapp.ui.preference

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emikhalets.sunnydayapp.data.repository.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class PreferenceViewModel @ViewModelInject constructor(private val repository: PreferenceRepository) :
    ViewModel() {

    private var _notice = MutableLiveData<String>()
    val notice: LiveData<String> get() = _notice

    var currentLang: String = Locale.getDefault().language
}
package com.emikhalets.sunnydayapp.ui.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

class PreferenceViewModel : ViewModel() {

    private var _notice = MutableLiveData<String>()
    val notice: LiveData<String> get() = _notice

    var currentLang: String = Locale.getDefault().language
}
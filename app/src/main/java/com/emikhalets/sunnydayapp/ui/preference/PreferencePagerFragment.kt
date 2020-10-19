package com.emikhalets.sunnydayapp.ui.preference

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.utils.ToastBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class PreferencePagerFragment : PreferenceFragmentCompat() {

    private lateinit var language: ListPreference
    private lateinit var theme: SwitchPreference
    private lateinit var temperature: ListPreference
    private lateinit var pressure: ListPreference
    private lateinit var speed: ListPreference
    private lateinit var calls: EditTextPreference
    private lateinit var callsRem: EditTextPreference
    private lateinit var callsTs: EditTextPreference
    private lateinit var historical: EditTextPreference
    private lateinit var historicalRem: EditTextPreference
    private lateinit var historicalTs: EditTextPreference

    private val prefViewModel: PreferenceViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_pager, rootKey)

        language = findPreference(getString(R.string.pref_key_lang))!!
        theme = findPreference(getString(R.string.pref_key_theme))!!
        temperature = findPreference(getString(R.string.pref_key_temp))!!
        pressure = findPreference(getString(R.string.pref_key_press))!!
        speed = findPreference(getString(R.string.pref_key_speed))!!
        calls = findPreference(getString(R.string.pref_key_calls))!!
        callsRem = findPreference(getString(R.string.pref_key_calls_rem))!!
        callsTs = findPreference(getString(R.string.pref_key_calls_ts))!!
        historical = findPreference(getString(R.string.pref_key_hist))!!
        historicalRem = findPreference(getString(R.string.pref_key_hist_rem))!!
        historicalTs = findPreference(getString(R.string.pref_key_hist_ts))!!

//        prefLang?.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
//            when (preference.value) {
//                "en" -> "English"
//                "ru" -> "Русский"
//                else -> "English"
//            }
//        }
//
//        prefUnits?.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
//            when (preference.value) {
//                "M" -> "Metric"
//                "S" -> "Scientific"
//                "F" -> "Fahrenheit"
//                else -> "Metric"
//            }
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        implementObservers()
        prefViewModel.getApiStatistics()
    }

    private fun implementObservers() {
        prefViewModel.apiStatistics.observe(viewLifecycleOwner, {
            calls.summary = it.callsCount.toString()
            callsRem.summary = it.callsRemaining.toString()
            callsTs.summary = formatDateTime(it.callsResetTs)
            historical.summary = it.callsCount.toString()
            historicalRem.summary = it.historicalCallsRemaining.toString()
            historicalTs.summary = formatDateTime(it.historicalCallsResetTs)
        })

        prefViewModel.notice.observe(viewLifecycleOwner, {
            ToastBuilder.build(it)
        })
    }

    private fun formatDateTime(ts: Long): String {
        val date = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(ts * 1000),
            ZoneId.systemDefault()
        )
        val formatter = DateTimeFormatter.ofPattern("d L y H:m")
        return date.format(formatter)
    }
}
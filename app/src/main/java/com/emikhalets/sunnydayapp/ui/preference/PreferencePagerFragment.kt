package com.emikhalets.sunnydayapp.ui.preference

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.preference.*
import com.emikhalets.sunnydayapp.R
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

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
        addPreferencesFromResource(R.xml.pref_pager)
        initViews()

        language.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "ru" -> {
                    setLocale("ru")
                    prefViewModel.currentLang = "ru"
                    getString(R.string.pref_pager_lang_ru)
                }
                else -> {
                    setLocale("en")
                    prefViewModel.currentLang = "en"
                    getString(R.string.pref_pager_lang_en)
                }
            }
        }

        temperature.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "F" -> getString(R.string.pref_pager_unit_temp_f)
                "K" -> getString(R.string.pref_pager_unit_temp_k)
                else -> getString(R.string.pref_pager_unit_temp_c)
            }
        }

        pressure.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "atm" -> getString(R.string.pref_pager_unit_press_atm)
                "pa" -> getString(R.string.pref_pager_unit_press_pa)
                else -> getString(R.string.pref_pager_unit_press_mb)
            }
        }

        speed.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "ms" -> getString(R.string.pref_pager_unit_speed_ms)
                else -> getString(R.string.pref_pager_unit_speed_kmh)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        prefViewModel.getApiStatistics()
    }

    private fun initViews() {
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
    }

    private fun initObservers() {
        prefViewModel.apiStatistics.observe(viewLifecycleOwner, {
            calls.summary = it.callsCount.toString()
            callsRem.summary = it.callsRemaining.toString()
            callsTs.summary = formatDateTime(it.callsResetTs)
            historical.summary = it.callsCount.toString()
            historicalRem.summary = it.historicalCallsRemaining.toString()
            historicalTs.summary = formatDateTime(it.historicalCallsResetTs)
        })

        prefViewModel.notice.observe(viewLifecycleOwner, {
            showToast(it)
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

    private fun setLocale(localeCode: String) {
        val res = resources
        val dm = res.displayMetrics
        val config = res.configuration
        config.setLocale(Locale(localeCode.toLowerCase(Locale.getDefault())))
        // TODO(): Deprecated
        res.updateConfiguration(config, dm)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
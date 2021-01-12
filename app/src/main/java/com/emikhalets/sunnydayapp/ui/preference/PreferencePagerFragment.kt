package com.emikhalets.sunnydayapp.ui.preference

import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.emikhalets.sunnydayapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencePagerFragment : PreferenceFragmentCompat() {

    private lateinit var language: ListPreference
    private lateinit var theme: SwitchPreference
    private lateinit var units: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_pager, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniPreferences()

        language.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "en" -> PREF_LANG_ENG
                "ru" -> PREF_LANG_RU
                else -> PREF_LANG_ENG
            }
        }

        units.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "metric" -> PREF_UNIT_METRIC
                "imperial" -> PREF_UNIT_IMPERIAL
                else -> PREF_UNIT_METRIC
            }
        }
    }

    private fun iniPreferences() {
        language = findPreference(KEY_PREF_LANG)!!
        theme = findPreference(KEY_PREF_THEME)!!
        units = findPreference(KEY_PREF_UNITS)!!

    }

    companion object {
        const val KEY_PREF_LANG = "key_pref_lang"
        const val KEY_PREF_THEME = "key_pref_theme"
        const val KEY_PREF_UNITS = "key_pref_units"

        private const val PREF_LANG_ENG = "English"
        private const val PREF_LANG_RU = "Русский"
        private const val PREF_UNIT_METRIC = "Metric"
        private const val PREF_UNIT_IMPERIAL = "Imperial"
    }
}
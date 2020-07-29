package com.emikhalets.sunnydayapp.ui

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.emikhalets.sunnydayapp.R

class PreferencePagerFragment : PreferenceFragmentCompat() {

    private val prefLang = findPreference<ListPreference>("pref_lang")
    private val prefUnits = findPreference<ListPreference>("pref_units")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_pager, rootKey)

        prefLang?.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "en" -> "English"
                "ru" -> "Русский"
                else -> "English"
            }
        }

        prefUnits?.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                "M" -> "Metric"
                "S" -> "Scientific"
                "F" -> "Fahrenheit"
                else -> "Metric"
            }
        }
    }
}
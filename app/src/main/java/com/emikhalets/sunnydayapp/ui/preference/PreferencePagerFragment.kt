package com.emikhalets.sunnydayapp.ui.preference

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.emikhalets.sunnydayapp.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PreferencePagerFragment : PreferenceFragmentCompat() {

    private lateinit var language: ListPreference
    private lateinit var theme: SwitchPreference
    private lateinit var units: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_pager, rootKey)
    }

    @SuppressLint("RestrictedApi")
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

        Navigation.findNavController(view).backStack.forEach {
            Timber.d(it.destination.navigatorName)
        }
    }

    private fun iniPreferences() {
        language = findPreference(getString(R.string.key_pref_lang))!!
        theme = findPreference(getString(R.string.key_pref_theme))!!
        units = findPreference(getString(R.string.key_pref_units))!!

    }

    companion object {
        private const val PREF_LANG_ENG = "English"
        private const val PREF_LANG_RU = "Русский"
        private const val PREF_UNIT_METRIC = "Metric"
        private const val PREF_UNIT_IMPERIAL = "Imperial"
    }
}
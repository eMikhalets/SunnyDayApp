package com.emikhalets.sunnydayapp.ui.preference

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PreferencePagerFragment : PreferenceFragmentCompat() {

    private lateinit var language: ListPreference
    private lateinit var units: ListPreference
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_pager, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniPreferences()

        language.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            setLocale(preference.value)
            when (preference.value) {
                getString(R.string.pref_lang_ru_val) -> getString(R.string.pref_lang_ru)
                else -> getString(R.string.pref_lang_en)
            }
        }

        units.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            when (preference.value) {
                getString(R.string.pref_unit_imperial_val) -> getString(R.string.pref_unit_imperial)
                else -> getString(R.string.pref_unit_metric)
            }
        }

        language.setOnPreferenceChangeListener { _, _ ->
            mainViewModel.isPreferencesChanged = true
            true
        }

        units.setOnPreferenceChangeListener { _, _ ->
            mainViewModel.isPreferencesChanged = true
            true
        }
    }

    private fun iniPreferences() {
        language = findPreference(getString(R.string.key_pref_lang))!!
        units = findPreference(getString(R.string.key_pref_units))!!
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
package com.emikhalets.sunnydayapp.ui.preference

import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.emikhalets.sunnydayapp.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class PreferencePagerFragment : PreferenceFragmentCompat() {

    private lateinit var language: ListPreference
    private lateinit var units: ListPreference

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
    }

    private fun iniPreferences() {
        language = findPreference(getString(R.string.key_pref_lang))!!
        units = findPreference(getString(R.string.key_pref_units))!!
    }

    //TODO: deprecated method 'Resources.updateConfiguration(Configuration, DisplayMetrics)'
    private fun setLocale(lang: String) {
        Timber.d("PREFERENCES SET LOCALE lang='$lang'")
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources = requireActivity().resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
package com.emikhalets.sunnydayapp.ui.preference

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.ui.preference.PreferenceViewModel

class PreferencePagerFragment : PreferenceFragmentCompat() {

    private val prefLang = findPreference<ListPreference>("pref_lang")
    private val prefUnits = findPreference<ListPreference>("pref_units")

    private val prefViewModel: PreferenceViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_pager, rootKey)
        implementObservers()

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

        //prefViewModel.getApiStatistics()
    }

    private fun implementObservers() {
        prefViewModel.apiStatistics.observe(viewLifecycleOwner, {
            // insert data
        })

        prefViewModel.notice.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }
}
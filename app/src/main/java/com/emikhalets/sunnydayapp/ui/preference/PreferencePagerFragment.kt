package com.emikhalets.sunnydayapp.ui.preference

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.emikhalets.sunnydayapp.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PreferencePagerFragment : PreferenceFragmentCompat() {

    private lateinit var language: ListPreference
    private lateinit var theme: SwitchPreference
    private lateinit var temperature: ListPreference
    private lateinit var pressure: ListPreference
    private lateinit var speed: ListPreference

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
    }

    private fun initViews() {
        language = findPreference(getString(R.string.pref_key_lang))!!
        theme = findPreference(getString(R.string.pref_key_theme))!!
        temperature = findPreference(getString(R.string.pref_key_temp))!!
        pressure = findPreference(getString(R.string.pref_key_press))!!
        speed = findPreference(getString(R.string.pref_key_speed))!!
    }

    private fun initObservers() {
        prefViewModel.notice.observe(viewLifecycleOwner, { showToast(it) })
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
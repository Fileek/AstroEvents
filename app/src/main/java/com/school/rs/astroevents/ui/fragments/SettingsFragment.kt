package com.school.rs.astroevents.ui.fragments

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.school.rs.astroevents.R
import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.ext.toOpticalInstrument
import com.school.rs.astroevents.ui.activity.SettingsListener
import com.school.rs.astroevents.ui.items.OpticalInstrument
import com.school.rs.astroevents.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var repository: AstroRepository

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val optInMenu =
            findPreference<DropDownPreference>(getString(R.string.optical_instruments_menu_key))
        val themeMenu =
            findPreference<DropDownPreference>(getString(R.string.app_theme_key))
        val infoPref =
            findPreference<Preference>(getString(R.string.info_preference_key))

        val listener = activity as SettingsListener

        optInMenu?.apply {
            setOpticalInstrumentIcon(this, optInMenu.value.toOpticalInstrument(resources))

            setOnPreferenceChangeListener { _, newValue ->
                val optic = (newValue as CharSequence).toOpticalInstrument(resources)

                setOpticalInstrumentIcon(optInMenu, optic)
                viewModel.updateOptic(optic)
                true
            }
        }

        themeMenu?.setOnPreferenceChangeListener { _, newValue ->
            listener.updateAppTheme(newValue as String?)
            true
        }

        infoPref?.setOnPreferenceClickListener {
            listener.openInfoFragment()
            true
        }
    }

    private fun setOpticalInstrumentIcon(menu: DropDownPreference, optic: OpticalInstrument) =
        menu.setIcon(
            when (optic) {
                OpticalInstrument.NAKED_EYE -> R.drawable.naked_eye
                OpticalInstrument.BINOCULARS -> R.drawable.binoculars
                OpticalInstrument.TELESCOPE -> R.drawable.telescope
            }
        )
}

package com.school.rs.astroevents.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.school.rs.astroevents.domain.OpticUseCase
import com.school.rs.astroevents.ui.items.OpticalInstrument
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updateOpticUseCase: OpticUseCase
) : ViewModel() {

    fun updateOptic(optic: OpticalInstrument) {
        updateOpticUseCase.performAction(optic)
    }
}

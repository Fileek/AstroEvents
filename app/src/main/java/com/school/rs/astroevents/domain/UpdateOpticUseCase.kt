package com.school.rs.astroevents.domain

import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.ui.items.OpticalInstrument
import javax.inject.Inject

interface OpticUseCase {

    fun performAction(optic: OpticalInstrument)
}

class UpdateOpticUseCase @Inject constructor(
    private val repo: AstroRepository
) : OpticUseCase {

    override fun performAction(optic: OpticalInstrument) {
        repo.updateOptic(optic)
    }
}

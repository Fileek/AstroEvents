package com.school.rs.astroevents.data.repositories

import com.school.rs.astroevents.data.entities.AstroEvent
import com.school.rs.astroevents.ui.items.Filters
import com.school.rs.astroevents.ui.items.OpticalInstrument
import kotlinx.coroutines.flow.SharedFlow

interface AstroRepository {

    val astroEvents: List<AstroEvent>

    val astroEventsFlow: SharedFlow<List<AstroEvent>>

    val stickToTodayDate: Boolean

    fun addFilter(filter: Filters)

    fun removeFilter(filter: Filters)

    fun clearFilters()

    fun updateOptic(optic: OpticalInstrument)
}

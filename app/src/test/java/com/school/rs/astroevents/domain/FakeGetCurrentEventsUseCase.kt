package com.school.rs.astroevents.domain

import com.school.rs.astroevents.ui.items.Event
import com.school.rs.astroevents.ui.items.OpticalInstrument
import java.util.Date

class FakeGetCurrentEventsUseCase : ListUseCase<Event> {

    override fun performAction(): List<Event> {
        val eventX = Event(
            "x",
            Date(100L),
            OpticalInstrument.BINOCULARS,
            "",
            "",
            "",
            ""
        )
        val eventY = Event(
            "y",
            Date(200L),
            OpticalInstrument.TELESCOPE,
            "",
            "",
            "",
            ""
        )
        return listOf(eventX, eventY)
    }
}

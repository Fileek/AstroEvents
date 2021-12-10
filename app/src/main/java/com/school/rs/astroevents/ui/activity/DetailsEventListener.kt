package com.school.rs.astroevents.ui.activity

import com.school.rs.astroevents.ui.items.Event

interface DetailsEventListener : EventListener {

    fun setEventToAddToCalendarListener(event: Event)
}

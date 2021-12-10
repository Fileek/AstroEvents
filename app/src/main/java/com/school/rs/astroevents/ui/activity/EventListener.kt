package com.school.rs.astroevents.ui.activity

import com.school.rs.astroevents.ui.items.Event

interface EventListener {

    fun addEventToCalendar(event: Event)
}

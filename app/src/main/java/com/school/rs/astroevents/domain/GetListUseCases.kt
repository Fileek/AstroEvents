package com.school.rs.astroevents.domain

import android.content.res.Resources
import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.ext.format
import com.school.rs.astroevents.ui.items.Event
import javax.inject.Inject

interface ListUseCase<T> {

    fun performAction(): List<T>
}

class GetCurrentEventsUseCase @Inject constructor(
    private val repo: AstroRepository,
    private val resources: Resources
) : ListUseCase<Event> {

    override fun performAction(): List<Event> = repo.astroEvents.format(resources)
}

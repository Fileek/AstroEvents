package com.school.rs.astroevents.domain

import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.ui.items.Filters
import javax.inject.Inject

class AddFilterUseCase @Inject constructor(private val repo: AstroRepository) {

    fun performAction(filter: Filters) {
        repo.addFilter(filter)
    }
}

class RemoveFilterUseCase @Inject constructor(private val repo: AstroRepository) {

    fun performAction(filter: Filters) {
        repo.removeFilter(filter)
    }
}

class ClearFiltersUseCase @Inject constructor(private val repo: AstroRepository) {

    fun performAction() {
        repo.clearFilters()
    }
}

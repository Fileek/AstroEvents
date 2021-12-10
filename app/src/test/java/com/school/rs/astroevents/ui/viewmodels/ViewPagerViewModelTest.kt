package com.school.rs.astroevents.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.school.rs.astroevents.domain.FakeGetCurrentEventsUseCase
import com.school.rs.astroevents.domain.ListUseCase
import com.school.rs.astroevents.ui.items.Event
import org.junit.Before
import org.junit.Test

class ViewPagerViewModelTest {

    private lateinit var viewModel: ViewPagerViewModel

    private lateinit var useCase: ListUseCase<Event>

    @Before
    fun setup() {
        useCase = FakeGetCurrentEventsUseCase()
        viewModel = ViewPagerViewModel(useCase)
    }

    @Test
    fun `events, returns list from ListUseCase performAction method`() {
        assertThat(viewModel.events).isEqualTo(useCase.performAction())
    }
}

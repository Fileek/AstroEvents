package com.school.rs.astroevents.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.school.rs.astroevents.MainCoroutineRule
import com.school.rs.astroevents.domain.FakeStateFlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class DetailsViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: DetailsViewModel

    private lateinit var useCase: FakeStateFlowUseCase

    @Test
    fun `fetch description, calls StateFlowUseCase performAction method`() {
        val url = "https://www.google.com/"
        useCase = mock(FakeStateFlowUseCase::class.java)
        viewModel = DetailsViewModel(useCase)
        viewModel.fetchDescription(url)

        verify(useCase).performAction(url)
    }

    @Test
    fun `description flow value, returns StateFlowUseCase resultFlow value`() {
        useCase = FakeStateFlowUseCase()
        viewModel = DetailsViewModel(useCase)

        assertThat(viewModel.descriptionFlow.value).isEqualTo(useCase.resultFlow.value)
    }
}

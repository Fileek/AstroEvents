package com.school.rs.astroevents.ui.viewmodels

import com.school.rs.astroevents.MainCoroutineRule
import com.school.rs.astroevents.domain.OpticUseCase
import com.school.rs.astroevents.ui.items.OpticalInstrument
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SettingsViewModel

    private lateinit var fakeUseCase: OpticUseCase

    @Before
    fun setup() {
        fakeUseCase = mock(OpticUseCase::class.java)
        viewModel = SettingsViewModel(fakeUseCase)
    }

    @Test
    fun `update optic, calls OpticUseCase performAction method`() {
        val optic = OpticalInstrument.BINOCULARS
        viewModel.updateOptic(optic)
        verify(fakeUseCase, times(1)).performAction(optic)
    }
}

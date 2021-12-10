package com.school.rs.astroevents.ui.viewmodels

import com.google.common.truth.Truth.assertThat
import com.school.rs.astroevents.MainCoroutineRule
import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.domain.AddFilterUseCase
import com.school.rs.astroevents.domain.ClearFiltersUseCase
import com.school.rs.astroevents.domain.FakeSharedFlowUseCase
import com.school.rs.astroevents.domain.RemoveFilterUseCase
import com.school.rs.astroevents.ui.items.Filters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    private lateinit var fakeRepo: AstroRepository

    private val firstFilter = Filters.METEOR_SHOWERS

    private val secondFilter = Filters.ECLIPSES

    @Before
    fun setup() {
        fakeRepo = mock(AstroRepository::class.java)
        viewModel = MainViewModel(
            FakeSharedFlowUseCase(),
            FakeSharedFlowUseCase(),
            AddFilterUseCase(fakeRepo),
            RemoveFilterUseCase(fakeRepo),
            ClearFiltersUseCase(fakeRepo)
        )
    }

    @Test
    fun `add filter, removes 'All' filter from checkedFilters`() {
        viewModel.addFilter(firstFilter)

        assertThat(viewModel.checkedFiltersFlow.value).doesNotContain(Filters.ALL)

        viewModel.addFilter(secondFilter)

        assertThat(viewModel.checkedFiltersFlow.value).doesNotContain(Filters.ALL)
    }

    @Test
    fun `add filter, adds it to checkedFilters`() {
        viewModel.addFilter(firstFilter)

        assertThat(viewModel.checkedFiltersFlow.value).contains(firstFilter)
    }

    @Test
    fun `add filter, calls AstroRepository addFilter method`() {
        viewModel.addFilter(firstFilter)

        verify(fakeRepo, times(1)).addFilter(firstFilter)
    }

    @Test
    fun `remove filter, if it's last, replaces it with 'All' filter in checkedFilters`() {
        viewModel.run {
            clearFilters()
            addFilter(firstFilter)
            removeFilter(firstFilter)
        }

        assertThat(viewModel.checkedFiltersFlow.value).containsExactly(Filters.ALL)
    }

    @Test
    fun `remove filter, removes it from checkedFilters`() {
        viewModel.run {
            addFilter(firstFilter)
            addFilter(secondFilter)
            removeFilter(firstFilter)
        }

        assertThat(viewModel.checkedFiltersFlow.value).doesNotContain(firstFilter)
    }

    @Test
    fun `remove filter, calls AstroRepository removeFilter method`() {
        viewModel.removeFilter(firstFilter)

        verify(fakeRepo, times(1)).removeFilter(firstFilter)
    }

    @Test
    fun `clear filters, replaces all filters with 'All' filter`() {
        viewModel.run {
            addFilter(firstFilter)
            addFilter(secondFilter)
            clearFilters()
        }

        assertThat(viewModel.checkedFiltersFlow.value).containsExactly(Filters.ALL)
    }

    @Test
    fun `clear filters, calls AstroRepository clearFilters method`() {
        viewModel.clearFilters()

        verify(fakeRepo, times(1)).clearFilters()
    }
}

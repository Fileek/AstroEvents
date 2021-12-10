package com.school.rs.astroevents.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.rs.astroevents.domain.AddFilterUseCase
import com.school.rs.astroevents.domain.ClearFiltersUseCase
import com.school.rs.astroevents.domain.RemoveFilterUseCase
import com.school.rs.astroevents.domain.SharedFlowUseCase
import com.school.rs.astroevents.ui.items.Filters
import com.school.rs.astroevents.ui.items.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getItemsUseCase: SharedFlowUseCase<List<Item>>,
    private val getTodayIndexUseCase: SharedFlowUseCase<Int>,
    private val addFilterUseCase: AddFilterUseCase,
    private val removeFilterUseCase: RemoveFilterUseCase,
    private val clearFiltersUseCase: ClearFiltersUseCase,
) : ViewModel() {

    val itemsFlow: SharedFlow<List<Item>> = getItemsUseCase.resultFlow

    val todayPositionFlow: SharedFlow<Int> = getTodayIndexUseCase.resultFlow

    private val _checkedFiltersFlow = MutableStateFlow(setOf(Filters.ALL))
    val checkedFiltersFlow: StateFlow<Set<Filters>> get() = _checkedFiltersFlow.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getItemsUseCase.performAction()
            }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getTodayIndexUseCase.performAction()
            }
        }
    }

    fun addFilter(filter: Filters) {
        val filters = checkedFiltersFlow.value.toMutableSet()
        filters.apply {
            remove(Filters.ALL)
            add(filter)
        }
        _checkedFiltersFlow.value = filters.toSet()
        addFilterUseCase.performAction(filter)
    }

    fun removeFilter(filter: Filters) {
        val filters = checkedFiltersFlow.value.toMutableSet()
        filters.apply {
            if (size == 1) _checkedFiltersFlow.value = setOf(Filters.ALL)
            else {
                remove(filter)
                _checkedFiltersFlow.value = this.toSet()
            }
        }
        removeFilterUseCase.performAction(filter)
    }

    fun clearFilters() {
        _checkedFiltersFlow.value = setOf(Filters.ALL)
        clearFiltersUseCase.performAction()
    }
}

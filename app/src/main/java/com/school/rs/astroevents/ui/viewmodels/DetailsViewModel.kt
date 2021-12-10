package com.school.rs.astroevents.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.school.rs.astroevents.domain.StateFlowUseCase
import com.school.rs.astroevents.ext.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.select.Elements
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getDescriptionElementsUseCase: StateFlowUseCase<Resource<Elements>, String>
) : ViewModel() {

    val descriptionFlow: StateFlow<Resource<Elements>> = getDescriptionElementsUseCase.resultFlow

    fun fetchDescription(url: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getDescriptionElementsUseCase.performAction(url)
            }
        }
    }
}

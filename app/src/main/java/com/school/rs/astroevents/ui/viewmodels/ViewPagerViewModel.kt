package com.school.rs.astroevents.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.school.rs.astroevents.domain.ListUseCase
import com.school.rs.astroevents.ui.items.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewPagerViewModel @Inject constructor(
    getCurrentEventsUseCase: ListUseCase<Event>
) : ViewModel() {

    val events = getCurrentEventsUseCase.performAction()
}

package com.school.rs.astroevents.domain

import android.content.res.Resources
import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.ext.format
import com.school.rs.astroevents.ui.items.Item
import com.school.rs.astroevents.ui.items.TodayDivider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.Date
import javax.inject.Inject

abstract class SharedFlowUseCase<T> {

    val resultFlow: SharedFlow<T> get() = mutableResultFlow.asSharedFlow()
    protected abstract val mutableResultFlow: MutableSharedFlow<T>

    protected val date = Date(System.currentTimeMillis())

    abstract suspend fun performAction()
}

class GetItemsUseCase @Inject constructor(
    private val repository: AstroRepository,
    private val resources: Resources
) : SharedFlowUseCase<List<Item>>() {

    override val mutableResultFlow = MutableSharedFlow<List<Item>>(1)

    override suspend fun performAction() {
        repository.astroEventsFlow.collectLatest { list ->
            if (list.isEmpty()) mutableResultFlow.emit(emptyList())
            else {
                val events = list.format(resources)
                val items: MutableList<Item> = events.toMutableList()
                items.add(events.indexOf(events.findLast { date > it.date }) + 1, TodayDivider)
                mutableResultFlow.emit(items.toList())
            }
        }
    }
}

class GetTodayIndexUseCase @Inject constructor(
    private val repository: AstroRepository,
    private val resources: Resources
) : SharedFlowUseCase<Int>() {

    override val mutableResultFlow = MutableSharedFlow<Int>(0)

    private var index = 0

    override suspend fun performAction() {
        repository.astroEventsFlow.collectLatest { list ->
            if (repository.stickToTodayDate) {
                val events = list.format(resources)
                val newIndex = events.indexOf(events.findLast { date > it.date }) + 1
                if (index != newIndex) {
                    index = newIndex
                    mutableResultFlow.emit(index)
                }
            } else {
                mutableResultFlow.emit(-1)
            }
        }
    }
}

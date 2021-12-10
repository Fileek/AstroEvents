package com.school.rs.astroevents.domain

import com.school.rs.astroevents.ext.Resource
import com.school.rs.astroevents.ext.toDescriptionElements
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.util.logging.Logger

abstract class StateFlowUseCase<T, P> {

    val resultFlow: StateFlow<T> get() = mutableResultFlow.asStateFlow()
    protected abstract val mutableResultFlow: MutableStateFlow<T>

    abstract fun performAction(param: P)
}

class GetDescriptionElementsUseCase : StateFlowUseCase<Resource<Elements>, String>() {

    override val mutableResultFlow = MutableStateFlow<Resource<Elements>>(Resource.notInitialized(null))

    override fun performAction(param: String) {
        mutableResultFlow.value = Resource.loading(null)
        val connection = Jsoup.connect(param)
        try {
            val doc = connection.get()
            mutableResultFlow.value = Resource.success(doc.toDescriptionElements())
        } catch (e: IOException) {
            Logger.getAnonymousLogger().info(e.message)
            mutableResultFlow.value = Resource.error(null)
        }
    }
}

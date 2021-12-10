package com.school.rs.astroevents.domain

import com.school.rs.astroevents.ext.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import org.jsoup.select.Elements

open class FakeStateFlowUseCase : StateFlowUseCase<Resource<Elements>, String>() {

    override val mutableResultFlow: MutableStateFlow<Resource<Elements>> =
        MutableStateFlow(Resource.notInitialized(null))

    override fun performAction(param: String) = Unit
}

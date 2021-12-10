package com.school.rs.astroevents.domain

import kotlinx.coroutines.flow.MutableSharedFlow

class FakeSharedFlowUseCase<T> : SharedFlowUseCase<T>() {

    override val mutableResultFlow = MutableSharedFlow<T>()

    override suspend fun performAction() = Unit
}

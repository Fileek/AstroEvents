package com.school.rs.astroevents.ext

data class Resource<out T>(val status: Status, val data: T?) {

    companion object {

        fun <T> success(data: T?) = Resource(Status.SUCCESS, data)

        fun <T> error(data: T?) = Resource(Status.ERROR, data)

        fun <T> loading(data: T?) = Resource(Status.LOADING, data)

        fun <T> notInitialized(data: T?) = Resource(Status.NOT_INITIALIZED, data)
    }
}

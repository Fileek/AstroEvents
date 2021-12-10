package com.school.rs.astroevents.ui.items

open class Item {
    override fun equals(other: Any?) = hashCode() == other.hashCode()

    override fun hashCode() = javaClass.hashCode()
}

package com.school.rs.astroevents.ui.items

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Event(
    val id: String,
    val date: Date,
    val visibility: OpticalInstrument,
    val summary: String,
    val imageUrl: String,
    val description: String,
    var url: String
) : Item(), Parcelable

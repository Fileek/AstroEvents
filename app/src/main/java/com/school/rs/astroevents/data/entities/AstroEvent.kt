package com.school.rs.astroevents.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.IMAGE_URL_COLUMN
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class AstroEvent(
    @PrimaryKey val id: String,
    val timestamp: String,
    val visibility: String,
    val summary: String,
    @ColumnInfo(name = IMAGE_URL_COLUMN) @SerializedName("image_url") val imageUrl: String,
    val description: String,
    var url: String
)

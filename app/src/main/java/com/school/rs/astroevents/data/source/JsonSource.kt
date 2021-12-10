package com.school.rs.astroevents.data.source

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.school.rs.astroevents.data.entities.AstroEvent
import java.io.IOException
import java.nio.charset.Charset
import java.util.logging.Logger

class JsonSource(private val context: Context) {

    private val logger = Logger.getAnonymousLogger()

    fun getEventsFromJson(): List<AstroEvent> {
        return try {
            val json = getEventsJson()
            Gson().fromJson(json, Array<AstroEvent>::class.java).toList()
        } catch (e: JsonSyntaxException) {
            logger.info(e.message)
            emptyList()
        }
    }

    private fun getEventsJson(): String =
        try {
            context.assets.open("events.json").use {
                val size = it.available()
                val buffer = ByteArray(size)
                it.read(buffer)

                String(buffer, Charset.defaultCharset())
            }
        } catch (e: IOException) {
            logger.info(e.message)
            ""
        }
}

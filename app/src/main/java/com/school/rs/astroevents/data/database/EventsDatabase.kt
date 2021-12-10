package com.school.rs.astroevents.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.school.rs.astroevents.data.dao.EventDao
import com.school.rs.astroevents.data.entities.AstroEvent
import com.school.rs.astroevents.data.source.JsonSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [AstroEvent::class], version = 1, exportSchema = false)
abstract class EventsDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    private class EventsDatabaseCallback(
        context: Context,
        private val scope: CoroutineScope
    ) : Callback() {

        private val jsonSource = JsonSource(context)

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    database.eventDao().insertAll(jsonSource.getEventsFromJson())
                }
            }
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: EventsDatabase? = null

        const val TABLE_NAME = "astro_events"
        const val ID_COLUMN = "id"
        const val TIMESTAMP_COLUMN = "timestamp"
        const val SUMMARY_COLUMN = "summary"
        const val IMAGE_URL_COLUMN = "image_url"
        const val DESCRIPTION_COLUMN = "description"
        const val VISIBILITY_COLUMN = "visibility"
        const val URL_COLUMN = "url"

        fun getDatabase(context: Context, scope: CoroutineScope): EventsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventsDatabase::class.java,
                    TABLE_NAME
                )
                    .addCallback(EventsDatabaseCallback(context, scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

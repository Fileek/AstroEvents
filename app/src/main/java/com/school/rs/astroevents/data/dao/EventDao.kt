package com.school.rs.astroevents.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.school.rs.astroevents.data.entities.AstroEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: Iterable<AstroEvent>)

    @RawQuery(observedEntities = [AstroEvent::class])
    fun getEventsByQuery(query: SupportSQLiteQuery): Flow<List<AstroEvent>>
}

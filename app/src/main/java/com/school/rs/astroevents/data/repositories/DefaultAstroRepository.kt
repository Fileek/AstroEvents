package com.school.rs.astroevents.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteQueryBuilder
import androidx.sqlite.db.SimpleSQLiteQuery
import com.school.rs.astroevents.R
import com.school.rs.astroevents.data.dao.EventDao
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.DESCRIPTION_COLUMN
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.IMAGE_URL_COLUMN
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.SUMMARY_COLUMN
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.TABLE_NAME
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.TIMESTAMP_COLUMN
import com.school.rs.astroevents.data.database.EventsDatabase.Companion.VISIBILITY_COLUMN
import com.school.rs.astroevents.data.entities.AstroEvent
import com.school.rs.astroevents.ext.toOpticalInstrument
import com.school.rs.astroevents.ui.items.Filters
import com.school.rs.astroevents.ui.items.OpticalInstrument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultAstroRepository @Inject constructor(
    private val context: Context,
    private val dao: EventDao,
    private val scope: CoroutineScope,
    private val prefs: SharedPreferences
) : AstroRepository {

    override var astroEvents = emptyList<AstroEvent>()
        private set

    private val _astroEventsFlow = MutableSharedFlow<List<AstroEvent>>(1)
    override val astroEventsFlow: SharedFlow<List<AstroEvent>> = _astroEventsFlow.asSharedFlow()

    override val stickToTodayDate // If UI should scroll to today date after changing filters
        get() = prefs.getBoolean(context.getString(R.string.stick_to_today_switch_key), true)

    private val curOptic // Filter by equipment
        get() = prefs.getString(
            context.getString(R.string.optical_instruments_menu_key),
            context.getString(R.string.telescope_entry)
        )?.toOpticalInstrument(context.resources) ?: OpticalInstrument.TELESCOPE

    private val filters = mutableSetOf<Filters>()
    private val separator = " OR " // for WHERE clauses
    private var job: Job = SupervisorJob() // contains current coroutine for better control

    init {
        emitQuery()
    }

    private fun emitQuery(optic: OpticalInstrument = curOptic) {
        job.cancel() // cancel previous query to get latest result and not longest
        job = scope.launch {
            withContext(Dispatchers.IO) { // switch to background thread
                val selection = getSelection(optic)
                val query = SimpleSQLiteQuery(
                    SQLiteQueryBuilder().run {
                        tables = TABLE_NAME
                        buildQuery(
                            null,
                            selection,
                            null,
                            null,
                            "$TIMESTAMP_COLUMN ASC",
                            null
                        )
                    }
                )

                dao.getEventsByQuery(query).collectLatest {
                    astroEvents = it
                    _astroEventsFlow.emit(it)
                }
            }
        }
    }

    private fun getSelection(optic: OpticalInstrument): String {
        val opticalInstrumentCondition =
            when (optic) {
                OpticalInstrument.NAKED_EYE -> buildCondition(
                    VISIBILITY_COLUMN,
                    optic.name.lowercase()
                )
                OpticalInstrument.BINOCULARS ->
                    "(${
                    buildCondition(
                        VISIBILITY_COLUMN,
                        optic.name.lowercase(),
                        OpticalInstrument.NAKED_EYE.name.lowercase()
                    )
                    })"
                OpticalInstrument.TELESCOPE -> "" // not filter by optic
            }
        return when {
            filters.size == 0 -> opticalInstrumentCondition // no filters means filter only by optic
            opticalInstrumentCondition.isNotEmpty() ->
                filters.joinToString(
                    separator,
                    "$opticalInstrumentCondition AND (", // concatenates optic and events filters
                    ")"
                ) { buildCondition(it) }
            else -> filters.joinToString(separator) { buildCondition(it) } // only events filters
        }
    }

    private fun buildCondition(filter: Filters) = when (filter) {
        Filters.METEOR_SHOWERS -> buildCondition(SUMMARY_COLUMN, "meteor shower")
        Filters.ECLIPSES -> buildCondition(SUMMARY_COLUMN, "eclipse")
        Filters.MOON -> buildCondition(SUMMARY_COLUMN, "Moon")
        Filters.EARTH -> buildCondition(SUMMARY_COLUMN, "equinox", "solstice", "Earth")
        Filters.CONJUNCTIONS -> buildCondition(
            SUMMARY_COLUMN,
            "Conjunction",
            "Close approach",
            "occultation"
        )
        Filters.ASTEROIDS -> buildCondition(SUMMARY_COLUMN, "Asteroid")
        Filters.COMETS -> buildCondition(IMAGE_URL_COLUMN, "comets")
        Filters.GALAXIES_AND_CLUSTERS -> buildCondition(
            DESCRIPTION_COLUMN,
            "galaxy",
            "star cluster"
        )
        Filters.PLANETS -> buildCondition(
            SUMMARY_COLUMN,
            "Mercury",
            "Venus",
            "Mars",
            "Jupiter",
            "Saturn",
            "Uranus",
            "Neptune"
        )
        Filters.ALL -> ""
    }

    private fun buildCondition(column: String, vararg searchRequests: String) =
        searchRequests.joinToString(separator) { "$column LIKE '%$it%'" }

    override fun addFilter(filter: Filters) {
        filters.add(filter)
        emitQuery()
    }

    override fun removeFilter(filter: Filters) {
        filters.remove(filter)
        emitQuery()
    }

    override fun clearFilters() {
        filters.clear()
        emitQuery()
    }

    override fun updateOptic(optic: OpticalInstrument) {
        emitQuery(optic)
    }
}

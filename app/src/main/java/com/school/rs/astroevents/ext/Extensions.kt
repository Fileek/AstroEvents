package com.school.rs.astroevents.ext

import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.school.rs.astroevents.R
import com.school.rs.astroevents.data.entities.AstroEvent
import com.school.rs.astroevents.ui.fragments.DetailsFragment
import com.school.rs.astroevents.ui.items.Event
import com.school.rs.astroevents.ui.items.OpticalInstrument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
* Transform list of [AstroEvent]s to list of UI-look [Event]s
*/
fun List<AstroEvent>.format(resources: Resources): List<Event> = this.map { event ->

    val delimiter = "https:"
    val degree = "&deg;"
    val minute = "&#39;"
    val date = event.timestamp.substringBefore('T')
    val time = event.timestamp.substringAfter('T').removeSuffix("Z")

    Event(
        event.id,
        date = SimpleDateFormat(
            "yyyyMMddHHmmsszzz", Locale.getDefault()
        ).parse(
            "$date${time}UTC"
        ) ?: Date(),
        event.visibility.toOpticalInstrument(resources),
        event.summary,
        event.imageUrl,
        event.description
            .substringBefore(delimiter)
            .replace(degree, "°")
            .replace(minute, "'"),
        event.url
    )
}

/**
 * Cast to [OpticalInstrument]
 * [OpticalInstrument.TELESCOPE] will return, if [CharSequence] is not compatible
 */
fun CharSequence.toOpticalInstrument(resources: Resources): OpticalInstrument = when (this) {
    resources.getString(R.string.naked_eye_value) -> OpticalInstrument.NAKED_EYE
    resources.getString(R.string.binoculars_value) -> OpticalInstrument.BINOCULARS
    else -> OpticalInstrument.TELESCOPE
}

/**
 * Select and filter elements from HTML [Document] for [DetailsFragment] description
 */
fun Document.toDescriptionElements(): Elements { // Later it will be simplified
    val elements = select("div.newsbody")
        .select(
            "p:matches((?=^((?!download).)*\$)" +
                "(?=^((?!animation).)*\$)" +
                "(?=^((?!green cross).)*\$)" +
                "(?=^((?!chart).)*\$))," +
                " h2"
        )
    val descEls = Elements()
    for (i in elements.indices) {
        val el = elements[i]
        when {
            el.toString().contains("autocomplete_location_toggle") -> {
                descEls.add(el)
            }
            el.text().contains("given in J2000") -> {
                descEls.removeLast()
            }
            (descEls.last()?.tagName() == "h2" && el.tagName() == "h2") -> {
                descEls.removeLast()
                descEls.add(el)
            }
            el.text().isEmpty() ||
                el.html().contains("available here") ||
                el.html().contains("green circle") ||
                el.html().contains("style") ||
                el.toString().contains("style") -> {
                if (descEls.last()?.tagName() == "h2") {
                    descEls.removeLast()
                }
            }
            else -> {
                descEls.add(el)
            }
        }
    }
    return descEls
}

/**
 * Launch a coroutine with the given [LifecycleOwner]
 * that collects this [Flow]
 * and repeats it when [Lifecycle] is at least at [Lifecycle.State.STARTED] state
 */
fun <T> Flow<T>.launchAndRepeatWhenStarted(
    lifecycleOwner: LifecycleOwner
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@launchAndRepeatWhenStarted.collectLatest { }
        }
    }
}

/**
 * Launch a coroutine with the given [LifecycleOwner]
 * that collects this [Flow]
 * and repeats it when [Lifecycle] is at least at [Lifecycle.State.CREATED] state
 */
fun <T> Flow<T>.launchAndRepeatWhenCreated(
    lifecycleOwner: LifecycleOwner
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            this@launchAndRepeatWhenCreated.collectLatest { }
        }
    }
}

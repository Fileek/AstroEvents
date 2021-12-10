package com.school.rs.astroevents.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.school.rs.astroevents.R
import com.school.rs.astroevents.databinding.MainFragmentBinding
import com.school.rs.astroevents.ext.launchAndRepeatWhenStarted
import com.school.rs.astroevents.ui.activity.EventListener
import com.school.rs.astroevents.ui.adapter.EventAdapter
import com.school.rs.astroevents.ui.items.Event
import com.school.rs.astroevents.ui.items.Filters
import com.school.rs.astroevents.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment), EventListener {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var prefs: SharedPreferences

    private val viewModel: MainViewModel by viewModels()

    private val filtersArray by lazy { resources.getStringArray(R.array.filters_array) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val binding = MainFragmentBinding.bind(view)
        val eventAdapter = EventAdapter(glide, activity as EventListener)
        val eventLayoutManager = LinearLayoutManager(context)

        viewModel.itemsFlow.onEach {
            eventAdapter.submitList(it)
            if (it.isEmpty()) binding.emptyList.visibility = View.VISIBLE
            else binding.emptyList.visibility = View.INVISIBLE
        }.launchAndRepeatWhenStarted(viewLifecycleOwner)

        viewModel.todayPositionFlow.onEach {
            delay(DELAY_MS_BEFORE_SCROLL)
            eventLayoutManager.scrollToPositionWithOffset(it, 0)
        }.launchAndRepeatWhenStarted(viewLifecycleOwner)

        viewModel.checkedFiltersFlow.onEach {
            updateChipGroup(it, binding.filters)
        }.launchAndRepeatWhenStarted(viewLifecycleOwner)

        binding.apply {
            events.layoutManager = eventLayoutManager
            events.adapter = eventAdapter
        }

        setChipGroup(binding.filters)
    }

    private fun setChipGroup(filters: ChipGroup) {
        for (i in filtersArray.indices) {
            val filter = Filters.values()[i]
            val chip =
                (layoutInflater.inflate(R.layout.filter_chip_item, filters, false)) as Chip
            chip.apply {
                text = filtersArray[i]
                id = filter.ordinal
                setChipListener(this, filter)
                filters.addView(this)
            }
        }
    }

    private fun setChipListener(chip: Chip, filter: Filters) =
        chip.setOnCheckedChangeListener { _, isChecked ->
            val checkedFilters = viewModel.checkedFiltersFlow.value
            when {
                filter == Filters.ALL && // when check "All"
                    filter !in checkedFilters &&
                    isChecked -> viewModel.clearFilters()
                filter !in checkedFilters && isChecked -> viewModel.addFilter(filter)
                filter in checkedFilters && !isChecked -> viewModel.removeFilter(filter)
            }
        }

    private fun updateChipGroup(checkedFilters: Set<Filters>, filters: ChipGroup) {
        val multipleSelectionEnabled =
            prefs.getBoolean(getString(R.string.multiple_filters_switch_key), true)
        filters.isSingleSelection = !multipleSelectionEnabled
        val allChip = filters[Filters.ALL.ordinal] as Chip

        if (checkedFilters.contains(Filters.ALL)) {
            filters.clearCheck()
            allChip.isChecked = true
            allChip.isEnabled = false
        } else {
            checkedFilters.forEach {
                filters.check(it.ordinal)
            }
            allChip.isChecked = false
            allChip.isEnabled = true
        }
    }

    override fun addEventToCalendar(event: Event) {
        val calendarIntent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, event.summary)
            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.date.time)
            .putExtra(CalendarContract.Events.DESCRIPTION, event.description)
        startActivity(calendarIntent)
    }

    private companion object {
        private const val DELAY_MS_BEFORE_SCROLL = 250L
    }
}

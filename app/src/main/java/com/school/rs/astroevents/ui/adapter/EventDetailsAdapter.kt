package com.school.rs.astroevents.ui.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.school.rs.astroevents.ui.fragments.DetailsFragment
import com.school.rs.astroevents.ui.fragments.DetailsFragment.Companion.EVENT_KEY
import com.school.rs.astroevents.ui.items.Event

class EventDetailsAdapter(
    fragment: Fragment,
    private val events: List<Event>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = events.size

    override fun createFragment(position: Int): Fragment {
        val fragment = DetailsFragment()
        fragment.arguments = bundleOf(EVENT_KEY to events[position])
        return fragment
    }
}

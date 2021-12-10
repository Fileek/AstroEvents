package com.school.rs.astroevents.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.school.rs.astroevents.databinding.EventItemBinding
import com.school.rs.astroevents.databinding.TodayDividerItemBinding
import com.school.rs.astroevents.ui.activity.EventListener
import com.school.rs.astroevents.ui.fragments.MainFragmentDirections
import com.school.rs.astroevents.ui.items.Event
import com.school.rs.astroevents.ui.items.Item
import com.school.rs.astroevents.ui.items.TodayDivider

class EventAdapter(
    private val glide: RequestManager,
    private val listener: EventListener
) : ListAdapter<Item, RecyclerView.ViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val holder: RecyclerView.ViewHolder

        if (viewType == EVENT_TYPE) {
            val binding = EventItemBinding.inflate(layoutInflater, parent, false)
            holder = EventViewHolder(binding, glide)
            binding.apply {
                root.setOnClickListener {
                    val position = holder.adapterPosition
                    val todayPosition = currentList.indexOf(TodayDivider)
                    val action = MainFragmentDirections.detailsAction(
                        if (todayPosition != -1 && todayPosition < position) position - 1
                        else position
                    )
                    root.findNavController().navigate(action)
                }
                addToCalendarButton.setOnClickListener {
                    listener.addEventToCalendar(getItem(holder.adapterPosition) as Event)
                }
            }
        } else { // == TODAY_DIVIDER_TYPE
            val binding = TodayDividerItemBinding.inflate(layoutInflater, parent, false)
            holder = TodayViewHolder(binding)
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder) holder.bind(getItem(position) as Event)
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position) is Event) EVENT_TYPE
        else TODAY_DIVIDER_TYPE

    companion object {

        private const val EVENT_TYPE = 0
        private const val TODAY_DIVIDER_TYPE = 1

        private val itemComparator = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item) =
                if (oldItem is Event && newItem is Event)
                    oldItem.id == newItem.id
                else
                    oldItem is TodayDivider && newItem is TodayDivider

            override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
        }
    }
}

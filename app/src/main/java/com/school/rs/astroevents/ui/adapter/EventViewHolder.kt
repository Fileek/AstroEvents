package com.school.rs.astroevents.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.school.rs.astroevents.R
import com.school.rs.astroevents.databinding.EventItemBinding
import com.school.rs.astroevents.ui.items.Event
import com.school.rs.astroevents.ui.items.OpticalInstrument
import java.text.SimpleDateFormat
import java.util.Locale

class EventViewHolder(
    private val binding: EventItemBinding,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("NewApi")
    fun bind(event: Event) {
        binding.apply {
            dateAndTimeView.text =
                SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(event.date)
            summary.text = event.summary
            description.text = event.description
            link.apply {
                text = event.url
                isSingleLine = event.description.length > MAX_SINGLE_LINE_DESCRIPTION_LENGTH
            }
            glide.load(event.imageUrl).into(image)
            visibility.setImageResource(
                when (event.visibility) {
                    OpticalInstrument.TELESCOPE -> R.drawable.telescope
                    OpticalInstrument.BINOCULARS -> R.drawable.binoculars
                    OpticalInstrument.NAKED_EYE -> R.drawable.naked_eye
                }
            )
        }
    }

    private companion object {

        private const val MAX_SINGLE_LINE_DESCRIPTION_LENGTH = 40
    }
}

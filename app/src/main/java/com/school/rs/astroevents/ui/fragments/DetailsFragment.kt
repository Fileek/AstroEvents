package com.school.rs.astroevents.ui.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.school.rs.astroevents.R
import com.school.rs.astroevents.databinding.DetailsFragmentBinding
import com.school.rs.astroevents.ext.Status
import com.school.rs.astroevents.ext.launchAndRepeatWhenCreated
import com.school.rs.astroevents.ui.activity.DetailsEventListener
import com.school.rs.astroevents.ui.items.Event
import com.school.rs.astroevents.ui.viewmodels.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.details_fragment) {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: DetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = DetailsFragmentBinding.bind(view)
        val event = arguments?.getParcelable(EVENT_KEY) as Event?

        if (event != null) {
            setEventInViews(event, binding)
            setListeners(event, binding)
            setDescriptionFromUrl(event.url, binding)
        }
    }

    private fun setEventInViews(event: Event, binding: DetailsFragmentBinding) {
        binding.apply {
            glide.load(event.imageUrl).into(image)
            summary.text = event.summary
            dateAndTimeView.text =
                SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(event.date)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val sourceText = "<a href=\"${event.url}\">${getText(R.string.source)}</a>"
                source.text = Html.fromHtml(sourceText, Html.FROM_HTML_MODE_COMPACT)
                source.movementMethod = LinkMovementMethod.getInstance()
            } else {
                val sourceText = "${getText(R.string.source)}:\n${event.url}"
                source.text = sourceText
            }
        }
    }

    private fun setListeners(event: Event, binding: DetailsFragmentBinding) {
        val listener = activity as DetailsEventListener
        listener.setEventToAddToCalendarListener(event)

        binding.retryButton.setOnClickListener {
            viewModel.fetchDescription(event.url)
        }
    }

    private fun setDescriptionFromUrl(url: String, binding: DetailsFragmentBinding) {
        viewModel.descriptionFlow.onEach {
            binding.apply {
                when (it.status) {
                    Status.NOT_INITIALIZED -> {
                        viewModel.fetchDescription(url)
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        errorMsg.visibility = View.GONE
                        retryButton.visibility = View.GONE
                        source.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        source.visibility = View.VISIBLE
                        it.data?.forEach { el ->
                            addTextViewForElement(el, descriptionLayout)
                        }
                    }
                    Status.ERROR -> {
                        source.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        errorMsg.visibility = View.VISIBLE
                        retryButton.visibility = View.VISIBLE
                    }
                }
            }
        }.launchAndRepeatWhenCreated(viewLifecycleOwner)
    }

    private fun addTextViewForElement(el: Element, parent: ViewGroup) {
        val textView = if (el.tagName() == "h2") {
            layoutInflater.inflate(R.layout.description_header, parent, false) as TextView
        } else {
            layoutInflater.inflate(R.layout.description_paragraph, parent, false) as TextView
        }
        textView.text = el.text()
        parent.addView(textView)
    }

    companion object {
        const val EVENT_KEY = "event"
    }
}

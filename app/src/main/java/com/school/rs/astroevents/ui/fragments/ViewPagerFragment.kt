package com.school.rs.astroevents.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.school.rs.astroevents.R
import com.school.rs.astroevents.databinding.ViewPagerFragmentBinding
import com.school.rs.astroevents.ui.adapter.EventDetailsAdapter
import com.school.rs.astroevents.ui.viewmodels.ViewPagerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewPagerFragment : Fragment(R.layout.view_pager_fragment) {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: ViewPagerViewModel by viewModels()
    private val args: ViewPagerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val eventAdapter = EventDetailsAdapter(this, viewModel.events)
        val binding = ViewPagerFragmentBinding.bind(view)

        binding.eventsPager.apply {
            adapter = eventAdapter
            setCurrentItem(args.position, false)
        }
    }
}

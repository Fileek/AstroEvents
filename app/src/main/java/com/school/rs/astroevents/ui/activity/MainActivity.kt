package com.school.rs.astroevents.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.CalendarContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.school.rs.astroevents.R
import com.school.rs.astroevents.databinding.MainActivityBinding
import com.school.rs.astroevents.ui.fragments.SettingsFragmentDirections
import com.school.rs.astroevents.ui.items.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DetailsEventListener, SettingsListener {

    @Inject
    lateinit var prefs: SharedPreferences

    private val binding by lazy { MainActivityBinding.inflate(layoutInflater) }
    private var appBarConfiguration: AppBarConfiguration? = null

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }
    private val navController by lazy { navHostFragment.navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appTheme =
            prefs.getString(getString(R.string.app_theme_key), getString(R.string.default_system))
        updateAppTheme(appTheme)

        setContentView(binding.root)

        binding.toolbar.apply {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                // hide action buttons, when user is not on the main screen
                menu.clear()
                when (destination.label) {
                    getString(R.string.app_name) -> inflateMenu(R.menu.main_menu)
                    getString(R.string.details_event_label) -> inflateMenu(R.menu.details_event_menu)
                }
            }
            with(AppBarConfiguration(navController.graph)) {
                appBarConfiguration = this
                setupWithNavController(navController, this)
            }
            setOnMenuItemClickListener {
                it.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return appBarConfiguration?.let { navController.navigateUp(it) } == true ||
            super.onSupportNavigateUp()
    }

    override fun updateAppTheme(theme: String?) = AppCompatDelegate.setDefaultNightMode(
        when (theme) {
            getString(R.string.light) -> AppCompatDelegate.MODE_NIGHT_NO
            getString(R.string.dark) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    )

    override fun openInfoFragment() {
        val action = SettingsFragmentDirections.aboutAction()
        navController.navigate(action)
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

    override fun setEventToAddToCalendarListener(event: Event) {
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            addEventToCalendar(event)
            true
        }
    }
}

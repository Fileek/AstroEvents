package com.school.rs.astroevents.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.school.rs.astroevents.R
import com.school.rs.astroevents.data.dao.EventDao
import com.school.rs.astroevents.data.database.EventsDatabase
import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.data.repositories.DefaultAstroRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .error(R.drawable.image_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun providesResources(
        @ApplicationContext context: Context
    ): Resources = context.resources

    @Singleton
    @Provides
    fun providesSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    fun providesScope() = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun providesRoomDao(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope
    ) = EventsDatabase.getDatabase(context, applicationScope).eventDao()

    @Singleton
    @Provides
    fun providesRepository(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        dao: EventDao,
        prefs: SharedPreferences
    ): AstroRepository = DefaultAstroRepository(context, dao, applicationScope, prefs)
}

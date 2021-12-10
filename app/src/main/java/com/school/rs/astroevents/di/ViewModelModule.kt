package com.school.rs.astroevents.di

import android.content.res.Resources
import com.school.rs.astroevents.data.repositories.AstroRepository
import com.school.rs.astroevents.domain.AddFilterUseCase
import com.school.rs.astroevents.domain.ClearFiltersUseCase
import com.school.rs.astroevents.domain.GetCurrentEventsUseCase
import com.school.rs.astroevents.domain.GetDescriptionElementsUseCase
import com.school.rs.astroevents.domain.GetItemsUseCase
import com.school.rs.astroevents.domain.GetTodayIndexUseCase
import com.school.rs.astroevents.domain.ListUseCase
import com.school.rs.astroevents.domain.OpticUseCase
import com.school.rs.astroevents.domain.RemoveFilterUseCase
import com.school.rs.astroevents.domain.SharedFlowUseCase
import com.school.rs.astroevents.domain.StateFlowUseCase
import com.school.rs.astroevents.domain.UpdateOpticUseCase
import com.school.rs.astroevents.ext.Resource
import com.school.rs.astroevents.ui.items.Event
import com.school.rs.astroevents.ui.items.Item
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.jsoup.select.Elements

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun providesGetItemsUseCase(
        repo: AstroRepository,
        resources: Resources
    ): SharedFlowUseCase<List<Item>> = GetItemsUseCase(repo, resources)

    @ViewModelScoped
    @Provides
    fun providesGetTodayIndexUseCase(
        repo: AstroRepository,
        resources: Resources
    ): SharedFlowUseCase<Int> = GetTodayIndexUseCase(repo, resources)

    @Provides
    fun providesAddFilterUseCase(
        repo: AstroRepository
    ) = AddFilterUseCase(repo)

    @Provides
    fun providesRemoveFilterUseCase(
        repo: AstroRepository
    ) = RemoveFilterUseCase(repo)

    @Provides
    fun providesClearFiltersUseCase(
        repo: AstroRepository
    ) = ClearFiltersUseCase(repo)

    @ViewModelScoped
    @Provides
    fun providesUpdateOpticUseCase(
        repo: AstroRepository
    ): OpticUseCase = UpdateOpticUseCase(repo)

    @ViewModelScoped
    @Provides
    fun providesGetCurrentEventsUseCase(
        repo: AstroRepository,
        resources: Resources
    ): ListUseCase<Event> = GetCurrentEventsUseCase(repo, resources)

    @ViewModelScoped
    @Provides
    fun providesGetDescriptionUseCase(): StateFlowUseCase<Resource<Elements>, String> =
        GetDescriptionElementsUseCase()
}

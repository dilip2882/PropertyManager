package com.propertymanager.data.di

import android.content.Context
import com.propertymanager.common.preferences.temp.PreferencesDataSource
import com.propertymanager.data.repository.PreferencesRepositoryImpl
import com.propertymanager.domain.repository.PreferencesRepository
import com.propertymanager.domain.usecase.settings.GetDarkModeUseCase
import com.propertymanager.domain.usecase.settings.GetDynamicColorUseCase
import com.propertymanager.domain.usecase.settings.SetDarkModeUseCase
import com.propertymanager.domain.usecase.settings.SetDynamicColorUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    @Singleton
    fun providesPreferencesDataSource(@ApplicationContext context: Context): PreferencesDataSource {
        return PreferencesDataSource(context)
    }

    @Provides
    @Singleton
    fun providesPreferencesRepository(dataSource: PreferencesDataSource): PreferencesRepository {
        return PreferencesRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun providesSetDynamicColorUseCase(repository: PreferencesRepository): SetDynamicColorUseCase {
        return SetDynamicColorUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesGetDynamicColorUseCase(repository: PreferencesRepository): GetDynamicColorUseCase {
        return GetDynamicColorUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesSetDarkModeUseCase(repository: PreferencesRepository): SetDarkModeUseCase {
        return SetDarkModeUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesGetDarkModeUseCase(repository: PreferencesRepository): GetDarkModeUseCase {
        return GetDarkModeUseCase(repository)
    }

}


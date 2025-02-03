package com.propertymanager.data.di

import android.content.Context
import com.propertymanager.common.preferences.PreferenceStore
import com.propertymanager.common.preferences.temp.PreferencesDataSource
import com.propertymanager.domain.ui.UiPreferences
import com.propertymanager.ui.base.delegate.ThemingDelegate
import com.propertymanager.ui.base.delegate.ThemingDelegateImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesPreferenceStore(@ApplicationContext context: Context): PreferenceStore {
        return PreferencesDataSource(context)
    }

    @Provides
    @Singleton
    fun provideUiPreferences(preferenceStore: PreferenceStore): UiPreferences {
        return UiPreferences(preferenceStore = preferenceStore)
    }

    @Provides
    @Singleton
    fun provideThemingDelegate(uiPreferences: UiPreferences): ThemingDelegate {
        return ThemingDelegateImpl(uiPreferences)
    }

}

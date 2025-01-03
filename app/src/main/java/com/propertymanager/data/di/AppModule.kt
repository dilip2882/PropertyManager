package com.propertymanager.data.di

import com.propertymanager.domain.ui.UiPreferences
import com.propertymanager.ui.base.delegate.ThemingDelegate
import com.propertymanager.ui.base.delegate.ThemingDelegateImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideUiPreferences(): UiPreferences {
        return UiPreferences()
    }

    @Provides
    fun provideThemingDelegate(uiPreferences: UiPreferences): ThemingDelegate {
        return ThemingDelegateImpl(uiPreferences)
    }

}

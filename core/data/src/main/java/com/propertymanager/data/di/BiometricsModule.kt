package com.propertymanager.data.di

import android.content.Context
import com.propertymanager.data.repository.BiometricAuthRepositoryImpl
import com.propertymanager.domain.repository.BiometricAuthRepository
import com.propertymanager.domain.usecase.biometrics.BiometricAvailabilityUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BiometricsModule {

    @Binds
    @Singleton
    abstract fun bindsBiometricAuthRepository(
        biometricAuthRepoImpl: BiometricAuthRepositoryImpl
    ): BiometricAuthRepository


    companion object{
        @Provides
        fun providesBiometricAvailabilityUseCase(
            @ApplicationContext context: Context,
            repository: BiometricAuthRepository
        ): BiometricAvailabilityUseCase {
            return BiometricAvailabilityUseCase(context,repository)
        }

    }

}

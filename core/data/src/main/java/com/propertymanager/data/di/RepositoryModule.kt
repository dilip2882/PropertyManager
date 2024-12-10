package com.propertymanager.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.UserRepositoryImpl
import com.propertymanager.domain.repository.UserRepository
import com.propertymanager.domain.usecase.UserUseCases
import com.propertymanager.domain.usecase.user.GetUserDetailsUseCases
import com.propertymanager.domain.usecase.user.SetUserDetailsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(firebaseFirestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideUserUseCases(repository: UserRepository): UserUseCases {
        return UserUseCases(
            getUserDetailsUseCases = GetUserDetailsUseCases(repository),
            setUserDetailsUseCase = SetUserDetailsUseCase(repository)
        )
    }
}


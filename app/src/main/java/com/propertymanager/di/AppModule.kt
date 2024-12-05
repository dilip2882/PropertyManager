package com.propertymanager.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.UserRepositoryImpl
import com.propertymanager.domain.repository.UserRepository
import com.propertymanager.domain.usecase.UserUseCases
import com.propertymanager.domain.usecase.userprofile.GetUserDetailsUseCases
import com.propertymanager.domain.usecase.userprofile.SetUserDetailsUseCase
import com.propertymanager.utils.DataStoreUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDataStoreUtil(dataStore: DataStore<Preferences>): DataStoreUtil {
        return DataStoreUtil(dataStore)
    }

    @Provides
    @Singleton
    fun providesUserRepository(firestore: FirebaseFirestore) : UserRepository {
        return UserRepositoryImpl(firebaseFirestore = firestore)
    }

    @Provides
    @Singleton
    fun providesUserUseCases(repository: UserRepository) = UserUseCases(
        getUserDetailsUseCases = GetUserDetailsUseCases(userRepository = repository),
        setUserDetailsUseCase = SetUserDetailsUseCase(userRepository = repository)
    )
}


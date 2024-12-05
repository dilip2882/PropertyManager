package com.propertymanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.AuthRepositoryImpl
import com.propertymanager.domain.repository.AuthRepository
import com.propertymanager.domain.usecase.AuthenticationUseCases
import com.propertymanager.domain.usecase.auth.CreateUserWithPhoneUseCase
import com.propertymanager.domain.usecase.auth.FirebaseAuthStateUseCase
import com.propertymanager.domain.usecase.auth.IsUserAuthenticatedUseCase
import com.propertymanager.domain.usecase.auth.SignInWithCredentialUseCase
import com.propertymanager.domain.usecase.auth.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesAuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthRepository {
        return AuthRepositoryImpl(auth = auth, firestore = firestore)
    }

    @Provides
    @Singleton
    fun providesAuthenticationUseCases(repository: AuthRepository) = AuthenticationUseCases(
        createUserWithPhoneUseCase = CreateUserWithPhoneUseCase(repository),
        signInWithCredentialUseCase = SignInWithCredentialUseCase(repository),
        signOutUseCase = SignOutUseCase(repository),
        firebaseAuthStateUseCase = FirebaseAuthStateUseCase(repository),
        isUserAuthenticatedUseCase = IsUserAuthenticatedUseCase(repository)
    )
}

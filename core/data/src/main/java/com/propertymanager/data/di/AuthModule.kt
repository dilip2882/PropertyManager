package com.propertymanager.data.di

import android.app.Activity
import android.content.Context
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.AuthRepositoryImpl
import com.propertymanager.domain.repository.AuthRepository
import com.propertymanager.domain.usecase.AuthenticationUseCases
import com.propertymanager.domain.usecase.auth.CreateUserWithPhoneUseCase
import com.propertymanager.domain.usecase.auth.FirebaseAuthStateUseCase
import com.propertymanager.domain.usecase.auth.IsUserAuthenticatedUseCase
import com.propertymanager.domain.usecase.auth.ResendOtpUseCase
import com.propertymanager.domain.usecase.auth.SignInWithCredentialUseCase
import com.propertymanager.domain.usecase.auth.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun providesAuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore, appCheck: FirebaseAppCheck): AuthRepository {
        return AuthRepositoryImpl(auth = auth, firestore = firestore, context = Activity(), appCheck = appCheck)
    }

    @Provides
    @Singleton
    fun providesAuthenticationUseCases(repository: AuthRepository) = AuthenticationUseCases(
        createUserWithPhoneUseCase = CreateUserWithPhoneUseCase(repository),
        signInWithCredentialUseCase = SignInWithCredentialUseCase(repository),
        resendOtpUseCase = ResendOtpUseCase(repository),
        signOutUseCase = SignOutUseCase(repository),
        firebaseAuthStateUseCase = FirebaseAuthStateUseCase(repository),
        isUserAuthenticatedUseCase = IsUserAuthenticatedUseCase(repository)
    )
}

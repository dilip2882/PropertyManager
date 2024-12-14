package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.auth.CreateUserWithPhoneUseCase
import com.propertymanager.domain.usecase.auth.FirebaseAuthStateUseCase
import com.propertymanager.domain.usecase.auth.IsUserAuthenticatedUseCase
import com.propertymanager.domain.usecase.auth.ResendOtpUseCase
import com.propertymanager.domain.usecase.auth.SignInWithCredentialUseCase
import com.propertymanager.domain.usecase.auth.SignOutUseCase

data class AuthenticationUseCases(
    val createUserWithPhoneUseCase: CreateUserWithPhoneUseCase,
    val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    val resendOtpUseCase: ResendOtpUseCase,
    val signOutUseCase: SignOutUseCase,
    val firebaseAuthStateUseCase: FirebaseAuthStateUseCase,
    val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase
)

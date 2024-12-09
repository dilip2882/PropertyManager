package propertymanager.feature.auth.domain.usecase

import propertymanager.feature.auth.domain.usecase.auth.CreateUserWithPhoneUseCase
import propertymanager.feature.auth.domain.usecase.auth.FirebaseAuthStateUseCase
import propertymanager.feature.auth.domain.usecase.auth.IsUserAuthenticatedUseCase
import propertymanager.feature.auth.domain.usecase.auth.ResendOtpUseCase
import propertymanager.feature.auth.domain.usecase.auth.SignInWithCredentialUseCase
import propertymanager.feature.auth.domain.usecase.auth.SignOutUseCase

data class AuthenticationUseCases(
    val createUserWithPhoneUseCase: CreateUserWithPhoneUseCase,
    val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    val resendOtpUseCase: ResendOtpUseCase,
    val signOutUseCase: SignOutUseCase,
    val firebaseAuthStateUseCase: FirebaseAuthStateUseCase,
    val isUserAuthenticatedUseCase: IsUserAuthenticatedUseCase
)

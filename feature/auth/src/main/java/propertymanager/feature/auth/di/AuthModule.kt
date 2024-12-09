package propertymanager.feature.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import propertymanager.feature.auth.data.AuthRepositoryImpl
import propertymanager.feature.auth.domain.repository.AuthRepository
import propertymanager.feature.auth.domain.usecase.AuthenticationUseCases
import propertymanager.feature.auth.domain.usecase.auth.CreateUserWithPhoneUseCase
import propertymanager.feature.auth.domain.usecase.auth.FirebaseAuthStateUseCase
import propertymanager.feature.auth.domain.usecase.auth.IsUserAuthenticatedUseCase
import propertymanager.feature.auth.domain.usecase.auth.ResendOtpUseCase
import propertymanager.feature.auth.domain.usecase.auth.SignInWithCredentialUseCase
import propertymanager.feature.auth.domain.usecase.auth.SignOutUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

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
        resendOtpUseCase = ResendOtpUseCase(repository),
        signOutUseCase = SignOutUseCase(repository),
        firebaseAuthStateUseCase = FirebaseAuthStateUseCase(repository),
        isUserAuthenticatedUseCase = IsUserAuthenticatedUseCase(repository)
    )
}

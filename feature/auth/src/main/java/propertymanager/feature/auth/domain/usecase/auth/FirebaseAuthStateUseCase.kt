package propertymanager.feature.auth.domain.usecase.auth

import propertymanager.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = authRepository.getFirebaseAuthState()
}

package propertymanager.feature.auth.domain.usecase.auth

import com.propertymanager.common.utils.Response
import propertymanager.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithCredentialUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(otp: String): Flow<Response<String>> {
        return authRepository.signInWithCredential(otp)
    }
}

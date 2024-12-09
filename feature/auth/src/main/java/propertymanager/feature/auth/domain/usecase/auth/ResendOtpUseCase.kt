package propertymanager.feature.auth.domain.usecase.auth

import android.app.Activity
import propertymanager.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phone: String, activity: Activity) =
        authRepository.resendOtp(phone, activity)

}

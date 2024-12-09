package propertymanager.feature.auth.domain.usecase.auth

import android.app.Activity
import com.propertymanager.common.utils.Response
import propertymanager.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateUserWithPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phone: String, activity: Activity): Flow<Response<String>> {
        return authRepository.createUserWithPhone(phone, activity)
    }
}

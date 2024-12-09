package propertymanager.feature.auth.domain.usecase.auth

import propertymanager.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class IsUserAuthenticatedUseCase @Inject constructor(
    private val repository: AuthRepository
){
    suspend operator fun invoke() = repository.isUserAuthenticatedInFirebase()
}

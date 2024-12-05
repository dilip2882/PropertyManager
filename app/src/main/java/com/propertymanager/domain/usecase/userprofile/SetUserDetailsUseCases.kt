package com.propertymanager.domain.usecase.userprofile

import com.propertymanager.domain.model.User
import com.propertymanager.domain.repository.UserRepository
import com.propertymanager.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetUserDetailsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(user: User): Flow<Response<Boolean>> {
        return userRepository.setUserDetails(user)
    }
}
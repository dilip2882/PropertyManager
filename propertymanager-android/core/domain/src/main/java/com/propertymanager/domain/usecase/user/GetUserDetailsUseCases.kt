package com.propertymanager.domain.usecase.user

import com.propertymanager.domain.repository.UserRepository
import javax.inject.Inject

class GetUserDetailsUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userid: String) =
        userRepository.getUserDetails(userid = userid)
}

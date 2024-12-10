package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.user.GetUserDetailsUseCases
import com.propertymanager.domain.usecase.user.SetUserDetailsUseCase

data class UserUseCases(
    val getUserDetailsUseCases: GetUserDetailsUseCases,
    val setUserDetailsUseCase: SetUserDetailsUseCase
)

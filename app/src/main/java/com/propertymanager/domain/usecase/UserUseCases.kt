package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.userprofile.GetUserDetailsUseCases
import com.propertymanager.domain.usecase.userprofile.SetUserDetailsUseCase

data class UserUseCases(
    val getUserDetailsUseCases: GetUserDetailsUseCases,
    val setUserDetailsUseCase: SetUserDetailsUseCase
)
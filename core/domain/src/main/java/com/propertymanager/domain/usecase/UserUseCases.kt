package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.user.AssociatePropertyUseCase
import com.propertymanager.domain.usecase.user.GetCurrentUserUseCase
import com.propertymanager.domain.usecase.user.GetUserDetailsUseCases
import com.propertymanager.domain.usecase.user.SetUserDetailsUseCase
import com.propertymanager.domain.usecase.user.UpdateSelectedPropertyUseCase

data class UserUseCases(
    val getUserDetailsUseCases: GetUserDetailsUseCases,
    val setUserDetailsUseCase: SetUserDetailsUseCase,
    val getCurrentUser: GetCurrentUserUseCase,
    val updateSelectedProperty: UpdateSelectedPropertyUseCase,
    val associateProperty: AssociatePropertyUseCase
)

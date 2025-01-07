package com.propertymanager.domain.usecase.location.society

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class FindSocietyByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(societyId: Int) =
        repository.findSocietyById(societyId)
}

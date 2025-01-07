package com.propertymanager.domain.usecase.location.tower

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class GetTowersForSocietyUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(societyId: Int) =
        repository.getTowersForSociety(societyId)
}

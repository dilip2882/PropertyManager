package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class GetFlatsForSocietyUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(societyId: Int) =
        repository.getFlatsForSociety(societyId)
}

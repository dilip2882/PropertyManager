package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateFlatUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(towerId: Int, flat: Flat): Result<Unit> =
        repository.updateFlat(towerId, flat)
}


package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteFlatUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(towerId: Int, flatId: Int): Result<Unit> =
        repository.deleteFlat(towerId, flatId)
}

package com.propertymanager.domain.usecase.location.tower

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteTowerUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(towerId: Int): Result<Unit> {
        return repository.deleteTower(towerId)
    }
}

package com.propertymanager.domain.usecase.location.tower

import com.propertymanager.domain.model.location.Tower
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddTowerUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(tower: Tower): Result<Unit> {
        return repository.addTower(tower)
    }
}

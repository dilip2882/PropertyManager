package com.propertymanager.domain.usecase.location.tower

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class FindTowerByIdUseCase @Inject constructor(
    val repository: LocationRepository
) {
    suspend operator fun invoke(towerId: Int) =
        repository.findTowerById(towerId)
}

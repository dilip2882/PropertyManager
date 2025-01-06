package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlatsForTowerUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(towerId: Int): Flow<List<Flat>> =
        repository.getFlatsForTower(towerId)
}

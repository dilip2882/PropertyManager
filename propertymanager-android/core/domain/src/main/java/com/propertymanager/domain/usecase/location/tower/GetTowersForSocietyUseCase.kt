package com.propertymanager.domain.usecase.location.tower

import com.propertymanager.domain.model.location.Tower
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTowersForSocietyUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(societyId: Int): Flow<List<Tower>> {
        return repository.getTowersForSociety(societyId)
    }
}

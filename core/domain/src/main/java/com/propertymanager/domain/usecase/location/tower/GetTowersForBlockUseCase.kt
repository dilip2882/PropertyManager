package com.propertymanager.domain.usecase.location.tower

import com.propertymanager.domain.model.location.Tower
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTowersForBlockUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(blockId: Int): Flow<List<Tower>> =
        repository.getTowersForBlock(blockId)
}

package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetFlatsForBlockUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(blockId: Int): Flow<List<Flat>> {
        return repository.getFlatsForBlock(blockId)
    }
}

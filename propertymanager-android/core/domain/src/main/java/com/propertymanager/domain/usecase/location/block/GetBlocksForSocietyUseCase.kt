package com.propertymanager.domain.usecase.location.block

import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlocksForSocietyUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(societyId: Int): Flow<List<Block>> =
        repository.getBlocksForSociety(societyId)
}

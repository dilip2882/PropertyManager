package com.propertymanager.domain.usecase.location.block

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteBlockUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(societyId: Int, blockId: Int): Result<Unit> =
        repository.deleteBlock(societyId, blockId)
}

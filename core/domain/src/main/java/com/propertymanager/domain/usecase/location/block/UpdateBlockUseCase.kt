package com.propertymanager.domain.usecase.location.block

import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class UpdateBlockUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(societyId: Int, block: Block): Result<Unit> =
        repository.updateBlock(societyId, block)
}

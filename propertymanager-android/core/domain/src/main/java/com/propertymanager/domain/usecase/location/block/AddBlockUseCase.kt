package com.propertymanager.domain.usecase.location.block

import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddBlockUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(block: Block): Result<Unit> {
        return repository.addBlock(block)
    }
}

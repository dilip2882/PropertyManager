package com.propertymanager.domain.usecase.location.block

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class FindBlockByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(blockId: Int) =
        repository.findBlockById(blockId)
}

package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class GetFlatsForBlockUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(blockId: Int) =
        repository.getFlatsForBlock(blockId)
}

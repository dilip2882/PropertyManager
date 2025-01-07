package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class FindFlatByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(flatId: Int) =
        repository.findFlatById(flatId)
}

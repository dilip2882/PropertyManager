package com.propertymanager.domain.usecase.location.flat

import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddFlatUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(flat: Flat): Result<Unit> {
        return repository.addFlat(flat)
    }
}

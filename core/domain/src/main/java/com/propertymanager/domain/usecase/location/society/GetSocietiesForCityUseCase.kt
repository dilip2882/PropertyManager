package com.propertymanager.domain.usecase.location.society

import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSocietiesForCityUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(cityId: Int): Flow<List<Society>> {
        return repository.getSocietiesForCity(cityId)
    }
}

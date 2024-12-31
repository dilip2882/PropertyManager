package com.propertymanager.domain.repository

import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.State
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCountries(): Flow<List<Country>>
    suspend fun getStatesForCountry(countryId: Int): Flow<List<State>>
    suspend fun getCitiesForState(stateId: Int): Flow<List<City>>
    suspend fun addCountry(country: Country): Result<Unit>
    suspend fun updateCountry(country: Country): Result<Unit>
    suspend fun deleteCountry(countryId: Int): Result<Unit>
    suspend fun addState(countryId: Int, state: State): Result<Unit>
    suspend fun updateState(countryId: Int, state: State): Result<Unit>
    suspend fun deleteState(countryId: Int, stateId: Int): Result<Unit>
    suspend fun addCity(stateId: Int, city: City): Result<Unit>
    suspend fun updateCity(stateId: Int, city: City): Result<Unit>
    suspend fun deleteCity(stateId: Int, cityId: Int): Result<Unit>
}


package com.propertymanager.domain.repository

import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    // Country
    suspend fun getCountries(): Flow<List<Country>>
    suspend fun findCountryById(countryId: Int): Result<Country?>
    suspend fun addCountry(country: Country): Result<Unit>
    suspend fun updateCountry(country: Country): Result<Unit>
    suspend fun deleteCountry(countryId: Int): Result<Unit>

    // State
    suspend fun getStatesForCountry(countryId: Int): Flow<List<State>>
    suspend fun findStateById(stateId: Int): Result<State?>
    suspend fun addState(state: State): Result<Unit>
    suspend fun updateState(state: State): Result<Unit>
    suspend fun deleteState(stateId: Int): Result<Unit>

    // City
    suspend fun getCitiesForState(stateId: Int): Flow<List<City>>
    suspend fun findCityById(cityId: Int): Result<City?>
    suspend fun addCity(city: City): Result<Unit>
    suspend fun updateCity(city: City): Result<Unit>
    suspend fun deleteCity(cityId: Int): Result<Unit>

    // Society
    suspend fun getSocietiesForCity(cityId: Int): Flow<List<Society>>
    suspend fun findSocietyById(societyId: Int): Result<Society?>
    suspend fun addSociety(society: Society): Result<Unit>
    suspend fun updateSociety(society: Society): Result<Unit>
    suspend fun deleteSociety(societyId: Int): Result<Unit>

    // Block
    suspend fun getBlocksForSociety(societyId: Int): Flow<List<Block>>
    suspend fun findBlockById(blockId: Int): Result<Block?>
    suspend fun addBlock(block: Block): Result<Unit>
    suspend fun updateBlock(block: Block): Result<Unit>
    suspend fun deleteBlock(blockId: Int): Result<Unit>

    // Tower
    suspend fun getTowersForSociety(societyId: Int): Flow<List<Tower>>
    suspend fun getTowersForBlock(blockId: Int): Flow<List<Tower>>
    suspend fun findTowerById(towerId: Int): Result<Tower?>
    suspend fun addTower(tower: Tower): Result<Unit>
    suspend fun updateTower(tower: Tower): Result<Unit>
    suspend fun deleteTower(towerId: Int): Result<Unit>

    // Flat
    suspend fun getFlatsForSociety(societyId: Int): Flow<List<Flat>>
    suspend fun getFlatsForBlock(blockId: Int): Flow<List<Flat>>
    suspend fun getFlatsForTower(towerId: Int): Flow<List<Flat>>
    suspend fun findFlatById(flatId: Int): Result<Flat?>
    suspend fun addFlat(flat: Flat): Result<Unit>
    suspend fun updateFlat(flat: Flat): Result<Unit>
    suspend fun deleteFlat(flatId: Int): Result<Unit>
}

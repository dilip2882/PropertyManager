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

    // Country-State-City
    suspend fun getCountries(): Flow<List<Country>>
    suspend fun findCountryById(countryId: Int): Result<Country?>
    suspend fun getStatesForCountry(countryId: Int): Flow<List<State>>
    suspend fun findStateById(stateId: Int): Result<State?>
    suspend fun getCitiesForState(stateId: Int): Flow<List<City>>
    suspend fun findCityById(cityId: Int): Result<City?>
    suspend fun addCountry(country: Country): Result<Unit>
    suspend fun updateCountry(country: Country): Result<Unit>
    suspend fun deleteCountry(countryId: Int): Result<Unit>
    suspend fun addState(countryId: Int, state: State): Result<Unit>
    suspend fun updateState(countryId: Int, state: State): Result<Unit>
    suspend fun deleteState(countryId: Int, stateId: Int): Result<Unit>
    suspend fun addCity(stateId: Int, city: City): Result<Unit>
    suspend fun updateCity(stateId: Int, city: City): Result<Unit>
    suspend fun deleteCity(stateId: Int, cityId: Int): Result<Unit>

    // Society
    suspend fun getSocietiesForCity(cityId: Int): Flow<List<Society>>
    suspend fun findSocietyById(societyId: Int): Result<Society?>
    suspend fun addSociety(cityId: Int, society: Society): Result<Unit>
    suspend fun updateSociety(cityId: Int, society: Society): Result<Unit>
    suspend fun deleteSociety(cityId: Int, societyId: Int): Result<Unit>

    // Block
    suspend fun getBlocksForSociety(societyId: Int): Flow<List<Block>>
    suspend fun findBlockById(blockId: Int): Result<Block?>
    suspend fun addBlock(societyId: Int, block: Block): Result<Unit>
    suspend fun updateBlock(societyId: Int, block: Block): Result<Unit>
    suspend fun deleteBlock(societyId: Int, blockId: Int): Result<Unit>

    // Tower
    suspend fun getTowersForSociety(societyId: Int): Flow<List<Tower>>
    suspend fun findTowerById(towerId: Int): Result<Tower?>
    suspend fun addTower(blockId: Int, tower: Tower): Result<Unit>
    suspend fun updateTower(blockId: Int, tower: Tower): Result<Unit>
    suspend fun deleteTower(blockId: Int, towerId: Int): Result<Unit>

    // Flat
    suspend fun getFlatsForSociety(societyId: Int): Flow<List<Flat>>
    suspend fun getFlatsForBlock(blockId: Int): Flow<List<Flat>>
    suspend fun getFlatsForTower(towerId: Int): Flow<List<Flat>>
    suspend fun findFlatById(flatId: Int): Result<Flat?>
    suspend fun addFlat(towerId: Int, flat: Flat): Result<Unit>
    suspend fun updateFlat(towerId: Int, flat: Flat): Result<Unit>
    suspend fun deleteFlat(towerId: Int, flatId: Int): Result<Unit>
}

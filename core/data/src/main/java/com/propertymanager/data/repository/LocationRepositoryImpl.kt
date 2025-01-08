package com.propertymanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.toObject
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LocationRepository {

    private val locationsRef = firestore.collection("locations")

    private companion object {
        const val DATA_DOC = "data"
        const val COUNTRIES_COLLECTION = "countries"
        const val STATES_COLLECTION = "states"
        const val CITIES_COLLECTION = "cities"
        const val SOCIETIES_COLLECTION = "societies"
        const val BLOCKS_COLLECTION = "blocks"
        const val TOWERS_COLLECTION = "towers"
        const val FLATS_COLLECTION = "flats"
    }

    // Collection references
    private fun countriesRef() = locationsRef.document(DATA_DOC).collection(COUNTRIES_COLLECTION)
    private fun statesRef() = locationsRef.document(DATA_DOC).collection(STATES_COLLECTION)
    private fun citiesRef() = locationsRef.document(DATA_DOC).collection(CITIES_COLLECTION)
    private fun societiesRef() = locationsRef.document(DATA_DOC).collection(SOCIETIES_COLLECTION)
    private fun blocksRef() = locationsRef.document(DATA_DOC).collection(BLOCKS_COLLECTION)
    private fun towersRef() = locationsRef.document(DATA_DOC).collection(TOWERS_COLLECTION)
    private fun flatsRef() = locationsRef.document(DATA_DOC).collection(FLATS_COLLECTION)

    // Country operations
    override suspend fun getCountries(): Flow<List<Country>> = callbackFlow {
        val subscription = countriesRef()
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val countries = snapshot?.documents?.mapNotNull {
                    it.toObject<Country>()
                } ?: emptyList()
                trySend(countries)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findCountryById(countryId: Int): Result<Country?> = safeFirestoreCall {
        countriesRef().document(countryId.toString()).get().await().toObject<Country>()
    }

    override suspend fun addCountry(country: Country): Result<Unit> = safeFirestoreCall {
        val newCountryRef = countriesRef().document()
        val countryWithId = country.copy(id = newCountryRef.id.hashCode())
        newCountryRef.set(countryWithId).await()
    }

    override suspend fun updateCountry(country: Country): Result<Unit> = safeFirestoreCall {
        countriesRef().document(country.id.toString())
            .set(country, SetOptions.merge()).await()
    }

    override suspend fun deleteCountry(countryId: Int): Result<Unit> = safeFirestoreCall {
        // First get all related data
        val states = statesRef().whereEqualTo("countryId", countryId)
            .get().await().documents
        
        val batch = firestore.batch()
        
        // Delete country
        batch.delete(countriesRef().document(countryId.toString()))
        
        // Delete states
        states.forEach { stateDoc ->
            val stateId = stateDoc.id.toInt()
            batch.delete(stateDoc.reference)
            
            // Get and delete cities
            val cities = citiesRef().whereEqualTo("stateId", stateId)
                .get().await().documents
            cities.forEach { cityDoc ->
                val cityId = cityDoc.id.toInt()
                batch.delete(cityDoc.reference)
                
                // Get and delete societies
                val societies = societiesRef().whereEqualTo("cityId", cityId)
                    .get().await().documents
                societies.forEach { societyDoc ->
                    val societyId = societyDoc.id.toInt()
                    batch.delete(societyDoc.reference)
                    
                    // Delete blocks and their flats
                    val blocks = blocksRef().whereEqualTo("societyId", societyId)
                        .get().await().documents
                    blocks.forEach { blockDoc ->
                        batch.delete(blockDoc.reference)
                        
                        // Delete flats in block
                        val blockFlats = flatsRef()
                            .whereEqualTo("blockId", blockDoc.id.toInt())
                            .get().await().documents
                        blockFlats.forEach { flatDoc ->
                            batch.delete(flatDoc.reference)
                        }
                    }
                    
                    // Delete towers and their flats
                    val towers = towersRef().whereEqualTo("societyId", societyId)
                        .get().await().documents
                    towers.forEach { towerDoc ->
                        batch.delete(towerDoc.reference)
                        
                        // Delete flats in tower
                        val towerFlats = flatsRef()
                            .whereEqualTo("towerId", towerDoc.id.toInt())
                            .get().await().documents
                        towerFlats.forEach { flatDoc ->
                            batch.delete(flatDoc.reference)
                        }
                    }
                    
                    // Delete society's direct flats
                    val societyFlats = flatsRef()
                        .whereEqualTo("societyId", societyId)
                        .whereEqualTo("blockId", null)
                        .whereEqualTo("towerId", null)
                        .get().await().documents
                    societyFlats.forEach { flatDoc ->
                        batch.delete(flatDoc.reference)
                    }
                }
            }
        }
        
        batch.commit().await()
    }

    // State operations
    override suspend fun getStatesForCountry(countryId: Int): Flow<List<State>> = callbackFlow {
        val subscription = statesRef()
            .whereEqualTo("countryId", countryId)
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val states = snapshot?.documents?.mapNotNull {
                    it.toObject<State>()
                } ?: emptyList()
                trySend(states)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findStateById(stateId: Int): Result<State?> = safeFirestoreCall {
        statesRef().document(stateId.toString()).get().await().toObject<State>()
    }

    override suspend fun addState(state: State): Result<Unit> = safeFirestoreCall {
        val newStateRef = statesRef().document()
        val stateWithId = state.copy(id = newStateRef.id.hashCode())
        newStateRef.set(stateWithId).await()
    }

    override suspend fun updateState(state: State): Result<Unit> = safeFirestoreCall {
        statesRef().document(state.id.toString())
            .set(state, SetOptions.merge()).await()
    }

    override suspend fun deleteState(stateId: Int): Result<Unit> = safeFirestoreCall {
        val batch = firestore.batch()
        
        // Delete state
        batch.delete(statesRef().document(stateId.toString()))
        
        // Get and delete cities
        val cities = citiesRef().whereEqualTo("stateId", stateId)
            .get().await().documents
        cities.forEach { cityDoc ->
            val cityId = cityDoc.id.toInt()
            batch.delete(cityDoc.reference)
            
            // Delete societies and their children
            val societies = societiesRef().whereEqualTo("cityId", cityId)
                .get().await().documents
            societies.forEach { societyDoc ->
                // ... similar pattern for society children
            }
        }
        
        batch.commit().await()
    }

    // City operations
    override suspend fun getCitiesForState(stateId: Int): Flow<List<City>> = callbackFlow {
        val subscription = citiesRef()
            .whereEqualTo("stateId", stateId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val cities = snapshot?.documents?.mapNotNull {
                    it.toObject<City>()
                }?.sortedBy { it.name } ?: emptyList()
                trySend(cities)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findCityById(cityId: Int): Result<City?> = safeFirestoreCall {
        citiesRef().document(cityId.toString()).get().await().toObject<City>()
    }

    override suspend fun addCity(city: City): Result<Unit> = safeFirestoreCall {
        val newCityRef = citiesRef().document()
        val cityWithId = city.copy(id = newCityRef.id.hashCode())
        newCityRef.set(cityWithId).await()
    }

    override suspend fun updateCity(city: City): Result<Unit> = safeFirestoreCall {
        citiesRef().document(city.id.toString())
            .set(city, SetOptions.merge()).await()
    }

    override suspend fun deleteCity(cityId: Int): Result<Unit> = safeFirestoreCall {
        val batch = firestore.batch()
        
        // Delete city
        batch.delete(citiesRef().document(cityId.toString()))
        
        // Get and delete societies
        val societies = societiesRef().whereEqualTo("cityId", cityId)
            .get().await().documents
        societies.forEach { societyDoc ->
            val societyId = societyDoc.id.toInt()
            batch.delete(societyDoc.reference)
            
            // Delete blocks and their flats
            val blocks = blocksRef().whereEqualTo("societyId", societyId)
                .get().await().documents
            blocks.forEach { blockDoc ->
                batch.delete(blockDoc.reference)
                
                // Delete flats in block
                val blockFlats = flatsRef()
                    .whereEqualTo("blockId", blockDoc.id.toInt())
                    .get().await().documents
                blockFlats.forEach { flatDoc ->
                    batch.delete(flatDoc.reference)
                }
            }
            
            // Delete towers and their flats
            val towers = towersRef().whereEqualTo("societyId", societyId)
                .get().await().documents
            towers.forEach { towerDoc ->
                batch.delete(towerDoc.reference)
                
                // Delete flats in tower
                val towerFlats = flatsRef()
                    .whereEqualTo("towerId", towerDoc.id.toInt())
                    .get().await().documents
                towerFlats.forEach { flatDoc ->
                    batch.delete(flatDoc.reference)
                }
            }
            
            // Delete society's direct flats
            val societyFlats = flatsRef()
                .whereEqualTo("societyId", societyId)
                .whereEqualTo("blockId", null)
                .whereEqualTo("towerId", null)
                .get().await().documents
            societyFlats.forEach { flatDoc ->
                batch.delete(flatDoc.reference)
            }
        }
        
        batch.commit().await()
    }

    // Helper function for error handling
    private suspend fun <T> safeFirestoreCall(call: suspend () -> T): Result<T> = 
        try {
            Result.success(call())
        } catch (e: Exception) {
            Result.failure(e)
    }

    // Society operations
    override suspend fun getSocietiesForCity(cityId: Int): Flow<List<Society>> = callbackFlow {
        val subscription = societiesRef()
            .whereEqualTo("cityId", cityId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val societies = snapshot?.documents?.mapNotNull {
                    it.toObject<Society>()
                }?.sortedBy { it.name } ?: emptyList()
                trySend(societies)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findSocietyById(societyId: Int): Result<Society?> = safeFirestoreCall {
        societiesRef().document(societyId.toString()).get().await().toObject<Society>()
    }

    override suspend fun addSociety(society: Society): Result<Unit> = safeFirestoreCall {
        val newSocietyRef = societiesRef().document()
        val societyWithId = society.copy(id = newSocietyRef.id.hashCode())
        newSocietyRef.set(societyWithId).await()
    }

    override suspend fun updateSociety(society: Society): Result<Unit> = safeFirestoreCall {
        societiesRef().document(society.id.toString())
            .set(society, SetOptions.merge()).await()
    }

    override suspend fun deleteSociety(societyId: Int): Result<Unit> = safeFirestoreCall {
        val batch = firestore.batch()
        
        // Delete society
        batch.delete(societiesRef().document(societyId.toString()))
        
        // Delete blocks and their flats
        val blocks = blocksRef().whereEqualTo("societyId", societyId)
            .get().await().documents
        blocks.forEach { blockDoc ->
            batch.delete(blockDoc.reference)
            
            // Delete flats in block
            val blockFlats = flatsRef()
                .whereEqualTo("blockId", blockDoc.id.toInt())
                .get().await().documents
            blockFlats.forEach { flatDoc ->
                batch.delete(flatDoc.reference)
            }
        }
        
        // Delete towers and their flats
        val towers = towersRef().whereEqualTo("societyId", societyId)
            .get().await().documents
        towers.forEach { towerDoc ->
            batch.delete(towerDoc.reference)
            
            // Delete flats in tower
            val towerFlats = flatsRef()
                .whereEqualTo("towerId", towerDoc.id.toInt())
                .get().await().documents
            towerFlats.forEach { flatDoc ->
                batch.delete(flatDoc.reference)
            }
        }
        
        // Delete society's direct flats
        val societyFlats = flatsRef()
            .whereEqualTo("societyId", societyId)
            .whereEqualTo("blockId", null)
            .whereEqualTo("towerId", null)
            .get().await().documents
        societyFlats.forEach { flatDoc ->
            batch.delete(flatDoc.reference)
        }
        
        batch.commit().await()
    }

    // Block operations
    override suspend fun getBlocksForSociety(societyId: Int): Flow<List<Block>> = callbackFlow {
        val subscription = blocksRef()
            .whereEqualTo("societyId", societyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val blocks = snapshot?.documents?.mapNotNull {
                    it.toObject<Block>()
                }?.sortedBy { it.name } ?: emptyList()
                trySend(blocks)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findBlockById(blockId: Int): Result<Block?> = safeFirestoreCall {
        blocksRef().document(blockId.toString()).get().await().toObject<Block>()
    }

    override suspend fun addBlock(block: Block): Result<Unit> = safeFirestoreCall {
        val newBlockRef = blocksRef().document()
        val blockWithId = block.copy(id = newBlockRef.id.hashCode())
        newBlockRef.set(blockWithId).await()
    }

    override suspend fun updateBlock(block: Block): Result<Unit> = safeFirestoreCall {
        blocksRef().document(block.id.toString())
            .set(block, SetOptions.merge()).await()
    }

    override suspend fun deleteBlock(blockId: Int): Result<Unit> = safeFirestoreCall {
        val batch = firestore.batch()
        
        // Delete block
        batch.delete(blocksRef().document(blockId.toString()))
        
        // Delete flats in block
        val blockFlats = flatsRef()
            .whereEqualTo("blockId", blockId)
            .get().await().documents
        blockFlats.forEach { flatDoc ->
            batch.delete(flatDoc.reference)
        }
        
        batch.commit().await()
    }

    // Tower operations
    override suspend fun getTowersForSociety(societyId: Int): Flow<List<Tower>> = callbackFlow {
        val subscription = towersRef()
            .whereEqualTo("societyId", societyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val towers = snapshot?.documents?.mapNotNull {
                    it.toObject<Tower>()
                }?.sortedBy { it.name } ?: emptyList()
                trySend(towers)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getTowersForBlock(blockId: Int): Flow<List<Tower>> = callbackFlow {
        val subscription = towersRef()
            .whereEqualTo("blockId", blockId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                close(error)
                    return@addSnapshotListener
                }
                val towers = snapshot?.documents?.mapNotNull {
                    it.toObject<Tower>()
                }?.sortedBy { it.name } ?: emptyList()
                trySend(towers)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findTowerById(towerId: Int): Result<Tower?> = safeFirestoreCall {
        towersRef().document(towerId.toString()).get().await().toObject<Tower>()
    }

    override suspend fun addTower(tower: Tower): Result<Unit> = safeFirestoreCall {
        val newTowerRef = towersRef().document()
        val towerWithId = tower.copy(id = newTowerRef.id.hashCode())
        newTowerRef.set(towerWithId).await()
    }

    override suspend fun updateTower(tower: Tower): Result<Unit> = safeFirestoreCall {
        towersRef().document(tower.id.toString())
            .set(tower, SetOptions.merge()).await()
    }

    override suspend fun deleteTower(towerId: Int): Result<Unit> = safeFirestoreCall {
        val batch = firestore.batch()
        
        // Delete tower
        batch.delete(towersRef().document(towerId.toString()))
        
        // Delete flats in tower
        val towerFlats = flatsRef()
            .whereEqualTo("towerId", towerId)
            .get().await().documents
        towerFlats.forEach { flatDoc ->
            batch.delete(flatDoc.reference)
        }
        
        batch.commit().await()
    }

    // Flat operations
    override suspend fun getFlatsForSociety(societyId: Int): Flow<List<Flat>> = callbackFlow {
        val subscription = flatsRef()
            .whereEqualTo("societyId", societyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val flats = snapshot?.documents?.mapNotNull {
                    it.toObject<Flat>()
                }?.sortedBy { it.number } ?: emptyList()
                trySend(flats)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getFlatsForBlock(blockId: Int): Flow<List<Flat>> = callbackFlow {
        val subscription = flatsRef()
            .whereEqualTo("blockId", blockId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val flats = snapshot?.documents?.mapNotNull {
                    it.toObject<Flat>()
                }?.sortedBy { it.number } ?: emptyList()
                trySend(flats)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getFlatsForTower(towerId: Int): Flow<List<Flat>> = callbackFlow {
        val subscription = flatsRef()
            .whereEqualTo("towerId", towerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val flats = snapshot?.documents?.mapNotNull {
                    it.toObject<Flat>()
                }?.sortedBy { it.number } ?: emptyList()
                trySend(flats)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findFlatById(flatId: Int): Result<Flat?> = safeFirestoreCall {
        flatsRef().document(flatId.toString()).get().await().toObject<Flat>()
    }

    override suspend fun addFlat(flat: Flat): Result<Unit> = safeFirestoreCall {
        val newFlatRef = flatsRef().document()
        val flatWithId = flat.copy(id = newFlatRef.id.hashCode())
        newFlatRef.set(flatWithId).await()
    }

    override suspend fun updateFlat(flat: Flat): Result<Unit> = safeFirestoreCall {
        flatsRef().document(flat.id.toString())
            .set(flat, SetOptions.merge()).await()
    }

    override suspend fun deleteFlat(flatId: Int): Result<Unit> = safeFirestoreCall {
        flatsRef().document(flatId.toString()).delete().await()
    }
}


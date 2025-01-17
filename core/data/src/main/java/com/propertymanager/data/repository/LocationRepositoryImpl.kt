package com.propertymanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

class LocationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
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

    private fun generatePositiveId(): Int {
        return abs(UUID.randomUUID().hashCode())
    }

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
        val id = generatePositiveId()
        val countryWithId = country.copy(id = id)
        countriesRef().document(id.toString()).set(countryWithId).await()
    }

    override suspend fun updateCountry(country: Country): Result<Unit> = safeFirestoreCall {
        val docRef = countriesRef().document(country.id.toString())

        // First verify the document exists
        val exists = docRef.get().await().exists()
        if (!exists) {
            return@safeFirestoreCall
        }

        docRef.set(country).await()
    }

    override suspend fun deleteCountry(countryId: Int): Result<Unit> = safeFirestoreCall {
        val operations = mutableListOf<suspend (WriteBatch) -> Unit>()

        // Add country deletion
        operations.add { batch ->
            batch.delete(countriesRef().document(countryId.toString()))
        }

        // Get all states
        val states = statesRef()
            .whereEqualTo("countryId", countryId)
            .get()
            .await()
            .documents

        // Add state deletions
        states.forEach { stateDoc ->
            val stateId = stateDoc.id.toInt()
            operations.add { batch ->
                batch.delete(stateDoc.reference)
            }

            // Get cities for this state
            val cities = citiesRef()
                .whereEqualTo("stateId", stateId)
                .get()
                .await()
                .documents

            cities.forEach { cityDoc ->
                val cityId = cityDoc.id.toInt()
                operations.add { batch ->
                    batch.delete(cityDoc.reference)
                }

                // Get societies for this city
                val societies = societiesRef()
                    .whereEqualTo("cityId", cityId)
                    .get()
                    .await()
                    .documents

                societies.forEach { societyDoc ->
                    val societyId = societyDoc.id.toInt()
                    operations.add { batch ->
                        batch.delete(societyDoc.reference)
                    }

                    // Add block deletions
                    val blocks = blocksRef()
                        .whereEqualTo("societyId", societyId)
                        .get()
                .await()
                        .documents

                    blocks.forEach { blockDoc ->
                        operations.add { batch ->
                            batch.delete(blockDoc.reference)
                        }
                    }

                    // Add tower deletions
                    val towers = towersRef()
                        .whereEqualTo("societyId", societyId)
                        .get()
                .await()
                        .documents

                    towers.forEach { towerDoc ->
                        operations.add { batch ->
                            batch.delete(towerDoc.reference)
                        }
                    }

                    // Add flat deletions
                    val flats = flatsRef()
                        .whereEqualTo("societyId", societyId)
            .get()
                        .await()
                        .documents

                    flats.forEach { flatDoc ->
                        operations.add { batch ->
                            batch.delete(flatDoc.reference)
                        }
                    }
                }
            }
        }

        // Execute operations in chunks of 500
        val chunks = operations.chunked(500)
        chunks.forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { operation ->
                operation(batch)
            }
            batch.commit().await()
        }
    }


    // State operations
    override suspend fun getStatesForCountry(countryId: Int): Flow<List<State>> = callbackFlow {
        println("DEBUG Repository: Starting to fetch states for country $countryId")

        try {
            val subscription = statesRef()
                .whereEqualTo("countryId", countryId)
                // Temporarily remove orderBy until index is ready
                // .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                        println("DEBUG Repository: Error in snapshot listener - ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                    println("DEBUG Repository: Snapshot received - documents count: ${snapshot?.documents?.size}")

                    val states = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            println("DEBUG Repository: Processing document ${doc.id}")
                            val state = doc.toObject<State>()
                            println("DEBUG Repository: Converted to State object - ${state?.name}")
                            state
        } catch (e: Exception) {
                            println("DEBUG Repository: Error converting document - ${e.message}")
                            null
                        }
                    }?.sortedBy { it.name } ?: emptyList() // Sort in memory instead

                    println("DEBUG Repository: Final states list size: ${states.size}")
                    states.forEach { state ->
                        println("DEBUG Repository: State - ${state.name} (ID: ${state.id}, CountryID: ${state.countryId})")
                    }

                trySend(states)
                }

            awaitClose {
                println("DEBUG Repository: Closing states subscription")
                subscription.remove()
            }
        } catch (e: Exception) {
            println("DEBUG Repository: Error in getStatesForCountry - ${e.message}")
            println("DEBUG Repository: Stack trace - ${e.stackTraceToString()}")
            close(e)
        }
    }

    override suspend fun findStateById(stateId: Int): Result<State?> = safeFirestoreCall {
        statesRef().document(stateId.toString()).get().await().toObject<State>()
    }

    override suspend fun addState(state: State): Result<Unit> = safeFirestoreCall {
        val id = generatePositiveId()
        val stateWithId = state.copy(id = id)
        statesRef().document(id.toString()).set(stateWithId).await()
    }

    override suspend fun updateState(state: State): Result<Unit> = safeFirestoreCall {
        val stateRef = statesRef().document(state.id.toString())

        // First verify the document exists
        val exists = stateRef.get().await().exists()
        if (!exists) {
            return@safeFirestoreCall
        }

        stateRef.set(state).await()
    }

    override suspend fun deleteState(stateId: Int): Result<Unit> = safeFirestoreCall {
        val operations = mutableListOf<suspend (WriteBatch) -> Unit>()

        // Add state deletion
        operations.add { batch ->
            batch.delete(statesRef().document(stateId.toString()))
        }

        // Get cities for this state
        val cities = citiesRef()
            .whereEqualTo("stateId", stateId)
            .get()
                .await()
            .documents

        cities.forEach { cityDoc ->
            val cityId = cityDoc.id.toInt()
            operations.add { batch ->
                batch.delete(cityDoc.reference)
            }

            // Get societies for this city
            val societies = societiesRef()
                .whereEqualTo("cityId", cityId)
                .get()
                .await()
                .documents

            societies.forEach { societyDoc ->
                val societyId = societyDoc.id.toInt()
                operations.add { batch ->
                    batch.delete(societyDoc.reference)
                }

                // Add block deletions
                val blocks = blocksRef()
                    .whereEqualTo("societyId", societyId)
                    .get()
                .await()
                    .documents

                blocks.forEach { blockDoc ->
                    operations.add { batch ->
                        batch.delete(blockDoc.reference)
                    }
                }

                // Add tower deletions
                val towers = towersRef()
                    .whereEqualTo("societyId", societyId)
                    .get()
                    .await()
                    .documents

                towers.forEach { towerDoc ->
                    operations.add { batch ->
                        batch.delete(towerDoc.reference)
                    }
                }

                // Add flat deletions
                val flats = flatsRef()
                    .whereEqualTo("societyId", societyId)
            .get()
                    .await()
                    .documents

                flats.forEach { flatDoc ->
                    operations.add { batch ->
                        batch.delete(flatDoc.reference)
                    }
                }
            }
        }

        // Execute operations in chunks of 500
        val chunks = operations.chunked(500)
        chunks.forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { operation ->
                operation(batch)
            }
            batch.commit().await()
        }
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
        val id = generatePositiveId()
        val cityWithId = city.copy(id = id)
        citiesRef().document(id.toString()).set(cityWithId).await()
    }

    override suspend fun updateCity(city: City): Result<Unit> = safeFirestoreCall {
        val cityRef = citiesRef().document(city.id.toString())

        // First verify the document exists
        val exists = cityRef.get().await().exists()
        if (!exists) {
            return@safeFirestoreCall
        }

        cityRef.set(cityRef).await()
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
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.NOT_FOUND ->
                    Result.failure(NoSuchElementException(e.message))
                FirebaseFirestoreException.Code.ALREADY_EXISTS ->
                    Result.failure(IllegalStateException("Document already exists"))
                FirebaseFirestoreException.Code.FAILED_PRECONDITION ->
                    Result.failure(IllegalStateException("Operation failed: ${e.message}"))
                else -> Result.failure(e)
            }
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
        val id = generatePositiveId()
        val societyWithId = society.copy(id = id)
        societiesRef().document(id.toString()).set(societyWithId).await()
    }

    override suspend fun updateSociety(society: Society): Result<Unit> = safeFirestoreCall {
        val societyRef = societiesRef().document(society.id.toString())
        val exists = societyRef.get().await().exists()
        if (!exists) {
            return@safeFirestoreCall
        }
        societyRef.set(society).await()
    }

    override suspend fun deleteSociety(societyId: Int): Result<Unit> = safeFirestoreCall {
        val operations = mutableListOf<suspend (WriteBatch) -> Unit>()

        // Add society deletion
        operations.add { batch ->
            batch.delete(societiesRef().document(societyId.toString()))
        }

        // Get blocks for this society
        val blocks = blocksRef()
            .whereEqualTo("societyId", societyId)
            .get()
            .await()
            .documents

        blocks.forEach { blockDoc ->
            val blockId = blockDoc.id.toInt()
            operations.add { batch ->
                batch.delete(blockDoc.reference)
            }

            // Get flats for this block
            val blockFlats = flatsRef()
                .whereEqualTo("blockId", blockId)
                .get()
                .await()
                .documents

            blockFlats.forEach { flatDoc ->
                operations.add { batch ->
                    batch.delete(flatDoc.reference)
                }
            }
        }

        // Get towers for this society
        val towers = towersRef()
            .whereEqualTo("societyId", societyId)
            .get()
            .await()
            .documents

        towers.forEach { towerDoc ->
            val towerId = towerDoc.id.toInt()
            operations.add { batch ->
                batch.delete(towerDoc.reference)
            }

            // Get flats for this tower
            val towerFlats = flatsRef()
                .whereEqualTo("towerId", towerId)
                .get()
                .await()
                .documents

            towerFlats.forEach { flatDoc ->
                operations.add { batch ->
                    batch.delete(flatDoc.reference)
                }
            }
        }

        // Get direct flats for this society
        val societyFlats = flatsRef()
            .whereEqualTo("societyId", societyId)
            .whereEqualTo("blockId", null)
            .whereEqualTo("towerId", null)
            .get()
                .await()
            .documents

        societyFlats.forEach { flatDoc ->
            operations.add { batch ->
                batch.delete(flatDoc.reference)
            }
        }

        // Execute operations in chunks of 500
        val chunks = operations.chunked(500)
        chunks.forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { operation ->
                operation(batch)
            }
            batch.commit().await()
        }
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
        val id = generatePositiveId()
        val blockWithId = block.copy(id = id)
        blocksRef().document(id.toString()).set(blockWithId).await()
    }

    override suspend fun updateBlock(block: Block): Result<Unit> = safeFirestoreCall {
        val blockRef = blocksRef().document(block.id.toString())
        val exists = blockRef.get().await().exists()
        if (!exists) {
            return@safeFirestoreCall
        }
        blockRef.set(block).await()
    }

    override suspend fun deleteBlock(blockId: Int): Result<Unit> = safeFirestoreCall {
        val operations = mutableListOf<suspend (WriteBatch) -> Unit>()

        // Add block deletion
        operations.add { batch ->
            batch.delete(blocksRef().document(blockId.toString()))
        }

        // Get flats for this block
        val blockFlats = flatsRef()
            .whereEqualTo("blockId", blockId)
            .get()
            .await()
            .documents

        blockFlats.forEach { flatDoc ->
            operations.add { batch ->
                batch.delete(flatDoc.reference)
            }
        }

        // Execute operations in chunks of 500
        val chunks = operations.chunked(500)
        chunks.forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { operation ->
                operation(batch)
            }
            batch.commit().await()
        }
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
        val id = generatePositiveId()
        val towerWithId = tower.copy(id = id)
        towersRef().document(id.toString()).set(towerWithId).await()

    }

    override suspend fun updateTower(tower: Tower): Result<Unit> = safeFirestoreCall {
        towersRef()
            .document(tower.id.toString())
            .set(tower)
                .await()
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
        val id = generatePositiveId()
        val flatWithId = flat.copy(id = id)
        flatsRef().document(id.toString()).set(flatWithId).await()

    }

    override suspend fun updateFlat(flat: Flat): Result<Unit> = safeFirestoreCall {
        flatsRef()
            .document(flat.id.toString())
            .set(flat)
                .await()
    }

    override suspend fun deleteFlat(flatId: Int): Result<Unit> = safeFirestoreCall {
        flatsRef().document(flatId.toString()).delete().await()
    }

    private suspend fun executeDeleteBatch(batch: WriteBatch) {
        try {
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                // Handle batch size limit exceeded
                throw FirebaseFirestoreException(
                    "Batch operation too large. Please try deleting in smaller chunks.",
                    FirebaseFirestoreException.Code.FAILED_PRECONDITION
                )
            }
            throw e
        }

    }

}

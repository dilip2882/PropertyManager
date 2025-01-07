package com.propertymanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

class LocationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LocationRepository {

    private val locationsRef = firestore.collection("locations")

    // subcollections
    private fun countriesRef() = locationsRef.document("root").collection("countries")
    private fun statesRef(countryId: Int) = countriesRef().document(countryId.toString()).collection("states")
    private fun citiesRef(countryId: Int, stateId: Int) =
        statesRef(countryId).document(stateId.toString()).collection("cities")
    private fun societiesRef(countryId: Int, stateId: Int, cityId: Int) =
        citiesRef(countryId, stateId).document(cityId.toString()).collection("societies")
    private fun blocksRef(countryId: Int, stateId: Int, cityId: Int, societyId: Int) =
        societiesRef(countryId, stateId, cityId).document(societyId.toString()).collection("blocks")
    private fun towersRef(countryId: Int, stateId: Int, cityId: Int, societyId: Int) =
        societiesRef(countryId, stateId, cityId).document(societyId.toString()).collection("towers")
    private fun flatsRef(countryId: Int, stateId: Int, cityId: Int, societyId: Int, parentType: String, parentId: Int) =
        when (parentType) {
            "block" -> blocksRef(countryId, stateId, cityId, societyId).document(parentId.toString()).collection("flats")
            "tower" -> towersRef(countryId, stateId, cityId, societyId).document(parentId.toString()).collection("flats")
            else -> societiesRef(countryId, stateId, cityId).document(societyId.toString()).collection("flats")
        }

    // Country operations
    override suspend fun getCountries(): Flow<List<Country>> = callbackFlow {
        val subscription = countriesRef()
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

    override suspend fun findCountryById(countryId: Int): Result<Country?> = withContext(Dispatchers.IO) {
        try {
            val document = countriesRef().document(countryId.toString()).get().await()
            Result.success(document.toObject<Country>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCountry(country: Country): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            countriesRef().document(country.id.toString()).set(country).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCountry(country: Country): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            countriesRef().document(country.id.toString()).set(country, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCountry(countryId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            countriesRef().document(countryId.toString()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // State operations
    override suspend fun getStatesForCountry(countryId: Int): Flow<List<State>> = callbackFlow {
        val subscription = statesRef(countryId)
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

    override suspend fun findStateById(stateId: Int): Result<State?> = withContext(Dispatchers.IO) {
        try {
            // First find the country that contains this state
            val querySnapshot = countriesRef().get().await()
            for (countryDoc in querySnapshot.documents) {
                val stateDoc = statesRef(countryDoc.id.toInt()).document(stateId.toString()).get().await()
                if (stateDoc.exists()) {
                    return@withContext Result.success(stateDoc.toObject<State>())
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addState(state: State): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            statesRef(state.countryId).document(state.id.toString()).set(state).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateState(state: State): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            statesRef(state.countryId).document(state.id.toString())
                .set(state, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteState(stateId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val state = findStateById(stateId).getOrNull() ?:
                return@withContext Result.failure(Exception("State not found"))
            statesRef(state.countryId).document(stateId.toString()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // City operations
    override suspend fun getCitiesForState(stateId: Int): Flow<List<City>> = callbackFlow {
        val state = findStateById(stateId).getOrNull() ?: return@callbackFlow

        val subscription = citiesRef(state.countryId, stateId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val cities = snapshot?.documents?.mapNotNull {
                    it.toObject<City>()
                } ?: emptyList()
                trySend(cities)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findCityById(cityId: Int): Result<City?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = countriesRef().get().await()
            for (countryDoc in querySnapshot.documents) {
                val countryId = countryDoc.id.toInt()
                val statesSnapshot = statesRef(countryId).get().await()
                for (stateDoc in statesSnapshot.documents) {
                    val cityDoc = citiesRef(countryId, stateDoc.id.toInt())
                        .document(cityId.toString()).get().await()
                    if (cityDoc.exists()) {
                        return@withContext Result.success(cityDoc.toObject<City>())
                    }
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCity(city: City): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            citiesRef(city.countryId, city.stateId)
                .document(city.id.toString())
                .set(city)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCity(city: City): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            citiesRef(city.countryId, city.stateId)
                .document(city.id.toString())
                .set(city, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCity(cityId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val city = findCityById(cityId).getOrNull() ?:
                return@withContext Result.failure(Exception("City not found"))
            citiesRef(city.countryId, city.stateId)
                .document(cityId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Society operations
    override suspend fun getSocietiesForCity(cityId: Int): Flow<List<Society>> = callbackFlow {
        val city = findCityById(cityId).getOrNull() ?: return@callbackFlow

        val subscription = societiesRef(city.countryId, city.stateId, cityId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val societies = snapshot?.documents?.mapNotNull {
                    it.toObject<Society>()
                } ?: emptyList()
                trySend(societies)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findSocietyById(societyId: Int): Result<Society?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = countriesRef().get().await()
            for (countryDoc in querySnapshot.documents) {
                val countryId = countryDoc.id.toInt()
                val statesSnapshot = statesRef(countryId).get().await()
                for (stateDoc in statesSnapshot.documents) {
                    val stateId = stateDoc.id.toInt()
                    val citiesSnapshot = citiesRef(countryId, stateId).get().await()
                    for (cityDoc in citiesSnapshot.documents) {
                        val societyDoc = societiesRef(countryId, stateId, cityDoc.id.toInt())
                            .document(societyId.toString()).get().await()
                        if (societyDoc.exists()) {
                            return@withContext Result.success(societyDoc.toObject<Society>())
                        }
                    }
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addSociety(society: Society): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            societiesRef(society.countryId, society.stateId, society.cityId)
                .document(society.id.toString())
                .set(society)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSociety(society: Society): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            societiesRef(society.countryId, society.stateId, society.cityId)
                .document(society.id.toString())
                .set(society, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSociety(societyId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val society = findSocietyById(societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))
            societiesRef(society.countryId, society.stateId, society.cityId)
                .document(societyId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Block operations
    override suspend fun getBlocksForSociety(societyId: Int): Flow<List<Block>> = callbackFlow {
        val society = findSocietyById(societyId).getOrNull() ?: return@callbackFlow

        val subscription = blocksRef(society.countryId, society.stateId, society.cityId, societyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val blocks = snapshot?.documents?.mapNotNull {
                    it.toObject<Block>()
                } ?: emptyList()
                trySend(blocks)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findBlockById(blockId: Int): Result<Block?> = withContext(Dispatchers.IO) {
        try {
            val societies = findAllSocieties()
            for (society in societies) {
                val blockDoc = blocksRef(society.countryId, society.stateId, society.cityId, society.id)
                    .document(blockId.toString()).get().await()
                if (blockDoc.exists()) {
                    return@withContext Result.success(blockDoc.toObject<Block>())
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addBlock(block: Block): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val society = findSocietyById(block.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))
            blocksRef(society.countryId, society.stateId, society.cityId, society.id)
                .document(block.id.toString())
                .set(block)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBlock(block: Block): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val society = findSocietyById(block.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))
            blocksRef(society.countryId, society.stateId, society.cityId, society.id)
                .document(block.id.toString())
                .set(block, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBlock(blockId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val block = findBlockById(blockId).getOrNull() ?:
                return@withContext Result.failure(Exception("Block not found"))
            val society = findSocietyById(block.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))

            blocksRef(society.countryId, society.stateId, society.cityId, society.id)
                .document(blockId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Tower operations
    override suspend fun getTowersForSociety(societyId: Int): Flow<List<Tower>> = callbackFlow {
        val society = findSocietyById(societyId).getOrNull() ?: return@callbackFlow

        val subscription = towersRef(society.countryId, society.stateId, society.cityId, societyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val towers = snapshot?.documents?.mapNotNull {
                    it.toObject<Tower>()
                } ?: emptyList()
                trySend(towers)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getTowersForBlock(blockId: Int): Flow<List<Tower>> = callbackFlow {
        val block = findBlockById(blockId).getOrNull() ?: return@callbackFlow
        val society = findSocietyById(block.societyId).getOrNull() ?: return@callbackFlow

        val subscription = towersRef(society.countryId, society.stateId, society.cityId, society.id)
            .whereEqualTo("blockId", blockId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val towers = snapshot?.documents?.mapNotNull {
                    it.toObject<Tower>()
                } ?: emptyList()
                trySend(towers)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findTowerById(towerId: Int): Result<Tower?> = withContext(Dispatchers.IO) {
        try {
            val societies = findAllSocieties()
            for (society in societies) {
                val towerDoc = towersRef(society.countryId, society.stateId, society.cityId, society.id)
                    .document(towerId.toString()).get().await()
                if (towerDoc.exists()) {
                    return@withContext Result.success(towerDoc.toObject<Tower>())
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTower(tower: Tower): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val society = findSocietyById(tower.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))
            towersRef(society.countryId, society.stateId, society.cityId, society.id)
                .document(tower.id.toString())
                .set(tower)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTower(tower: Tower): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val society = findSocietyById(tower.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))
            towersRef(society.countryId, society.stateId, society.cityId, society.id)
                .document(tower.id.toString())
                .set(tower, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTower(towerId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val tower = findTowerById(towerId).getOrNull() ?:
                return@withContext Result.failure(Exception("Tower not found"))
            val society = findSocietyById(tower.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))

            towersRef(society.countryId, society.stateId, society.cityId, society.id)
                .document(towerId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Flat operations
    override suspend fun getFlatsForSociety(societyId: Int): Flow<List<Flat>> = callbackFlow {
        val society = findSocietyById(societyId).getOrNull() ?: return@callbackFlow

        val subscription = flatsRef(society.countryId, society.stateId, society.cityId, society.id, "society", society.id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val flats = snapshot?.documents?.mapNotNull {
                    it.toObject<Flat>()
                } ?: emptyList()
                trySend(flats)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getFlatsForBlock(blockId: Int): Flow<List<Flat>> = callbackFlow {
        val block = findBlockById(blockId).getOrNull() ?: return@callbackFlow
        val society = findSocietyById(block.societyId).getOrNull() ?: return@callbackFlow

        val subscription = flatsRef(society.countryId, society.stateId, society.cityId, society.id, "block", blockId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val flats = snapshot?.documents?.mapNotNull {
                    it.toObject<Flat>()
                } ?: emptyList()
                trySend(flats)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getFlatsForTower(towerId: Int): Flow<List<Flat>> = callbackFlow {
        val tower = findTowerById(towerId).getOrNull() ?: return@callbackFlow
        val society = findSocietyById(tower.societyId).getOrNull() ?: return@callbackFlow

        val subscription = flatsRef(society.countryId, society.stateId, society.cityId, society.id, "tower", towerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val flats = snapshot?.documents?.mapNotNull {
                    it.toObject<Flat>()
                } ?: emptyList()
                trySend(flats)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findFlatById(flatId: Int): Result<Flat?> = withContext(Dispatchers.IO) {
        try {
            val societies = findAllSocieties()
            for (society in societies) {
                // Check society flats
                val societyFlatDoc = flatsRef(society.countryId, society.stateId, society.cityId, society.id, "society", society.id)
                    .document(flatId.toString()).get().await()
                if (societyFlatDoc.exists()) {
                    return@withContext Result.success(societyFlatDoc.toObject<Flat>())
                }

                // Check block flats
                val blocks = getBlocksForSociety(society.id).first()
                for (block in blocks) {
                    val blockFlatDoc = flatsRef(society.countryId, society.stateId, society.cityId, society.id, "block", block.id)
                        .document(flatId.toString()).get().await()
                    if (blockFlatDoc.exists()) {
                        return@withContext Result.success(blockFlatDoc.toObject<Flat>())
                    }
                }

                // Check tower flats
                val towers = getTowersForSociety(society.id).first()
                for (tower in towers) {
                    val towerFlatDoc = flatsRef(society.countryId, society.stateId, society.cityId, society.id, "tower", tower.id)
                        .document(flatId.toString()).get().await()
                    if (towerFlatDoc.exists()) {
                        return@withContext Result.success(towerFlatDoc.toObject<Flat>())
                    }
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addFlat(flat: Flat): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val society = findSocietyById(flat.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))

            val parentType = when {
                flat.towerId != null -> "tower"
                flat.blockId != null -> "block"
                else -> "society"
            }
            val parentId = flat.towerId ?: flat.blockId ?: flat.societyId

            flatsRef(society.countryId, society.stateId, society.cityId, society.id, parentType, parentId)
                .document(flat.id.toString())
                .set(flat)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFlat(flat: Flat): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val society = findSocietyById(flat.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))

            val parentType = when {
                flat.towerId != null -> "tower"
                flat.blockId != null -> "block"
                else -> "society"
            }
            val parentId = flat.towerId ?: flat.blockId ?: flat.societyId

            flatsRef(society.countryId, society.stateId, society.cityId, society.id, parentType, parentId)
                .document(flat.id.toString())
                .set(flat, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFlat(flatId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val flat = findFlatById(flatId).getOrNull() ?:
                return@withContext Result.failure(Exception("Flat not found"))
            val society = findSocietyById(flat.societyId).getOrNull() ?:
                return@withContext Result.failure(Exception("Society not found"))

            val parentType = when {
                flat.towerId != null -> "tower"
                flat.blockId != null -> "block"
                else -> "society"
            }
            val parentId = flat.towerId ?: flat.blockId ?: flat.societyId

            flatsRef(society.countryId, society.stateId, society.cityId, society.id, parentType, parentId)
                .document(flatId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ... Continue with remaining methods for Tower and Flat operations ...

    // Helper method to find all societies
    private suspend fun findAllSocieties(): List<Society> {
        val societies = mutableListOf<Society>()
        val countriesSnapshot = countriesRef().get().await()

        for (countryDoc in countriesSnapshot.documents) {
            val countryId = countryDoc.id.toInt()
            val statesSnapshot = statesRef(countryId).get().await()

            for (stateDoc in statesSnapshot.documents) {
                val stateId = stateDoc.id.toInt()
                val citiesSnapshot = citiesRef(countryId, stateId).get().await()

                for (cityDoc in citiesSnapshot.documents) {
                    val cityId = cityDoc.id.toInt()
                    val societiesSnapshot = societiesRef(countryId, stateId, cityId).get().await()

                    societies.addAll(societiesSnapshot.documents.mapNotNull {
                        it.toObject<Society>()
                    })
                }
            }
        }
        return societies
    }
}


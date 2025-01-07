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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
) : LocationRepository {
    private val locationsCollection = firestore.collection("locations")

    /* ----------------- * Country * ----------------- */
    override suspend fun getCountries(): Flow<List<Country>> = callbackFlow {
        val subscription = locationsCollection
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
            val document = locationsCollection.document(countryId.toString()).get().await()
            val country = document.toObject<Country>()
            Result.success(country)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCountry(country: Country): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            locationsCollection
                .document(country.id.toString())
                .set(country)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCountry(country: Country): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            locationsCollection
                .document(country.id.toString())
                .set(country, SetOptions.merge())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCountry(countryId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            locationsCollection.document(countryId.toString()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ----------------- * State * ----------------- */
    override suspend fun getStatesForCountry(countryId: Int): Flow<List<State>> = callbackFlow {
        val subscription = locationsCollection
            .document(countryId.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val country = snapshot?.toObject<Country>()
                val states = country?.states ?: emptyList()
                trySend(states)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun findStateById(stateId: Int): Result<State?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val state = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .flatMap { it.states }
                .find { it.id == stateId }
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addState(countryId: Int, state: State): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.document(countryId.toString()).get().await()
            val country =
                countryDoc.toObject<Country>() ?: return@withContext Result.failure(Exception("Country not found"))

            val updatedStates = country.states.toMutableList().apply { add(state) }
            locationsCollection.document(countryId.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateState(countryId: Int, state: State): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.document(countryId.toString()).get().await()
            val country =
                countryDoc.toObject<Country>() ?: return@withContext Result.failure(Exception("Country not found"))

            val updatedStates = country.states.map {
                if (it.id == state.id) state else it
            }
            locationsCollection.document(countryId.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteState(countryId: Int, stateId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.document(countryId.toString()).get().await()
            val country =
                countryDoc.toObject<Country>() ?: return@withContext Result.failure(Exception("Country not found"))

            val updatedStates = country.states.filter { it.id != stateId }
            locationsCollection.document(countryId.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ----------------- * City * ----------------- */
    override suspend fun getCitiesForState(stateId: Int): Flow<List<City>> = callbackFlow {
        val subscription = locationsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val cities = querySnapshot.documents
                    .mapNotNull { it.toObject<Country>() }
                    .flatMap { it.states }
                    .find { it.id == stateId }
                    ?.cities ?: emptyList()
                trySend(cities)
            }
            .addOnFailureListener { error ->
                close(error)
            }

        awaitClose { }
    }

    override suspend fun findCityById(cityId: Int): Result<City?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val city = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .flatMap { it.states }
                .flatMap { it.cities }
                .find { it.id == cityId }
            Result.success(city)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addCity(stateId: Int, city: City): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.get().await()
            val country = countryDoc.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { it.states.any { state -> state.id == stateId } }
                ?: return@withContext Result.failure(Exception("State not found"))

            val updatedStates = country.states.map { state ->
                if (state.id == stateId) {
                    state.copy(cities = state.cities + city)
                } else state
            }
            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCity(stateId: Int, city: City): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.get().await()
            val country = countryDoc.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { it.states.any { state -> state.id == stateId } }
                ?: return@withContext Result.failure(Exception("State not found"))

            val updatedStates = country.states.map { state ->
                if (state.id == stateId) {
                    state.copy(cities = state.cities.map { if (it.id == city.id) city else it })
                } else state
            }
            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCity(stateId: Int, cityId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.get().await()
            val country = countryDoc.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { it.states.any { state -> state.id == stateId } }
                ?: return@withContext Result.failure(Exception("State not found"))

            val updatedStates = country.states.map { state ->
                if (state.id == stateId) {
                    state.copy(cities = state.cities.filter { it.id != cityId })
                } else state
            }
            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ----------------- * Society * ----------------- */
    override suspend fun getSocietiesForCity(cityId: Int): Flow<List<Society>> = callbackFlow {
        val subscription = locationsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val societies = querySnapshot.documents
                    .mapNotNull { it.toObject<Country>() }
                    .flatMap { it.states }
                    .flatMap { it.cities }
                    .find { it.id == cityId }
                    ?.societies ?: emptyList()
                trySend(societies)
            }
            .addOnFailureListener { error ->
                close(error)
            }
        awaitClose { }
    }

    override suspend fun addSociety(cityId: Int, society: Society): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.get().await()
            val country = countryDoc.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { it.states.any { state -> state.cities.any { city -> city.id == cityId } } }
                ?: return@withContext Result.failure(Exception("City not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        if (city.id == cityId) city.copy(societies = city.societies + society) else city
                    },
                )
            }
            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSociety(cityId: Int, society: Society): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.get().await()
            val country = countryDoc.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { it.states.any { state -> state.cities.any { city -> city.id == cityId } } }
                ?: return@withContext Result.failure(Exception("City not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        if (city.id == cityId) city.copy(societies = city.societies.map { if (it.id == society.id) society else it }) else city
                    },
                )
            }
            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSociety(cityId: Int, societyId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.get().await()
            val country = countryDoc.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { it.states.any { state -> state.cities.any { city -> city.id == cityId } } }
                ?: return@withContext Result.failure(Exception("City not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        if (city.id == cityId) city.copy(societies = city.societies.filter { it.id != societyId }) else city
                    },
                )
            }
            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun findSocietyById(societyId: Int): Result<Society?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val society = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .flatMap { it.states }
                .flatMap { it.cities }
                .flatMap { it.societies }
                .find { it.id == societyId }
            Result.success(society)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ----------------- * Block * ----------------- */
    override suspend fun getBlocksForSociety(societyId: Int): Flow<List<Block>> = callbackFlow {
        val subscription = locationsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val blocks = querySnapshot.documents
                    .mapNotNull { it.toObject<Country>() }
                    .flatMap { it.states }
                    .flatMap { it.cities }
                    .flatMap { it.societies }
                    .find { it.id == societyId }
                    ?.blocks ?: emptyList()
                trySend(blocks)
            }
            .addOnFailureListener { error ->
                close(error)
            }
        awaitClose { }
    }

    override suspend fun findBlockById(blockId: Int): Result<Block?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val block = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .flatMap { it.states }
                .flatMap { it.cities }
                .flatMap { it.societies }
                .flatMap { it.blocks }
                .find { it.id == blockId }
            Result.success(block)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addBlock(societyId: Int, block: Block): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { it.id == societyId }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Society not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                if (society.id == societyId) {
                                    society.copy(blocks = society.blocks + block)
                                } else society
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBlock(societyId: Int, block: Block): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { it.id == societyId }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Society not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                if (society.id == societyId) {
                                    society.copy(
                                        blocks = society.blocks.map {
                                            if (it.id == block.id) block else it
                                        },
                                    )
                                } else society
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBlock(societyId: Int, blockId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { it.id == societyId }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Society not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                if (society.id == societyId) {
                                    society.copy(blocks = society.blocks.filter { it.id != blockId })
                                } else society
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ----------------- * Tower * ----------------- */
    override suspend fun getTowersForSociety(societyId: Int): Flow<List<Tower>> = callbackFlow {
        val subscription = locationsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val towers = querySnapshot.documents
                    .mapNotNull { it.toObject<Country>() }
                    .flatMap { it.states }
                    .flatMap { it.cities }
                    .flatMap { it.societies }
                    .find { it.id == societyId }
                    ?.towers ?: emptyList()
                trySend(towers)
            }
            .addOnFailureListener { error ->
                close(error)
            }
        awaitClose { }
    }

    override suspend fun findTowerById(towerId: Int): Result<Tower?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val tower = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .flatMap { it.states }
                .flatMap { it.cities }
                .flatMap { it.societies }
                .flatMap { it.towers }
                .find { it.id == towerId }
            Result.success(tower)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTower(blockId: Int, tower: Tower): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { society ->
                                society.blocks.any { it.id == blockId }
                            }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Block not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                if (society.blocks.any { it.id == blockId }) {
                                    society.copy(towers = society.towers + tower)
                                } else society
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTower(blockId: Int, tower: Tower): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { society ->
                                society.blocks.any { it.id == blockId }
                            }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Block not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                if (society.blocks.any { it.id == blockId }) {
                                    society.copy(
                                        towers = society.towers.map {
                                            if (it.id == tower.id) tower else it
                                        },
                                    )
                                } else society
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTower(blockId: Int, towerId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { society ->
                                society.blocks.any { it.id == blockId }
                            }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Block not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                if (society.blocks.any { it.id == blockId }) {
                                    society.copy(towers = society.towers.filter { it.id != towerId })
                                } else society
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ----------------- * Flat * ----------------- */
    override suspend fun getFlatsForSociety(societyId: Int): Flow<List<Flat>> = callbackFlow {
        val subscription = locationsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val flats = querySnapshot.documents
                    .mapNotNull { it.toObject<Country>() }
                    .flatMap { it.states }
                    .flatMap { it.cities }
                    .flatMap { it.societies }
                    .find { it.id == societyId }
                    ?.flats ?: emptyList()
                trySend(flats)
            }
            .addOnFailureListener { error ->
                close(error)
            }
        awaitClose { }
    }

    override suspend fun getFlatsForBlock(blockId: Int): Flow<List<Flat>> = callbackFlow {
        val subscription = locationsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val flats = querySnapshot.documents
                    .mapNotNull { it.toObject<Country>() }
                    .flatMap { it.states }
                    .flatMap { it.cities }
                    .flatMap { it.societies }
                    .flatMap { it.blocks }
                    .find { it.id == blockId }
                    ?.flats ?: emptyList()
                trySend(flats)
            }
            .addOnFailureListener { error ->
                close(error)
            }
        awaitClose { }
    }

    override suspend fun getFlatsForTower(towerId: Int): Flow<List<Flat>> = callbackFlow {
        val subscription = locationsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val flats = querySnapshot.documents
                    .mapNotNull { it.toObject<Country>() }
                    .flatMap { it.states }
                    .flatMap { it.cities }
                    .flatMap { it.societies }
                    .flatMap { it.towers }
                    .find { it.id == towerId }
                    ?.flats ?: emptyList()
                trySend(flats)
            }
            .addOnFailureListener { error ->
                close(error)
            }
        awaitClose { }
    }

    override suspend fun findFlatById(flatId: Int): Result<Flat?> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val flat = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .flatMap { it.states }
                .flatMap { it.cities }
                .flatMap { it.societies }
                .flatMap { society ->
                    society.blocks.flatMap { it.flats } +
                        society.towers.flatMap { it.flats } +
                        society.flats
                }
                .find { it.id == flatId }
            Result.success(flat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addFlat(towerId: Int, flat: Flat): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { society ->
                                society.towers.any { it.id == towerId }
                            }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Tower not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                society.copy(
                                    towers = society.towers.map { tower ->
                                        if (tower.id == towerId) {
                                            tower.copy(flats = tower.flats + flat)
                                        } else tower
                                    },
                                )
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFlat(towerId: Int, flat: Flat): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { society ->
                                society.towers.any { it.id == towerId }
                            }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Tower not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                society.copy(
                                    towers = society.towers.map { tower ->
                                        if (tower.id == towerId) {
                                            tower.copy(
                                                flats = tower.flats.map {
                                                    if (it.id == flat.id) flat else it
                                                },
                                            )
                                        } else tower
                                    },
                                )
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFlat(towerId: Int, flatId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = locationsCollection.get().await()
            val country = querySnapshot.documents
                .mapNotNull { it.toObject<Country>() }
                .firstOrNull { country ->
                    country.states.any { state ->
                        state.cities.any { city ->
                            city.societies.any { society ->
                                society.towers.any { it.id == towerId }
                            }
                        }
                    }
                } ?: return@withContext Result.failure(Exception("Tower not found"))

            val updatedStates = country.states.map { state ->
                state.copy(
                    cities = state.cities.map { city ->
                        city.copy(
                            societies = city.societies.map { society ->
                                society.copy(
                                    towers = society.towers.map { tower ->
                                        if (tower.id == towerId) {
                                            tower.copy(flats = tower.flats.filter { it.id != flatId })
                                        } else tower
                                    },
                                )
                            },
                        )
                    },
                )
            }

            locationsCollection.document(country.id.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


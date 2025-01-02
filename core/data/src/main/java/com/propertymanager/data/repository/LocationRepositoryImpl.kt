package com.propertymanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LocationRepository {
    private val locationsCollection = firestore.collection("locations")

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

        awaitClose {  }
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

    override suspend fun addState(countryId: Int, state: State): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val countryDoc = locationsCollection.document(countryId.toString()).get().await()
            val country = countryDoc.toObject<Country>() ?: return@withContext Result.failure(Exception("Country not found"))

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
            val country = countryDoc.toObject<Country>() ?: return@withContext Result.failure(Exception("Country not found"))

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
            val country = countryDoc.toObject<Country>() ?: return@withContext Result.failure(Exception("Country not found"))

            val updatedStates = country.states.filter { it.id != stateId }
            locationsCollection.document(countryId.toString())
                .update("states", updatedStates)
                .await()

            Result.success(Unit)
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
}

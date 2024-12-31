package com.propertymanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.repository.PropertyRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PropertyRepository {
    private val propertyCollection = firestore.collection("properties")

    override suspend fun getProperties(): List<Property> {
        val snapshot = propertyCollection.get().await()
        return snapshot.documents.mapNotNull { document ->
            document.toObject(Property::class.java)?.copy(id = document.id)
        }
    }

    override suspend fun addProperty(property: Property): String {
        val docRef = propertyCollection.add(property).await()
        return docRef.id
    }

    override suspend fun updateProperty(property: Property) {
        propertyCollection.document(property.id).set(property).await()
    }

    override suspend fun deleteProperty(propertyId: String) {
        propertyCollection.document(propertyId).delete().await()
    }

    override suspend fun getPropertyById(propertyId: String): Property? {
        val snapshot = propertyCollection.document(propertyId).get().await()
        return snapshot.toObject(Property::class.java)?.copy(id = snapshot.id)
    }

    override suspend fun addAddress(propertyId: String, address: Property.Address) {
        val document = propertyCollection.document(propertyId)
        val snapshot = document.get().await()
        val property = snapshot.toObject(Property::class.java)

        property?.let {
            val updatedProperty = it.copy(address = address)
            document.set(updatedProperty).await()
        }
    }

    override suspend fun deleteAddress(propertyId: String) {
        val document = propertyCollection.document(propertyId)
        val snapshot = document.get().await()
        val property = snapshot.toObject(Property::class.java)

        property?.let {
            val clearedAddress = Property.Address()
            val updatedProperty = it.copy(address = clearedAddress)
            document.set(updatedProperty).await()
        }
    }

    override suspend fun updateAddress(propertyId: String, address: Property.Address) {
        val document = propertyCollection.document(propertyId)
        val snapshot = document.get().await()
        val property = snapshot.toObject(Property::class.java)

        property?.let {
            val updatedProperty = it.copy(address = address)
            document.set(updatedProperty).await()
        }
    }

}

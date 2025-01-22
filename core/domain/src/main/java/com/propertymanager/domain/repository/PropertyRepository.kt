package com.propertymanager.domain.repository

import com.propertymanager.domain.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(): Flow<List<Property>>
    suspend fun addProperty(property: Property): String
    suspend fun updateProperty(property: Property)
    suspend fun deleteProperty(propertyId: String)
    suspend fun getPropertyById(propertyId: String): Property?

    suspend fun addAddress(propertyId: String, address: Property.Address)
    suspend fun deleteAddress(propertyId: String)
    suspend fun updateAddress(propertyId: String, address: Property.Address)
}

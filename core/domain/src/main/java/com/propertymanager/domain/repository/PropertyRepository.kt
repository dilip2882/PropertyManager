package com.propertymanager.domain.repository

import com.propertymanager.domain.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    suspend fun getProperties(): List<Property>
    suspend fun addProperty(property: Property): String
    suspend fun updateProperty(property: Property)
    suspend fun deleteProperty(propertyId: String)
    suspend fun getPropertyById(propertyId: String): Property?
}

package com.propertymanager.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class User(
    val userId: String? = null, // based on role - can be manager, tenant, landlord
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val username: String,
    var imageUrl: String? = null,
    var bio: String = "",
    var url: String = "",
    val passwordHash: String? = null,
    val role: String = Role.TENANT.name,
    val token: List<String> = listOf(),
    val address: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val associatedProperties: List<String> = emptyList(), // List of property IDs
    val selectedPropertyId: String? = null, // Currently selected property ID
    val createdAt: Timestamp? = Timestamp.now(),
    val updatedAt: Timestamp? = Timestamp.now(),
    var profileImage: String? = null,
    val bannerImage: String? = null,
) {
    // No-argument constructor for Firestore
    constructor() : this(
        userId = "",
        name = "",
        email = "",
        phone = "",
        role = Role.TENANT.name,
        associatedProperties = emptyList(),
        selectedPropertyId = null,
        username = "",
        imageUrl = null,
        bio = "",
        url = "",
        passwordHash = "",
        token = emptyList(),
        address = "",
        location = GeoPoint(0.0, 0.0),
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now(),
        profileImage = null,
        bannerImage = null,
    )
}

enum class Role {
    LANDLORD, TENANT, MANAGER
}

package com.propertymanager.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class User(
    @DocumentId val userId: String? = null, // based on role - can be manager, tenant, landlord
    val name: String = "",
    var username: String = "",
    var imageUrl: String = "",
    var bio: String = "",
    var url: String = "",
    val phone: String = "",
    val email: String = "",
    val passwordHash: String? = null,
    val role: Role = Role.TENANT,
    val address: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val associatedProperties: List<String> = emptyList(),
    val createdAt: Timestamp? = Timestamp.now(),
    val updatedAt: Timestamp? = Timestamp.now(),
    var profileImage: String? = null
) {
    // No-argument constructor for Firestore
    constructor() : this(
        userId = "",
        name = "",
        username = "",
        imageUrl = "",
        bio = "",
        url = "",
        phone = "",
        email = "",
        passwordHash = "",
        role = Role.TENANT,
        address = "",
        location = GeoPoint(0.0, 0.0),
        associatedProperties = emptyList(),
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now(),
        profileImage = null
    )
}

enum class Role {
    TENANT, LANDLORD, MANAGER
}

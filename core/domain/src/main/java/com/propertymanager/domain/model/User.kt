package com.propertymanager.domain.model

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class User(
    val userId: String = "",
    val name: String = "",
    var username: String = "",
    var imageUrl: String = "",
    var bio: String = "",
    var url: String = "",
    val phone: String = "",
    val email: String = "",
    val role: Role = Role.TENANT,
    val address: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val properties: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
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
        role = Role.TENANT,
        address = "",
        location = GeoPoint(0.0, 0.0),
        properties = emptyList(),
        createdAt = Date(),
        updatedAt = Date(),
        profileImage = null
    )
}

enum class Role {
    TENANT, LANDLORD, MANAGER
}

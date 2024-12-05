package com.propertymanager.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class User(
    val userid: String = "",
    var name: String = "",
    var username: String = "",
    var imageUrl: String = "",
    var bio: String = "",
    var url: String = "",
    var following: List<String> = emptyList(),
    var followers: List<String> = emptyList(),
    var totalPosts: String = "",
    var email: String = "",
    var phone: String = "",
    var role: Role = Role.MANAGER,
    var address: String = "",
    var location: GeoPoint? = null,
    var properties: List<String> = emptyList(),
    var createdAt: Timestamp = Timestamp.now(),
    var updatedAt: Timestamp = Timestamp.now(),
    var profileImage: String? = null
)

enum class Role(val roleName: String) {
    TENANT("tenant"),
    LANDLORD("landlord"),
    MANAGER("manager");

    companion object {
        fun fromString(value: String): Role? {
            return entries.find { it.roleName == value }
        }
    }
}
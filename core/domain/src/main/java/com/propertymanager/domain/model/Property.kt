package com.propertymanager.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

data class Property(
    @DocumentId val id: String = "",
    val address: Address = Address(),
    val ownerId: String = "",
    val currentTenantId: String = "",
    val maintenanceRequests: List<String> = emptyList(),
    val status: PropertyStatus = PropertyStatus.PENDING_APPROVAL,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {
    constructor() : this(
        id = "",
        address = Address(),
        ownerId = "",
        currentTenantId = "",
        maintenanceRequests = emptyList(),
        status = PropertyStatus.PENDING_APPROVAL,
        createdAt = Timestamp.now(),
        updatedAt = Timestamp.now()
    )

    // property address
    data class Address(
        val country: String = "",
        val state: String = "",
        val city: String = "",
        val society: String = "",
        val building: Building = Building.FLAT,
        val flatNo: String = "",
    )

    @Serializable
    enum class Building {
        BLOCK, TOWER, FLAT;

        companion object {
            fun fromString(value: String): Building {
                return try {
                    valueOf(value.uppercase())
                } catch (e: IllegalArgumentException) {
                    BLOCK
                }
            }
        }
    }

}

enum class PropertyStatus(val label: String) {
    ACTIVE("Active"),
    PENDING_APPROVAL("Pending Approval"),
    REJECTED("Rejected"),
    EXPIRED("Expired");

    companion object {
        fun fromString(value: String): PropertyStatus =
            entries.firstOrNull { it.label.equals(value, ignoreCase = true) } 
                ?: PENDING_APPROVAL
    }
}

fun Property.isActive(): Boolean = status == PropertyStatus.ACTIVE

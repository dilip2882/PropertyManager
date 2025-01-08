package com.propertymanager.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Property(
    @DocumentId val id: String = "",
    val address: Address = Address(),
    val ownerId: String = "",
    val currentTenantId: String = "",
    val maintenanceRequests: List<String> = emptyList(),
    val createdAt: Timestamp? = null,
) {
    constructor() : this(
        id = "",
        address = Address(),
        ownerId = "",
        currentTenantId = "",
        maintenanceRequests = emptyList(),
        createdAt = Timestamp.now(),
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

/*
object BuildingTypeConverter {
    @TypeConverter
    fun fromBuilding(building: Property.Building): String {
        return building.name
    }

    @TypeConverter
    fun toBuilding(value: String?): Property.Building {
        return if (value.isNullOrEmpty()) {
            Property.Building.FLAT
        } else {
            Property.Building.fromString(value)
        }
    }
}
*/

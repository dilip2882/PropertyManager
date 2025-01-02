package com.propertymanager.domain.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Property(
    @DocumentId val id: String = "",
    val address: Address,
    val ownerId: String = "",
    val currentTenantId: String = "",
    val maintenanceRequests: List<String> = emptyList(),
    val createdAt: Timestamp? = null
) {
    constructor() : this(
        id = "",
        address = Address(),
        ownerId = "",
        currentTenantId = "",
        maintenanceRequests = emptyList(),
        createdAt = Timestamp.now()
    )

    // property address
    data class Address(
        val country: String = "",
        val state: String = "",
        val city: String = "",
        val society: String = "",
        val building: String = "",
        val flatNo: String = ""
    )
}

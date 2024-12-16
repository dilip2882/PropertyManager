package com.propertymanager.domain.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Property(
    @DocumentId val id: String = "", // Document ID, auto-generated by Firestore
    val address: Address = Address(), // Address details for the property
    val ownerId: String = "", // Reference to `users.id` (landlord)
    val currentTenantId: String = "", // Reference to `users.id` (tenant)
    val maintenanceRequests: List<String> = emptyList(), // Array of maintenance request IDs
    val createdAt: Timestamp? = null // Timestamp of property creation
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
        val street: String = "",
        val city: String = "",
        val state: String = "",
        val zipCode: String = ""
    )
}
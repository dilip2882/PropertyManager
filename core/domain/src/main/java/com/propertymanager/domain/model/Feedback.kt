package com.propertymanager.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Feedback(
    @DocumentId val id: String = "", // Auto-generated by Firestore
    val requestId: String = "", // Reference to MaintenanceRequest.id
    val tenantId: String = "", // Reference to User.id
    val rating: Int = 0, // Rating (e.g., 1-5)
    val comments: String = "",
    val createdAt: Timestamp? = Timestamp.now() // Firestore timestamp
)
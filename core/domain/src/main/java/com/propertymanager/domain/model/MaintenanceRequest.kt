package com.propertymanager.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MaintenanceRequest(
    val maintenanceRequestsId: String? = null,
    val propertyId: String = "",
    val tenantId: String = "",
    val assignedStaffId: String = "",
    val workerDetails: WorkerDetails = WorkerDetails(),
    val issueDescription: String = "",
    val issueCategory: String = "",
    val status: String = RequestStatus.PENDING.label,
    val priority: String = PriorityLevel.LOW.label,
    @ServerTimestamp
    val createdAt: Timestamp = Timestamp.now(),
    @ServerTimestamp
    val updatedAt: Timestamp = Timestamp.now(),
    val photos: List<String> = emptyList(),
    val videos: List<String> = emptyList()
)

data class WorkerDetails(
    val name: String = "",
    val phone: String = "",
    val trade: String = ""
)

enum class RequestStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    companion object {
        fun fromString(value: String): RequestStatus =
            entries.firstOrNull { it.label.equals(value, ignoreCase = true) } ?: PENDING

        fun getAllStatuses(): List<String> = entries.map { it.label }
    }
}

enum class PriorityLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    companion object {
        fun fromString(value: String): PriorityLevel =
            entries.firstOrNull { it.label.equals(value, ignoreCase = true) } ?: LOW

        fun getAllPriorities(): List<String> = entries.map { it.label }
    }
}

enum class MediaType {
    IMAGE, VIDEO
}

fun Date?.formatDate(): String {
    return this?.let {
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(it)
    } ?: "Not available"
}

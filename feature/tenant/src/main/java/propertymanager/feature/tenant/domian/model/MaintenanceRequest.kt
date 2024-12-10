package propertymanager.feature.tenant.domian.model

data class MaintenanceRequest(
    val id: String = "",
    val propertyId: String,
    val tenantId: String,
    val issueDescription: String,
    val status: String = RequestStatus.PENDING.label, // pending, inProgress, completed
    val priority: String = PriorityLevel.LOW.label, // low, medium, high
    val photos: List<String> = emptyList(), // URLs of photos
    val videos: List<String> = emptyList(), // URLs of videos
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    constructor() : this(
        id = "",
        propertyId = "",
        tenantId = "",
        issueDescription = "",
        status = RequestStatus.PENDING.label,
        priority = PriorityLevel.LOW.label,
        photos = emptyList(),
        videos = emptyList(),
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}


enum class RequestStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    companion object {
        fun fromString(value: String): RequestStatus {
            return entries.firstOrNull { it.label.equals(value, ignoreCase = true) } ?: PENDING
        }

        fun getAllStatuses(): List<String> {
            return entries.map { it.label }
        }
    }
}

enum class PriorityLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    companion object {
        fun fromString(value: String): PriorityLevel {
            return entries.firstOrNull { it.label.equals(value, ignoreCase = true) } ?: LOW
        }

        fun getAllPriorities(): List<String> {
            return entries.map { it.label }
        }
    }
}


package propertymanager.feature.tenant.domian.model

data class MaintenanceRequest(
    val id: String = "", // auto-generated ID
    val propertyId: String, // Reference to `properties.id`
    val tenantId: String, // (tenant who raised the request)
    val assignedStaffId: String = "", // (agency staff assigned)
    val workerDetails: WorkerDetails = WorkerDetails(), // Worker assigned for the task
    val issueDescription: String, // Description of the issue
    val issueCategory: String = "", // Category of the issue
    val status: String = RequestStatus.PENDING.label, // "pending", "inProgress", "completed"
    val priority: String = PriorityLevel.LOW.label, // "low", "medium", "high"
    val createdAt: Long = System.currentTimeMillis(), // Request creation timestamp
    val updatedAt: Long = System.currentTimeMillis(), // Last status update timestamp
    val photos: List<String> = emptyList(), // URLs of photos
    val videos: List<String> = emptyList() // URLs of videos
) {
    constructor() : this(
        id = "",
        propertyId = "",
        tenantId = "",
        assignedStaffId = "",
        workerDetails = WorkerDetails(),
        issueDescription = "",
        issueCategory = "",
        status = RequestStatus.PENDING.label,
        priority = PriorityLevel.LOW.label,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        photos = emptyList(),
        videos = emptyList()
    )
}

data class WorkerDetails(
    val name: String = "", // Name of the worker
    val phone: String = "", // Phone number of the worker
    val trade: String = "" // Trade of the worker
)

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

package com.propertymanager.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.WorkerDetails
import com.propertymanager.domain.repository.StaffRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StaffRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StaffRepository {

    override suspend fun getAssignedRequests(staffId: String): Flow<Response<List<MaintenanceRequest>>> = callbackFlow {
        trySend(Response.Loading)

        val listenerRegistration = firestore.collection("maintenance_requests")
            .whereEqualTo("assignedStaffId", staffId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    trySend(Response.Error(exception.message ?: "Error loading assigned requests"))
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(MaintenanceRequest::class.java)?.copy(maintenanceRequestsId = document.id)
                }

                if (requests != null) {
                    trySend(Response.Success(requests))
                } else {
                    trySend(Response.Error("No requests found"))
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun updateRequestStatus(requestId: String, status: String): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            firestore.collection("maintenance_requests")
                .document(requestId)
                .update(mapOf(
                    "status" to status,
                    "updatedAt" to Timestamp.now()
                ))
                .await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error("Failed to update status: ${e.message}"))
        }
    }

    override suspend fun updateRequestPriority(requestId: String, priority: String): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            firestore.collection("maintenance_requests")
                .document(requestId)
                .update(mapOf(
                    "priority" to priority,
                    "updatedAt" to Timestamp.now()
                ))
                .await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error("Failed to update priority: ${e.message}"))
        }
    }

    override suspend fun assignWorker(requestId: String, workerDetails: WorkerDetails): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            firestore.collection("maintenance_requests")
                .document(requestId)
                .update(mapOf(
                    "workerDetails" to workerDetails,
                    "updatedAt" to Timestamp.now()
                ))
                .await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error("Failed to assign worker: ${e.message}"))
        }
    }

    override suspend fun updateRequestNotes(requestId: String, notes: String): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            firestore.collection("maintenance_requests")
                .document(requestId)
                .update(mapOf(
                    "notes" to notes,
                    "updatedAt" to Timestamp.now()
                ))
                .await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error("Failed to update notes: ${e.message}"))
        }
    }
}

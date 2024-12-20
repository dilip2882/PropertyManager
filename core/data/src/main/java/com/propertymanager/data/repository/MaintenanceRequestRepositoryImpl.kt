package com.propertymanager.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.repository.MaintenanceRequestRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MaintenanceRequestRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : MaintenanceRequestRepository {

    override fun getMaintenanceRequests(): Flow<Response<List<MaintenanceRequest>>> = callbackFlow {
        trySend(Response.Loading)

        val listener = firestore.collection("maintenance_requests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Response.Error(error.localizedMessage ?: "Unknown error"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val requests = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MaintenanceRequest::class.java)?.copy(
                            maintenanceRequestsId = doc.id,
                        )
                    }
                    trySend(Response.Success(requests))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun getMaintenanceRequestById(requestId: String): Flow<Response<MaintenanceRequest>> = callbackFlow {
        trySend(Response.Loading)

        val listener = firestore.collection("maintenance_requests")
            .document(requestId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Response.Error(error.localizedMessage ?: "Unknown error"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val request = snapshot.toObject(MaintenanceRequest::class.java)?.copy(
                        maintenanceRequestsId = snapshot.id,
                    )
                    if (request != null) {
                        trySend(Response.Success(request))
                    } else {
                        trySend(Response.Error("Request not found"))
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    override fun createMaintenanceRequest(request: MaintenanceRequest): Flow<Response<MaintenanceRequest>> = flow {
        emit(Response.Loading)
        try {
            val docRef = firestore.collection("maintenance_requests").add(request).await()
            val createdRequest = request.copy(maintenanceRequestsId = docRef.id)
            firestore.collection("maintenance_requests")
                .document(createdRequest.maintenanceRequestsId!!)
                .set(createdRequest)
                .await()
            emit(Response.Success(createdRequest))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Failed to create request"))
        }
    }

    override fun updateMaintenanceRequest(request: MaintenanceRequest): Flow<Response<MaintenanceRequest>> = flow {
        emit(Response.Loading)
        try {
            request.maintenanceRequestsId?.let { id ->
                firestore.collection("maintenance_requests").document(id).set(request).await()
                emit(Response.Success(request))
            } ?: emit(Response.Error("Request ID is required"))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Failed to update request"))
        }
    }

    override fun deleteMaintenanceRequest(requestId: String): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            if (requestId.isBlank()) {
                emit(Response.Error("Request ID is missing. Cannot delete request."))
                return@flow
            }

            firestore.collection("maintenance_requests")
                .document(requestId)
                .delete()
                .await()

            Log.d("MaintenanceRequestRepo", "Document with ID: $requestId successfully deleted")
            emit(Response.Success(true))
        } catch (e: Exception) {
            Log.e("MaintenanceRequestRepo", "Error deleting document with ID: $requestId: ${e.localizedMessage}")
            emit(Response.Error("Failed to delete request: ${e.localizedMessage}"))
        }
    }
}

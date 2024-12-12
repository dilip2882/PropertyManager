package propertymanager.feature.tenant.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.propertymanager.common.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import propertymanager.feature.tenant.domian.model.MaintenanceRequest
import propertymanager.feature.tenant.domian.repository.MaintenanceRequestRepository
import java.util.UUID
import javax.inject.Inject

class MaintenanceRequestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MaintenanceRequestRepository {

    override suspend fun getMaintenanceRequests(): Flow<Response<List<MaintenanceRequest>>> = callbackFlow {
        trySend(Response.Loading)

        val listenerRegistration = firestore.collection("maintenance_requests")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    trySend(Response.Error(exception.message ?: "Error listening for updates"))
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(MaintenanceRequest::class.java)?.copy(id = document.id)
                }

                if (requests != null) {
                    trySend(Response.Success(requests))
                } else {
                    trySend(Response.Error("Failed to load requests"))
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun getMaintenanceRequestById(requestId: String): Flow<Response<MaintenanceRequest>> = callbackFlow {
        trySend(Response.Loading)

        firestore.collection("maintenance_requests")
            .document(requestId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val request = document.toObject(MaintenanceRequest::class.java)
                    if (request != null) {
                        trySend(Response.Success(request))
                    } else {
                        trySend(Response.Error("Failed to deserialize request"))
                    }
                } else {
                    trySend(Response.Error("Request not found"))
                }
            }
            .addOnFailureListener { exception ->
                trySend(Response.Error(exception.message ?: "Unknown error"))
            }

        awaitClose { }
    }

    override suspend fun createMaintenanceRequest(request: MaintenanceRequest): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            val docRef = firestore.collection("maintenance_requests").add(request).await()

            val updatedRequest = request.copy(id = docRef.id)

            firestore.collection("maintenance_requests")
                .document(updatedRequest.id!!)
                .set(updatedRequest)
                .await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to create request"))
        }
    }

    override suspend fun updateMaintenanceRequest(request: MaintenanceRequest): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            if (request.id.isNullOrEmpty()) {
                emit(Response.Error("Request ID is missing. Cannot update request."))
                return@flow
            }

            firestore.collection("maintenance_requests")
                .document(request.id)
                .update(
                    "issueDescription", request.issueDescription,
                    "issueCategory", request.issueCategory,
                    "priority", request.priority,
                    "photos", request.photos,
                    "videos", request.videos
                ).await()

            emit(Response.Success(true))

        } catch (e: Exception) {
            emit(Response.Error("Error updating request: ${e.message ?: "Unknown error"}"))
        }
    }



    override suspend fun deleteMaintenanceRequest(requestId: String): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            if (requestId.isNullOrEmpty()) {
                emit(Response.Error("Request ID is missing. Cannot delete request."))
                return@flow
            }

            firestore.collection("maintenance_requests")
                .document(requestId)
                .delete()
                .await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to delete request"))
        }
    }

}

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

        firestore.collection("maintenance_requests")
            .get()
            .addOnSuccessListener { result ->
                val requests = result.mapNotNull { it.toObject(MaintenanceRequest::class.java) }
                trySend(Response.Success(requests))
            }
            .addOnFailureListener { exception ->
                trySend(Response.Error(exception.message ?: "Unknown error"))
            }

        awaitClose { }
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
            firestore.collection("maintenance_requests")
                .add(request)
                .await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to create request"))
        }
    }

    override suspend fun updateMaintenanceRequest(request: MaintenanceRequest): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            firestore.collection("maintenance_requests")
                .document(request.id)
                .set(request)
                .await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to update request"))
        }
    }

    override suspend fun deleteMaintenanceRequest(requestId: String): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
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

package com.propertymanager.domain.repository

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.WorkerDetails
import kotlinx.coroutines.flow.Flow

interface StaffRepository {
    suspend fun getAssignedRequests(staffId: String): Flow<Response<List<MaintenanceRequest>>>
    suspend fun updateRequestStatus(requestId: String, status: String): Flow<Response<Boolean>>
    suspend fun updateRequestPriority(requestId: String, priority: String): Flow<Response<Boolean>>
    suspend fun assignWorker(requestId: String, workerDetails: WorkerDetails): Flow<Response<Boolean>>
    suspend fun updateRequestNotes(requestId: String, notes: String): Flow<Response<Boolean>>
}

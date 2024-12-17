package com.propertymanager.domain.repository

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import kotlinx.coroutines.flow.Flow

interface MaintenanceRequestRepository {
    fun getAvailableCategories(): List<String>
    fun getMaintenanceRequests(): Flow<Response<List<MaintenanceRequest>>>
    fun getMaintenanceRequestById(requestId: String): Flow<Response<MaintenanceRequest>>
    fun createMaintenanceRequest(request: MaintenanceRequest): Flow<Response<MaintenanceRequest>>
    fun updateMaintenanceRequest(request: MaintenanceRequest): Flow<Response<MaintenanceRequest>>
    fun deleteMaintenanceRequest(requestId: String): Flow<Response<Boolean>>
}

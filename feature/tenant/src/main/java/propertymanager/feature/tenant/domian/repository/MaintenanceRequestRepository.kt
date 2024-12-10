package propertymanager.feature.tenant.domian.repository

import android.net.Uri
import com.propertymanager.common.utils.Response
import kotlinx.coroutines.flow.Flow
import propertymanager.feature.tenant.domian.model.MaintenanceRequest

interface MaintenanceRequestRepository {
    suspend fun getMaintenanceRequests(): Flow<Response<List<MaintenanceRequest>>>
    suspend fun getMaintenanceRequestById(requestId: String): Flow<Response<MaintenanceRequest>>
    suspend fun createMaintenanceRequest(request: MaintenanceRequest): Flow<Response<Boolean>>
    suspend fun updateMaintenanceRequest(request: MaintenanceRequest): Flow<Response<Boolean>>
    suspend fun deleteMaintenanceRequest(requestId: String): Flow<Response<Boolean>>
}

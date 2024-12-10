package propertymanager.feature.tenant.domian.usecase.maintenance_request

import com.propertymanager.common.utils.Response
import kotlinx.coroutines.flow.Flow
import propertymanager.feature.tenant.domian.repository.MaintenanceRequestRepository
import javax.inject.Inject

class DeleteMaintenanceRequestUseCase @Inject constructor(
    private val maintenanceRequestRepository: MaintenanceRequestRepository
) {
    suspend operator fun invoke(requestId: String): Flow<Response<Boolean>> {
        return maintenanceRequestRepository.deleteMaintenanceRequest(requestId)
    }
}

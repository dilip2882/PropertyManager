package propertymanager.feature.tenant.domian.usecase.maintenance_request

import com.propertymanager.common.utils.Response
import kotlinx.coroutines.flow.Flow
import propertymanager.feature.tenant.domian.model.MaintenanceRequest
import propertymanager.feature.tenant.domian.repository.MaintenanceRequestRepository
import javax.inject.Inject

class UpdateMaintenanceRequestUseCase @Inject constructor(
    private val maintenanceRequestRepository: MaintenanceRequestRepository
) {
    suspend operator fun invoke(request: MaintenanceRequest): Flow<Response<Boolean>> {
        return maintenanceRequestRepository.updateMaintenanceRequest(request)
    }
}

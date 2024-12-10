package propertymanager.feature.tenant.domian.usecase.maintenance_request

import com.propertymanager.common.utils.Response
import kotlinx.coroutines.flow.Flow
import propertymanager.feature.tenant.domian.model.MaintenanceRequest
import propertymanager.feature.tenant.domian.repository.MaintenanceRequestRepository
import javax.inject.Inject

class GetMaintenanceRequestByIdUseCase @Inject constructor(
    private val maintenanceRequestRepository: MaintenanceRequestRepository
) {
    suspend operator fun invoke(requestId: String): Flow<Response<MaintenanceRequest>> {
        return maintenanceRequestRepository.getMaintenanceRequestById(requestId)
    }
}

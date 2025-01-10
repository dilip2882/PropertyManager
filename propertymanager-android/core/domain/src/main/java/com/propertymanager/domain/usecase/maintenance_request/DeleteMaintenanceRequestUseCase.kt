package com.propertymanager.domain.usecase.maintenance_request

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.repository.MaintenanceRequestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteMaintenanceRequestUseCase @Inject constructor(
    private val maintenanceRequestRepository: MaintenanceRequestRepository
) {
    suspend operator fun invoke(requestId: String): Flow<Response<Boolean>> {
        return maintenanceRequestRepository.deleteMaintenanceRequest(requestId)
    }
}

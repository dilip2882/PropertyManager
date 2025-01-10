package com.propertymanager.domain.usecase.maintenance_request

import com.propertymanager.common.utils.Response
import kotlinx.coroutines.flow.Flow
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.repository.MaintenanceRequestRepository
import javax.inject.Inject

class UpdateMaintenanceRequestUseCase @Inject constructor(
    private val maintenanceRequestRepository: MaintenanceRequestRepository
) {
    suspend operator fun invoke(request: MaintenanceRequest): Flow<Response<MaintenanceRequest>> {
        return maintenanceRequestRepository.updateMaintenanceRequest(request)
    }
}

package com.propertymanager.domain.usecase.maintenance_request

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.repository.MaintenanceRequestRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateMaintenanceRequestUseCase @Inject constructor(
    private val maintenanceRequestRepository: MaintenanceRequestRepository
) {
    suspend operator fun invoke(request: MaintenanceRequest): Flow<Response<MaintenanceRequest>> {
        return maintenanceRequestRepository.createMaintenanceRequest(request)
    }
}

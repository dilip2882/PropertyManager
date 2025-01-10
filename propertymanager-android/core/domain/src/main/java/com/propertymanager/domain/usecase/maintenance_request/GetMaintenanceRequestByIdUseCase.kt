package com.propertymanager.domain.usecase.maintenance_request

import com.propertymanager.common.utils.Response
import kotlinx.coroutines.flow.Flow
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.repository.MaintenanceRequestRepository
import javax.inject.Inject

class GetMaintenanceRequestByIdUseCase @Inject constructor(
    private val maintenanceRequestRepository: MaintenanceRequestRepository
) {
    suspend operator fun invoke(requestId: String): Flow<Response<MaintenanceRequest>> {
        return maintenanceRequestRepository.getMaintenanceRequestById(requestId)
    }
}

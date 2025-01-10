package com.propertymanager.domain.usecase.staff

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAssignedRequestsUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    suspend operator fun invoke(staffId: String): Flow<Response<List<MaintenanceRequest>>> {
        return staffRepository.getAssignedRequests(staffId)
    }
}

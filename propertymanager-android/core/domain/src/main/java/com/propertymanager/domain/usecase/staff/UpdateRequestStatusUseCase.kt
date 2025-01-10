package com.propertymanager.domain.usecase.staff

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateRequestStatusUseCase  @Inject constructor(
    private val staffRepository: StaffRepository
) {
    suspend operator fun invoke(requestId: String, status: String): Flow<Response<Boolean>> {
        return staffRepository.updateRequestStatus(requestId, status)
    }
}

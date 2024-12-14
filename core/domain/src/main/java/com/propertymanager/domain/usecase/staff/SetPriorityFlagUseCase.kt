package com.propertymanager.domain.usecase.staff

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetPriorityFlagUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    suspend operator fun invoke(requestId: String, priority: String): Flow<Response<Boolean>> {
        return staffRepository.updateRequestPriority(requestId, priority)
    }
}

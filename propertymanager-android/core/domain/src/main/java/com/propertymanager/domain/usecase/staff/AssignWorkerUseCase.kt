package com.propertymanager.domain.usecase.staff

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.WorkerDetails
import com.propertymanager.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssignWorkerUseCase @Inject constructor(
    private val repository: StaffRepository
) {
    suspend operator fun invoke(requestId: String, workerDetails: WorkerDetails): Flow<Response<Boolean>> =
        repository.assignWorker(requestId, workerDetails)
}

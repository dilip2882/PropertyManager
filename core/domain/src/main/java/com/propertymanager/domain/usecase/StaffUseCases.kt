package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.staff.AssignWorkerUseCase
import com.propertymanager.domain.usecase.staff.GetAssignedRequestsUseCase
import com.propertymanager.domain.usecase.staff.SetPriorityFlagUseCase
import com.propertymanager.domain.usecase.staff.UpdateRequestNotesUseCase
import com.propertymanager.domain.usecase.staff.UpdateRequestStatusUseCase

data class StaffUseCases(
    val getAssignedRequests: GetAssignedRequestsUseCase,
    val updateRequestStatus: UpdateRequestStatusUseCase,
    val setPriorityFlag: SetPriorityFlagUseCase,
    val assignWorker: AssignWorkerUseCase,
    val updateRequestNotes: UpdateRequestNotesUseCase
)

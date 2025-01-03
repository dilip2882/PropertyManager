package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.maintenance_request.CreateMaintenanceRequestUseCase
import com.propertymanager.domain.usecase.maintenance_request.DeleteMaintenanceRequestUseCase
import com.propertymanager.domain.usecase.maintenance_request.GetMaintenanceRequestByIdUseCase
import com.propertymanager.domain.usecase.maintenance_request.GetMaintenanceRequestsByUserUseCase
import com.propertymanager.domain.usecase.maintenance_request.GetMaintenanceRequestsUseCase
import com.propertymanager.domain.usecase.maintenance_request.UpdateMaintenanceRequestUseCase

data class MaintenanceRequestUseCases(
    val getMaintenanceRequests: GetMaintenanceRequestsUseCase,
    val getMaintenanceRequestsByUserUseCase: GetMaintenanceRequestsByUserUseCase,
    val getMaintenanceRequestById: GetMaintenanceRequestByIdUseCase,
    val createMaintenanceRequest: CreateMaintenanceRequestUseCase,
    val updateMaintenanceRequest: UpdateMaintenanceRequestUseCase,
    val deleteMaintenanceRequest: DeleteMaintenanceRequestUseCase
)

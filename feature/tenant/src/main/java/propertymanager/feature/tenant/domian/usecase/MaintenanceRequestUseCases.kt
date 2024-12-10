package propertymanager.feature.tenant.domian.usecase

import propertymanager.feature.tenant.domian.usecase.maintenance_request.CreateMaintenanceRequestUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.DeleteMaintenanceRequestUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.GetMaintenanceRequestByIdUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.GetMaintenanceRequestsUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.UpdateMaintenanceRequestUseCase

data class MaintenanceRequestUseCases(
    val getMaintenanceRequests: GetMaintenanceRequestsUseCase,
    val getMaintenanceRequestById: GetMaintenanceRequestByIdUseCase,
    val createMaintenanceRequest: CreateMaintenanceRequestUseCase,
    val updateMaintenanceRequest: UpdateMaintenanceRequestUseCase,
    val deleteMaintenanceRequest: DeleteMaintenanceRequestUseCase
)

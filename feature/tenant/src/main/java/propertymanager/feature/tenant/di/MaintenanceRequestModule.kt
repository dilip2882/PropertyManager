package propertymanager.feature.tenant.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import propertymanager.feature.tenant.data.repository.MaintenanceRequestRepositoryImpl
import propertymanager.feature.tenant.domian.repository.MaintenanceRequestRepository
import propertymanager.feature.tenant.domian.usecase.MaintenanceRequestUseCases
import propertymanager.feature.tenant.domian.usecase.maintenance_request.CreateMaintenanceRequestUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.DeleteMaintenanceRequestUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.GetMaintenanceRequestByIdUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.GetMaintenanceRequestsUseCase
import propertymanager.feature.tenant.domian.usecase.maintenance_request.UpdateMaintenanceRequestUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MaintenanceRequestModule {

    @Provides
    @Singleton
    fun providesMaintenanceRequestRepository(firestore: FirebaseFirestore): MaintenanceRequestRepository {
        return MaintenanceRequestRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun providesMaintenanceRequestUseCases(repository: MaintenanceRequestRepository): MaintenanceRequestUseCases {
        return MaintenanceRequestUseCases(
            getMaintenanceRequests = GetMaintenanceRequestsUseCase(repository),
            getMaintenanceRequestById = GetMaintenanceRequestByIdUseCase(repository),
            createMaintenanceRequest = CreateMaintenanceRequestUseCase(repository),
            updateMaintenanceRequest = UpdateMaintenanceRequestUseCase(repository),
            deleteMaintenanceRequest = DeleteMaintenanceRequestUseCase(repository)
        )
    }
}

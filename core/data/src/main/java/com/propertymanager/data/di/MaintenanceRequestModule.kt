package com.propertymanager.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.MaintenanceRequestRepositoryImpl
import com.propertymanager.domain.repository.MaintenanceRequestRepository
import com.propertymanager.domain.usecase.MaintenanceRequestUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.propertymanager.domain.usecase.maintenance_request.CreateMaintenanceRequestUseCase
import com.propertymanager.domain.usecase.maintenance_request.DeleteMaintenanceRequestUseCase
import com.propertymanager.domain.usecase.maintenance_request.GetMaintenanceRequestByIdUseCase
import com.propertymanager.domain.usecase.maintenance_request.GetMaintenanceRequestsByUserUseCase
import com.propertymanager.domain.usecase.maintenance_request.GetMaintenanceRequestsUseCase
import com.propertymanager.domain.usecase.maintenance_request.UpdateMaintenanceRequestUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MaintenanceRequestModule {

    @Provides
    @Singleton
    fun providesMaintenanceRequestRepository(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth): MaintenanceRequestRepository {
        return MaintenanceRequestRepositoryImpl(firestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun providesMaintenanceRequestUseCases(repository: MaintenanceRequestRepository): MaintenanceRequestUseCases {
        return MaintenanceRequestUseCases(
            getMaintenanceRequests = GetMaintenanceRequestsUseCase(repository),
            getMaintenanceRequestsByUserUseCase = GetMaintenanceRequestsByUserUseCase(repository),
            getMaintenanceRequestById = GetMaintenanceRequestByIdUseCase(repository),
            createMaintenanceRequest = CreateMaintenanceRequestUseCase(repository),
            updateMaintenanceRequest = UpdateMaintenanceRequestUseCase(repository),
            deleteMaintenanceRequest = DeleteMaintenanceRequestUseCase(repository)
        )
    }
}

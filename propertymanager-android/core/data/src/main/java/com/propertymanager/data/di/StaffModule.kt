package com.propertymanager.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.StaffRepositoryImpl
import com.propertymanager.domain.repository.StaffRepository
import com.propertymanager.domain.usecase.StaffUseCases
import com.propertymanager.domain.usecase.staff.AssignWorkerUseCase
import com.propertymanager.domain.usecase.staff.GetAssignedRequestsUseCase
import com.propertymanager.domain.usecase.staff.SetPriorityFlagUseCase
import com.propertymanager.domain.usecase.staff.UpdateRequestNotesUseCase
import com.propertymanager.domain.usecase.staff.UpdateRequestStatusUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StaffModule {

    @Provides
    @Singleton
    fun provideStaffRepository(firebaseFirestore: FirebaseFirestore): StaffRepository {
        return StaffRepositoryImpl(firebaseFirestore)
    }

    @Provides
    @Singleton
    fun provideStaffUseCases(repository: StaffRepository): StaffUseCases {
        return StaffUseCases(
            getAssignedRequests = GetAssignedRequestsUseCase(repository),
            updateRequestStatus = UpdateRequestStatusUseCase(repository),
            setPriorityFlag = SetPriorityFlagUseCase(repository),
            assignWorker = AssignWorkerUseCase(repository),
            updateRequestNotes = UpdateRequestNotesUseCase(repository)
        )
    }
}


package com.propertymanager.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.PropertyRepositoryImpl
import com.propertymanager.domain.repository.PropertyRepository
import com.propertymanager.domain.usecase.PropertyUseCases
import com.propertymanager.domain.usecase.property.AddAddressUseCase
import com.propertymanager.domain.usecase.property.AddPropertyUseCase
import com.propertymanager.domain.usecase.property.DeleteAddressUseCase
import com.propertymanager.domain.usecase.property.DeletePropertyUseCase
import com.propertymanager.domain.usecase.property.GetPropertiesUseCase
import com.propertymanager.domain.usecase.property.GetPropertyByIdUseCase
import com.propertymanager.domain.usecase.property.UpdateAddressUseCase
import com.propertymanager.domain.usecase.property.UpdatePropertyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PropertyModule {
    @Provides
    @Singleton
    fun providePropertyRepository(firestore: FirebaseFirestore): PropertyRepository {
        return PropertyRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun providePropertyUseCases(repository: PropertyRepository): PropertyUseCases {
        return PropertyUseCases(
            addProperty = AddPropertyUseCase(repository),
            deleteProperty = DeletePropertyUseCase(repository),
            updateProperty = UpdatePropertyUseCase(repository),
            getProperties = GetPropertiesUseCase(repository),
            getPropertyById = GetPropertyByIdUseCase(repository),
            addAddress = AddAddressUseCase(repository),
            updateAddress = UpdateAddressUseCase(repository),
            deleteAddress = DeleteAddressUseCase(repository)
        )
    }

}

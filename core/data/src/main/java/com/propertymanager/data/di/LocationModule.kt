package com.propertymanager.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.LocationRepositoryImpl
import com.propertymanager.domain.repository.LocationRepository
import com.propertymanager.domain.usecase.LocationUseCases
import com.propertymanager.domain.usecase.location.AddCityUseCase
import com.propertymanager.domain.usecase.location.AddCountryUseCase
import com.propertymanager.domain.usecase.location.AddStateUseCase
import com.propertymanager.domain.usecase.location.DeleteCityUseCase
import com.propertymanager.domain.usecase.location.DeleteCountryUseCase
import com.propertymanager.domain.usecase.location.DeleteStateUseCase
import com.propertymanager.domain.usecase.location.GetCitiesForStateUseCase
import com.propertymanager.domain.usecase.location.GetCountriesUseCase
import com.propertymanager.domain.usecase.location.GetStatesForCountryUseCase
import com.propertymanager.domain.usecase.location.UpdateCityUseCase
import com.propertymanager.domain.usecase.location.UpdateCountryUseCase
import com.propertymanager.domain.usecase.location.UpdateStateUseCase
import com.propertymanager.domain.usecase.location.ValidateLocationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationRepository(
        firestore: FirebaseFirestore
    ): LocationRepository = LocationRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideLocationUseCases(
        repository: LocationRepository
    ): LocationUseCases {
        return LocationUseCases(
            getCountries = GetCountriesUseCase(repository),
            getStatesForCountry = GetStatesForCountryUseCase(repository),
            getCitiesForState = GetCitiesForStateUseCase(repository),
            addCountry = AddCountryUseCase(repository),
            updateCountry = UpdateCountryUseCase(repository),
            deleteCountry = DeleteCountryUseCase(repository),
            addState = AddStateUseCase(repository),
            updateState = UpdateStateUseCase(repository),
            deleteState = DeleteStateUseCase(repository),
            addCity = AddCityUseCase(repository),
            updateCity = UpdateCityUseCase(repository),
            deleteCity = DeleteCityUseCase(repository),
            validateLocation = ValidateLocationUseCase()
        )
    }

}

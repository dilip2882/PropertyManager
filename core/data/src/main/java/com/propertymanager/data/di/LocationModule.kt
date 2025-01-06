package com.propertymanager.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.LocationRepositoryImpl
import com.propertymanager.domain.repository.LocationRepository
import com.propertymanager.domain.usecase.LocationUseCases
import com.propertymanager.domain.usecase.location.city.AddCityUseCase
import com.propertymanager.domain.usecase.location.country.AddCountryUseCase
import com.propertymanager.domain.usecase.location.state.AddStateUseCase
import com.propertymanager.domain.usecase.location.city.DeleteCityUseCase
import com.propertymanager.domain.usecase.location.country.DeleteCountryUseCase
import com.propertymanager.domain.usecase.location.state.DeleteStateUseCase
import com.propertymanager.domain.usecase.location.city.GetCitiesForStateUseCase
import com.propertymanager.domain.usecase.location.country.GetCountriesUseCase
import com.propertymanager.domain.usecase.location.state.GetStatesForCountryUseCase
import com.propertymanager.domain.usecase.location.city.UpdateCityUseCase
import com.propertymanager.domain.usecase.location.country.UpdateCountryUseCase
import com.propertymanager.domain.usecase.location.state.UpdateStateUseCase
import com.propertymanager.domain.usecase.location.ValidateLocationUseCase
import com.propertymanager.domain.usecase.location.block.AddBlockUseCase
import com.propertymanager.domain.usecase.location.block.DeleteBlockUseCase
import com.propertymanager.domain.usecase.location.block.GetBlocksForSocietyUseCase
import com.propertymanager.domain.usecase.location.block.UpdateBlockUseCase
import com.propertymanager.domain.usecase.location.flat.AddFlatUseCase
import com.propertymanager.domain.usecase.location.flat.DeleteFlatUseCase
import com.propertymanager.domain.usecase.location.flat.GetFlatsForTowerUseCase
import com.propertymanager.domain.usecase.location.flat.UpdateFlatUseCase
import com.propertymanager.domain.usecase.location.society.AddSocietyUseCase
import com.propertymanager.domain.usecase.location.society.DeleteSocietyUseCase
import com.propertymanager.domain.usecase.location.society.GetSocietiesForCityUseCase
import com.propertymanager.domain.usecase.location.society.UpdateSocietyUseCase
import com.propertymanager.domain.usecase.location.tower.AddTowerUseCase
import com.propertymanager.domain.usecase.location.tower.DeleteTowerUseCase
import com.propertymanager.domain.usecase.location.tower.GetTowersForBlockUseCase
import com.propertymanager.domain.usecase.location.tower.UpdateTowerUseCase
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
            // Country, State, and City
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
            validateLocation = ValidateLocationUseCase(),

            // Society
            getSocietiesForCity = GetSocietiesForCityUseCase(repository),
            addSociety = AddSocietyUseCase(repository),
            updateSociety = UpdateSocietyUseCase(repository),
            deleteSociety = DeleteSocietyUseCase(repository),

            // Block
            getBlocksForSociety = GetBlocksForSocietyUseCase(repository),
            addBlock = AddBlockUseCase(repository),
            updateBlock = UpdateBlockUseCase(repository),
            deleteBlock = DeleteBlockUseCase(repository),

            // Tower
            getTowersForBlock = GetTowersForBlockUseCase(repository),
            addTower = AddTowerUseCase(repository),
            updateTower = UpdateTowerUseCase(repository),
            deleteTower = DeleteTowerUseCase(repository),

            // Flat
            getFlatsForTower = GetFlatsForTowerUseCase(repository),
            addFlat = AddFlatUseCase(repository),
            updateFlat = UpdateFlatUseCase(repository),
            deleteFlat = DeleteFlatUseCase(repository)
        )
    }


}

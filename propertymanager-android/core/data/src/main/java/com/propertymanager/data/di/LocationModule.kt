package com.propertymanager.data.di

import GetTowersForBlockUseCase
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
import com.propertymanager.domain.usecase.location.block.FindBlockByIdUseCase
import com.propertymanager.domain.usecase.location.block.GetBlocksForSocietyUseCase
import com.propertymanager.domain.usecase.location.block.UpdateBlockUseCase
import com.propertymanager.domain.usecase.location.city.FindCityByIdUseCase
import com.propertymanager.domain.usecase.location.country.FindCountryByIdUseCase
import com.propertymanager.domain.usecase.location.flat.AddFlatUseCase
import com.propertymanager.domain.usecase.location.flat.DeleteFlatUseCase
import com.propertymanager.domain.usecase.location.flat.FindFlatByIdUseCase
import com.propertymanager.domain.usecase.location.flat.GetFlatsForBlockUseCase
import com.propertymanager.domain.usecase.location.flat.GetFlatsForSocietyUseCase
import com.propertymanager.domain.usecase.location.flat.GetFlatsForTowerUseCase
import com.propertymanager.domain.usecase.location.flat.UpdateFlatUseCase
import com.propertymanager.domain.usecase.location.society.AddSocietyUseCase
import com.propertymanager.domain.usecase.location.society.DeleteSocietyUseCase
import com.propertymanager.domain.usecase.location.society.FindSocietyByIdUseCase
import com.propertymanager.domain.usecase.location.society.GetSocietiesForCityUseCase
import com.propertymanager.domain.usecase.location.society.UpdateSocietyUseCase
import com.propertymanager.domain.usecase.location.state.FindStateByIdUseCase
import com.propertymanager.domain.usecase.location.tower.AddTowerUseCase
import com.propertymanager.domain.usecase.location.tower.DeleteTowerUseCase
import com.propertymanager.domain.usecase.location.tower.FindTowerByIdUseCase
import com.propertymanager.domain.usecase.location.tower.GetTowersForSocietyUseCase
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
    ): LocationRepository {
        return LocationRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideLocationUseCases(
        repository: LocationRepository
    ): LocationUseCases {
        return LocationUseCases(
            // Country
            getCountries = GetCountriesUseCase(repository),
            findCountryById = FindCountryByIdUseCase(repository),
            addCountry = AddCountryUseCase(repository),
            updateCountry = UpdateCountryUseCase(repository),
            deleteCountry = DeleteCountryUseCase(repository),

            // State
            getStatesForCountry = GetStatesForCountryUseCase(repository),
            findStateById = FindStateByIdUseCase(repository),
            addState = AddStateUseCase(repository),
            updateState = UpdateStateUseCase(repository),
            deleteState = DeleteStateUseCase(repository),

            // City
            getCitiesForState = GetCitiesForStateUseCase(repository),
            findCityById = FindCityByIdUseCase(repository),
            addCity = AddCityUseCase(repository),
            updateCity = UpdateCityUseCase(repository),
            deleteCity = DeleteCityUseCase(repository),

            // Society
            getSocietiesForCity = GetSocietiesForCityUseCase(repository),
            findSocietyById = FindSocietyByIdUseCase(repository),
            addSociety = AddSocietyUseCase(repository),
            updateSociety = UpdateSocietyUseCase(repository),
            deleteSociety = DeleteSocietyUseCase(repository),

            // Block
            getBlocksForSociety = GetBlocksForSocietyUseCase(repository),
            findBlockById = FindBlockByIdUseCase(repository),
            addBlock = AddBlockUseCase(repository),
            updateBlock = UpdateBlockUseCase(repository),
            deleteBlock = DeleteBlockUseCase(repository),

            // Tower
            getTowersForSociety = GetTowersForSocietyUseCase(repository),
            findTowerById = FindTowerByIdUseCase(repository),
            addTower = AddTowerUseCase(repository),
            updateTower = UpdateTowerUseCase(repository),
            deleteTower = DeleteTowerUseCase(repository),

            // Flat
            getFlatsForSociety = GetFlatsForSocietyUseCase(repository),
            getFlatsForBlock = GetFlatsForBlockUseCase(repository),
            getFlatsForTower = GetFlatsForTowerUseCase(repository),
            findFlatById = FindFlatByIdUseCase(repository),
            addFlat = AddFlatUseCase(repository),
            updateFlat = UpdateFlatUseCase(repository),
            deleteFlat = DeleteFlatUseCase(repository),

            validateLocation = ValidateLocationUseCase()
        )
    }
}

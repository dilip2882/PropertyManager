package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.location.ValidateLocationUseCase
import com.propertymanager.domain.usecase.location.block.AddBlockUseCase
import com.propertymanager.domain.usecase.location.block.DeleteBlockUseCase
import com.propertymanager.domain.usecase.location.block.GetBlocksForSocietyUseCase
import com.propertymanager.domain.usecase.location.block.UpdateBlockUseCase
import com.propertymanager.domain.usecase.location.city.AddCityUseCase
import com.propertymanager.domain.usecase.location.city.DeleteCityUseCase
import com.propertymanager.domain.usecase.location.city.GetCitiesForStateUseCase
import com.propertymanager.domain.usecase.location.city.UpdateCityUseCase
import com.propertymanager.domain.usecase.location.country.AddCountryUseCase
import com.propertymanager.domain.usecase.location.country.DeleteCountryUseCase
import com.propertymanager.domain.usecase.location.country.GetCountriesUseCase
import com.propertymanager.domain.usecase.location.country.UpdateCountryUseCase
import com.propertymanager.domain.usecase.location.flat.AddFlatUseCase
import com.propertymanager.domain.usecase.location.flat.DeleteFlatUseCase
import com.propertymanager.domain.usecase.location.flat.GetFlatsForTowerUseCase
import com.propertymanager.domain.usecase.location.flat.UpdateFlatUseCase
import com.propertymanager.domain.usecase.location.society.AddSocietyUseCase
import com.propertymanager.domain.usecase.location.society.DeleteSocietyUseCase
import com.propertymanager.domain.usecase.location.society.GetSocietiesForCityUseCase
import com.propertymanager.domain.usecase.location.society.UpdateSocietyUseCase
import com.propertymanager.domain.usecase.location.state.AddStateUseCase
import com.propertymanager.domain.usecase.location.state.DeleteStateUseCase
import com.propertymanager.domain.usecase.location.state.GetStatesForCountryUseCase
import com.propertymanager.domain.usecase.location.state.UpdateStateUseCase
import com.propertymanager.domain.usecase.location.tower.AddTowerUseCase
import com.propertymanager.domain.usecase.location.tower.DeleteTowerUseCase
import com.propertymanager.domain.usecase.location.tower.GetTowersForBlockUseCase
import com.propertymanager.domain.usecase.location.tower.UpdateTowerUseCase

data class LocationUseCases(
    val getCountries: GetCountriesUseCase,
    val getStatesForCountry: GetStatesForCountryUseCase,
    val getCitiesForState: GetCitiesForStateUseCase,
    val addCountry: AddCountryUseCase,
    val updateCountry: UpdateCountryUseCase,
    val deleteCountry: DeleteCountryUseCase,
    val addState: AddStateUseCase,
    val updateState: UpdateStateUseCase,
    val deleteState: DeleteStateUseCase,
    val addCity: AddCityUseCase,
    val updateCity: UpdateCityUseCase,
    val deleteCity: DeleteCityUseCase,
    val validateLocation: ValidateLocationUseCase,

    // Society
    val getSocietiesForCity: GetSocietiesForCityUseCase,
    val addSociety: AddSocietyUseCase,
    val updateSociety: UpdateSocietyUseCase,
    val deleteSociety: DeleteSocietyUseCase,

    // Block
    val getBlocksForSociety: GetBlocksForSocietyUseCase,
    val addBlock: AddBlockUseCase,
    val updateBlock: UpdateBlockUseCase,
    val deleteBlock: DeleteBlockUseCase,

    // Tower
    val getTowersForBlock: GetTowersForBlockUseCase,
    val addTower: AddTowerUseCase,
    val updateTower: UpdateTowerUseCase,
    val deleteTower: DeleteTowerUseCase,

    // Flat
    val getFlatsForTower: GetFlatsForTowerUseCase,
    val addFlat: AddFlatUseCase,
    val updateFlat: UpdateFlatUseCase,
    val deleteFlat: DeleteFlatUseCase,
)

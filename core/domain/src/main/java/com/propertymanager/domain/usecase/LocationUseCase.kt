package com.propertymanager.domain.usecase

import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.repository.LocationRepository
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
    val validateLocation: ValidateLocationUseCase
)

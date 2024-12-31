package com.propertymanager.domain.usecase.location

import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.State
import javax.inject.Inject

class ValidateLocationUseCase @Inject constructor() {
    operator fun invoke(country: Country?, state: State?, city: City?): ValidationResult {
        country?.let {
            if (it.name.isBlank()) {
                return ValidationResult.Error("Country name cannot be empty")
            }
        }
        state?.let {
            if (it.name.isBlank()) {
                return ValidationResult.Error("State name cannot be empty")
            }
        }
        city?.let {
            if (it.name.isBlank()) {
                return ValidationResult.Error("City name cannot be empty")
            }
        }
        return ValidationResult.Success
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

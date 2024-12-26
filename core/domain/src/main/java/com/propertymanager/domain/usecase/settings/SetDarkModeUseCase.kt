package com.propertymanager.domain.usecase.settings

import com.propertymanager.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetDarkModeUseCase @Inject constructor(private val repository: PreferencesRepository) {
    suspend fun execute(enabled: Boolean) {
        repository.setDarkMode(enabled)
    }
}

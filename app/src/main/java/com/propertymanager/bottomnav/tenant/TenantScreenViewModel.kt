package com.propertymanager.bottomnav.tenant

import androidx.lifecycle.ViewModel
import com.propertymanager.common.preferences.temp.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TenantScreenViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    suspend fun clearAuthToken() {
        appPreferences.removeAuthToken()
    }
}

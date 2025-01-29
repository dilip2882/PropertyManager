package propertymanager.feature.staff.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.common.preferences.PreferenceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StaffSettingsViewModel @Inject constructor(
    private val preferenceStore: PreferenceStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val languagePreference = preferenceStore.getString("language", "English")

    val language: StateFlow<String> = languagePreference.stateIn(viewModelScope)

    init {
        viewModelScope.launch {
            val currentLanguage = languagePreference.get()
            updateLocale(currentLanguage)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            languagePreference.set(language)
            updateLocale(language)
        }
    }

    private fun updateLocale(language: String) {
        val locale = when (language) {
            "हिंदी" -> Locale("hi", "IN")
            "Español" -> Locale("es", "ES")
            "Français" -> Locale("fr", "FR")
            else -> Locale("en", "US")
        }
        // Update Android Resources Configuration
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        // Update App-wide Locale
        Locale.setDefault(locale)
        
        // Create new instance of context with updated locale
        context.createConfigurationContext(config)
    }
}

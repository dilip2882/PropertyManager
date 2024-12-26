package propertymanager.feature.staff.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.domain.usecase.settings.GetDarkModeUseCase
import com.propertymanager.domain.usecase.settings.GetDynamicColorUseCase
import com.propertymanager.domain.usecase.settings.SetDarkModeUseCase
import com.propertymanager.domain.usecase.settings.SetDynamicColorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val setDynamicColorUseCase: SetDynamicColorUseCase,
    private val getDynamicColorUseCase: GetDynamicColorUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase
) : ViewModel() {
    private val _dynamicColor = MutableStateFlow(false)
    val dynamicColor: StateFlow<Boolean> = _dynamicColor

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    init {
        getDynamicColorUseCase.execute().map {
            _dynamicColor.value = it
        }.launchIn(viewModelScope)

        getDarkModeUseCase.execute().map {
            _darkMode.value = it
        }.launchIn(viewModelScope)
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            setDynamicColorUseCase.execute(enabled)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            setDarkModeUseCase.execute(enabled)
        }
    }

}

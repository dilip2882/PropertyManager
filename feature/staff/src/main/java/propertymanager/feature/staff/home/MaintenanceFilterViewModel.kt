package propertymanager.feature.staff.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MaintenanceFilterViewModel @Inject constructor() : ViewModel() {

    private val _filterState = MutableStateFlow(MaintenanceFilterState())
    val filterState = _filterState.asStateFlow()

    fun updateSelectedTab(tab: FilterTab) {
        _filterState.update { it.copy(selectedTab = tab) }
    }

    fun updateDisplayMode(mode: DisplayMode) {
        _filterState.update { it.copy(displayMode = mode) }
    }

    fun updateGridSize(size: Int) {
        _filterState.update { it.copy(gridSize = size) }
    }

    fun updateStatusFilter(status: String, selected: Boolean) {
        _filterState.update { state ->
            val newStatuses = if (selected) {
                state.selectedStatuses + status
            } else {
                state.selectedStatuses - status
            }
            state.copy(selectedStatuses = newStatuses)
        }
    }

    fun updatePriorityFilter(priority: String, selected: Boolean) {
        _filterState.update { state ->
            val newPriorities = if (selected) {
                state.selectedPriorities + priority
            } else {
                state.selectedPriorities - priority
            }
            state.copy(selectedPriorities = newPriorities)
        }
    }

    fun updateSortOption(option: SortOption) {
        _filterState.update { 
            it.copy(
                sortBy = option,
                isAscending = true  // Reset to ascending when changing sort option
            )
        }
    }

    fun toggleSortDirection() {
        _filterState.update { 
            it.copy(isAscending = !it.isAscending) 
        }
    }

}

data class MaintenanceFilterState(
    // Tab selection
    val selectedTab: FilterTab = FilterTab.FILTER,
    
    // Status and Priority filters
    val selectedStatuses: Set<String> = emptySet(),
    val selectedPriorities: Set<String> = emptySet(),

    // Display states
    val displayMode: DisplayMode = DisplayMode.LIST,
    val gridSize: Int = 4,
    
    // Sort states
    val sortBy: SortOption = SortOption.DATE_ADDED,
    val isAscending: Boolean = true
)


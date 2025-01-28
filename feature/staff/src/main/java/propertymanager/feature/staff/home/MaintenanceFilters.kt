package propertymanager.feature.staff.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.PriorityLevel
import com.propertymanager.domain.model.RequestStatus
import kotlin.reflect.KFunction2

@Composable
fun MaintenanceFilters(
    viewModel: MaintenanceFilterViewModel = hiltViewModel()
) {
    val filterState by viewModel.filterState.collectAsState()
    var selectedTab by remember { mutableStateOf(FilterTab.FILTER) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(text = tab.title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            FilterTab.FILTER -> {
                FilterContent(
                    filterState = filterState,
                    onStatusFilterChange = viewModel::updateStatusFilter,
                    onPriorityFilterChange = viewModel::updatePriorityFilter
                )
            }
            FilterTab.SORT -> {
                SortContent(
                    filterState = filterState,
                    onSortOptionChange = viewModel::updateSortOption,
                    onSortDirectionChange = viewModel::toggleSortDirection
                )
            }
            FilterTab.DISPLAY -> {
                DisplayContent(
                    filterState = filterState,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun FilterContent(
    filterState: MaintenanceFilterState,
    onStatusFilterChange: KFunction2<String, Boolean, Unit>,
    onPriorityFilterChange: KFunction2<String, Boolean, Unit>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RequestStatus.entries.forEach { status ->
                FilterChip(
                    selected = filterState.selectedStatuses.contains(status.label),
                    onClick = { 
                        onStatusFilterChange(
                            status.label, 
                            !filterState.selectedStatuses.contains(status.label)
                        )
                    },
                    label = { Text(status.label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Priority",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PriorityLevel.entries.forEach { priority ->
                FilterChip(
                    selected = filterState.selectedPriorities.contains(priority.label),
                    onClick = { 
                        onPriorityFilterChange(
                            priority.label, 
                            !filterState.selectedPriorities.contains(priority.label)
                        )
                    },
                    label = { Text(priority.label) }
                )
            }
        }
    }
}

@Composable
private fun SortContent(
    filterState: MaintenanceFilterState,
    onSortOptionChange: (SortOption) -> Unit,
    onSortDirectionChange: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Sort by",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortOption.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSortOptionChange(option) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = filterState.sortBy == option,
                        onClick = { onSortOptionChange(option) }
                    )
                    Text(
                        text = option.title,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Order",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onSortDirectionChange) {
                Icon(
                    imageVector = if (filterState.isAscending) {
                        Icons.Default.ArrowUpward
                    } else {
                        Icons.Default.ArrowDownward
                    },
                    contentDescription = if (filterState.isAscending) {
                        "Sort Ascending"
                    } else {
                        "Sort Descending"
                    }
                )
            }
        }
    }
}

@Composable
private fun DisplayContent(
    filterState: MaintenanceFilterState,
    viewModel: MaintenanceFilterViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Display Mode",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayMode.entries.forEach { mode ->
                OutlinedButton(
                    onClick = { viewModel.updateDisplayMode(mode) },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (filterState.displayMode == mode)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (filterState.displayMode == mode)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = mode.title,
                        color = if (filterState.displayMode == mode)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Grid size section
        if (filterState.displayMode != DisplayMode.LIST) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Grid size",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${filterState.gridSize} per row",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Slider(
                value = filterState.gridSize.toFloat(),
                onValueChange = { viewModel.updateGridSize(it.toInt()) },
                valueRange = 2f..6f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

enum class FilterTab(val title: String) {
    FILTER("Filter"),
    SORT("Sort"),
    DISPLAY("Display")
}

enum class DisplayMode(val title: String) {
    COMPACT_GRID("Compact grid"),
    COMFORTABLE_GRID("Comfortable grid"),
    COVER_ONLY_GRID("Cover-only grid"),
    LIST("List")
}

enum class SortOption(val title: String, val description: String) {
    ALPHABETICAL("Name", "Sort alphabetically by request name"),
    DATE_ADDED("Date added", "Sort by creation date"),
    LAST_UPDATED("Last updated", "Sort by most recently updated")
} 

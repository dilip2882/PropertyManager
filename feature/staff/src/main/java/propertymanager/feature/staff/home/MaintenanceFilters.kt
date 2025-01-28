package propertymanager.feature.staff.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.PriorityLevel
import com.propertymanager.domain.model.RequestStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFilters(
    viewModel: MaintenanceFilterViewModel = hiltViewModel()
) {
    val filterState by viewModel.filterState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Simple Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilterTab.entries.forEach { tab ->
                TextButton(
                    onClick = { viewModel.updateSelectedTab(tab) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (filterState.selectedTab == tab)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        // Blue indicator for selected tab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(when (filterState.selectedTab) {
                        FilterTab.FILTER -> Alignment.CenterStart
                        FilterTab.SORT -> Alignment.Center
                        FilterTab.DISPLAY -> Alignment.CenterEnd
                    })
            )
        }

        Divider(modifier = Modifier.padding(top = 1.dp))

        // Content based on selected tab
        when (filterState.selectedTab) {
            FilterTab.FILTER -> FilterContent(filterState, viewModel)
            FilterTab.SORT -> SortContent(filterState, viewModel)
            FilterTab.DISPLAY -> DisplayContent(filterState, viewModel)
        }
    }
}

@Composable
private fun FilterContent(
    filterState: MaintenanceFilterState,
    viewModel: MaintenanceFilterViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Status Section
        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RequestStatus.getAllStatuses().forEach { status ->
            FilterOption(
                text = status,
                checked = filterState.selectedStatuses.contains(status),
                onCheckedChange = { checked ->
                    viewModel.updateStatusFilter(status, checked)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Priority Section
        Text(
            text = "Priority",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        PriorityLevel.getAllPriorities().forEach { priority ->
            FilterOption(
                text = priority,
                checked = filterState.selectedPriorities.contains(priority),
                onCheckedChange = { checked ->
                    viewModel.updatePriorityFilter(priority, checked)
                }
            )
        }
    }
}

@Composable
private fun FilterOption(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun SortContent(
    filterState: MaintenanceFilterState,
    viewModel: MaintenanceFilterViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SortOption.entries.forEach { sortOption ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        if (filterState.sortBy == sortOption) {
                            viewModel.toggleSortDirection()
                        } else {
                            viewModel.updateSortOption(sortOption)
                        }
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = sortOption.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = sortOption.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (filterState.sortBy == sortOption) {
                    Icon(
                        imageVector = if (filterState.isAscending) 
                            Icons.Default.ArrowUpward 
                        else Icons.Default.ArrowDownward,
                        contentDescription = if (filterState.isAscending) 
                            "Sort Ascending" 
                        else "Sort Descending",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
            text = "Display mode",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Display mode options
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DisplayMode.entries.forEach { mode ->
                OutlinedButton(
                    onClick = { viewModel.updateDisplayMode(mode) },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (filterState.displayMode == mode) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (filterState.displayMode == mode) 
                            MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(mode.title)
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

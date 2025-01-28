package propertymanager.feature.staff.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class FlatFilterState(
    val isOccupied: Boolean? = null,
    val sortOption: SortOption = SortOption.ALPHABETICAL,
    val displayMode: DisplayMode = DisplayMode.COMFORTABLE_GRID
)

@Composable
fun FlatFilters(
    filterState: FlatFilterState,
    onFilterStateChanged: (FlatFilterState) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(FilterTab.FILTER) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Tabs
        TabRow(
            selectedTabIndex = FilterTab.entries.indexOf(selectedTab)
        ) {
            FilterTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            FilterTab.FILTER -> FilterContent(
                isOccupied = filterState.isOccupied,
                onIsOccupiedChanged = { newValue ->
                    onFilterStateChanged(filterState.copy(isOccupied = newValue))
                }
            )
            FilterTab.SORT -> SortContent(
                selectedOption = filterState.sortOption,
                onOptionSelected = { option ->
                    onFilterStateChanged(filterState.copy(sortOption = option))
                }
            )
            FilterTab.DISPLAY -> DisplayContent(
                selectedMode = filterState.displayMode,
                onModeSelected = { mode ->
                    onFilterStateChanged(filterState.copy(displayMode = mode))
                }
            )
        }
    }
}

@Composable
private fun FilterContent(
    isOccupied: Boolean?,
    onIsOccupiedChanged: (Boolean?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Occupancy Status",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = isOccupied == true,
                onClick = { onIsOccupiedChanged(if (isOccupied == true) null else true) },
                label = { Text("Occupied") }
            )
            FilterChip(
                selected = isOccupied == false,
                onClick = { onIsOccupiedChanged(if (isOccupied == false) null else false) },
                label = { Text("Vacant") }
            )
        }
    }
}

@Composable
private fun SortContent(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(SortOption.entries.size) { index ->
            val option = SortOption.entries[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = option == selectedOption,
                        onClick = { onOptionSelected(option) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = null
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = option.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = option.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DisplayContent(
    selectedMode: DisplayMode,
    onModeSelected: (DisplayMode) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(DisplayMode.entries.size) { index ->
            val mode = DisplayMode.entries[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = mode == selectedMode,
                        onClick = { onModeSelected(mode) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = mode == selectedMode,
                    onClick = null
                )
                Text(
                    text = mode.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

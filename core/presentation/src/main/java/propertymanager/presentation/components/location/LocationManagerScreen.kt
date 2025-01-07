package propertymanager.presentation.components.location

import AddOptionsBottomSheet
import DialogType
import LocationDetails
import LocationDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.location.*
import com.propertymanager.domain.model.location.State
import propertymanager.presentation.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationManagerScreen(
    viewModel: LocationManagerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var currentDialog by remember { mutableStateOf<DialogType?>(null) }
    var showAddOptions by remember { mutableStateOf(false) }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            // Show error snackbar or dialog
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Manager") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (canShowAddButton(state)) {
                FloatingActionButton(
                    onClick = { showAddOptions = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Left Panel - Navigation Tree
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LocationTreeList(
                        state = state,
                        onCountrySelected = viewModel::onCountrySelected,
                        onStateSelected = viewModel::onStateSelected,
                        onCitySelected = viewModel::onCitySelected,
                        onSocietySelected = viewModel::onSocietySelected,
                        onBlockSelected = viewModel::onBlockSelected,
                        onTowerSelected = viewModel::onTowerSelected,
                        onAddClick = { showAddOptions = true }
                    )
                }

                // Right Panel - Details
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LocationDetails(
                        modifier = Modifier.fillMaxSize(),
                        selectedItem = state.selectedTower ?: state.selectedBlock ?: 
                                     state.selectedSociety ?: state.selectedCity ?: 
                                     state.selectedState ?: state.selectedCountry,
                        onEdit = { currentDialog = getEditDialogType(it) },
                        onDelete = { handleDelete(it, viewModel) }
                    )
                }
            }
        }

        // Dialogs and Bottom Sheets
        if (showAddOptions) {
            AddOptionsBottomSheet(
                state = state,
                onDismiss = { showAddOptions = false },
                onOptionSelected = { 
                    currentDialog = it
                    showAddOptions = false
                }
            )
        }

    currentDialog?.let { dialogType ->
            LocationDialog(
                dialogType = dialogType,
                state = state,
                    onDismiss = { currentDialog = null },
                onConfirm = { entity ->
                    handleDialogConfirm(dialogType, entity, viewModel, state)
                        currentDialog = null
                    }
                )
            }
    }
}

@Composable
private fun LocationTreeList(
    state: LocationManagerState,
    onCountrySelected: (Country) -> Unit,
    onStateSelected: (State) -> Unit,
    onCitySelected: (City) -> Unit,
    onSocietySelected: (Society) -> Unit,
    onBlockSelected: (Block) -> Unit,
    onTowerSelected: (Tower) -> Unit,
    onAddClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        // Countries
        items(
            items = state.countries,
            key = { it.id }
        ) { country ->
            Column {
                    LocationTreeItem(
                        name = country.name,
                    isSelected = country == state.selectedCountry,
                    isExpanded = state.selectedCountry == country,
                    level = 0,
                        onClick = { onCountrySelected(country) },
                    onAddClick = onAddClick
                    )

                // States
                if (state.selectedCountry == country) {
                    state.states.forEach { stateItem ->
                            LocationTreeItem(
                            name = stateItem.name,
                            isSelected = stateItem == state.selectedState,
                            isExpanded = state.selectedState == stateItem,
                            level = 1,
                            onClick = { onStateSelected(stateItem) },
                            onAddClick = onAddClick
                        )

                        // Cities
                        if (state.selectedState == stateItem) {
                                state.cities.forEach { city ->
                                    LocationTreeItem(
                                        name = city.name,
                                    isSelected = city == state.selectedCity,
                                    isExpanded = state.selectedCity == city,
                                    level = 2,
                                        onClick = { onCitySelected(city) },
                                    onAddClick = onAddClick
                                )

                                // Societies
                                if (state.selectedCity == city) {
                                    state.societies.forEach { society ->
                                            LocationTreeItem(
                                                name = society.name,
                                            isSelected = society == state.selectedSociety,
                                            isExpanded = state.selectedSociety == society,
                                            level = 3,
                                                onClick = { onSocietySelected(society) },
                                            onAddClick = onAddClick
                                        )

                                        // Blocks and Towers
                                        if (state.selectedSociety == society) {
                                            state.blocks.forEach { block ->
                                                    LocationTreeItem(
                                                    name = "Block ${block.name}",
                                                    isSelected = block == state.selectedBlock,
                                                    isExpanded = state.selectedBlock == block,
                                                    level = 4,
                                                        onClick = { onBlockSelected(block) },
                                                    onAddClick = onAddClick
                                                )
                                            }

                                            state.towers.forEach { tower ->
                                                LocationTreeItem(
                                                    name = "Tower ${tower.name}",
                                                    isSelected = tower == state.selectedTower,
                                                    isExpanded = state.selectedTower == tower,
                                                    level = 4,
                                                    onClick = { onTowerSelected(tower) },
                                                    onAddClick = onAddClick
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationTreeItem(
    name: String,
    isSelected: Boolean,
    isExpanded: Boolean,
    level: Int,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 16).dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                           else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                           else MaterialTheme.colorScheme.onSurface
                )
            }
            if (isSelected) {
                IconButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun getEditDialogType(item: Any): DialogType {
    return when (item) {
        is Country -> DialogType.EDIT_COUNTRY
        is State -> DialogType.EDIT_STATE
        is City -> DialogType.EDIT_CITY
        is Society -> DialogType.EDIT_SOCIETY
        is Block -> DialogType.EDIT_BLOCK
        is Tower -> DialogType.EDIT_TOWER
        is Flat -> DialogType.EDIT_FLAT
        else -> throw IllegalArgumentException("Unknown item type")
    }
}

private fun handleDelete(item: Any, viewModel: LocationManagerViewModel) {
    when (item) {
        is Country -> viewModel.onEvent(LocationManagerEvent.DeleteCountry(item.id))
        is State -> viewModel.onEvent(LocationManagerEvent.DeleteState(item.id))
        is City -> viewModel.onEvent(LocationManagerEvent.DeleteCity(item.id))
        is Society -> viewModel.onEvent(LocationManagerEvent.DeleteSociety(item.id))
        is Block -> viewModel.onEvent(LocationManagerEvent.DeleteBlock(item.id))
        is Tower -> viewModel.onEvent(LocationManagerEvent.DeleteTower(item.id))
        is Flat -> viewModel.onEvent(LocationManagerEvent.DeleteFlat(item.id))
    }
}

private fun handleDialogConfirm(
    dialogType: DialogType,
    entity: Any,
    viewModel: LocationManagerViewModel,
    state: LocationManagerState
) {
    when (dialogType) {
        DialogType.ADD_COUNTRY -> {
            viewModel.onEvent(LocationManagerEvent.AddCountry(entity as Country))
        }
        DialogType.EDIT_COUNTRY -> {
            viewModel.onEvent(LocationManagerEvent.UpdateCountry(entity as Country))
        }
        DialogType.ADD_STATE -> {
            state.selectedCountry?.let { country ->
                viewModel.onEvent(LocationManagerEvent.AddState(
                    countryId = country.id,
                    state = entity as State
                ))
            }
        }
        DialogType.EDIT_STATE -> {
            state.selectedCountry?.let { country ->
                viewModel.onEvent(LocationManagerEvent.UpdateState(
                    countryId = country.id,
                    state = entity as State
                ))
            }
        }
        DialogType.ADD_CITY -> {
            state.selectedState?.let { selectedState ->
                state.selectedCountry?.let { country ->
                    viewModel.onEvent(LocationManagerEvent.AddCity(
                        countryId = country.id,
                        stateId = selectedState.id,
                        city = entity as City
                    ))
                }
            }
        }
        DialogType.EDIT_CITY -> {
            state.selectedState?.let { selectedState ->
                state.selectedCountry?.let { country ->
                    viewModel.onEvent(LocationManagerEvent.UpdateCity(
                        countryId = country.id,
                        stateId = selectedState.id,
                        city = entity as City
                    ))
                }
            }
        }
        DialogType.ADD_SOCIETY -> {
            state.selectedCity?.let { city ->
                state.selectedState?.let { selectedState ->
                    state.selectedCountry?.let { country ->
                        viewModel.onEvent(LocationManagerEvent.AddSociety(
                            countryId = country.id,
                            stateId = selectedState.id,
                            cityId = city.id,
                            society = entity as Society
                        ))
                    }
                }
            }
        }
        DialogType.EDIT_SOCIETY -> {
            state.selectedCity?.let { city ->
                state.selectedState?.let { selectedState ->
                    state.selectedCountry?.let { country ->
                        viewModel.onEvent(LocationManagerEvent.UpdateSociety(
                            countryId = country.id,
                            stateId = selectedState.id,
                            cityId = city.id,
                            society = entity as Society
                        ))
                    }
                }
            }
        }
        DialogType.ADD_BLOCK -> {
            state.selectedSociety?.let { society ->
                viewModel.onEvent(LocationManagerEvent.AddBlock(
                    societyId = society.id,
                    block = entity as Block
                ))
            }
        }
        DialogType.EDIT_BLOCK -> {
            state.selectedSociety?.let { society ->
                viewModel.onEvent(LocationManagerEvent.UpdateBlock(
                    societyId = society.id,
                    block = entity as Block
                ))
            }
        }
        DialogType.ADD_TOWER -> {
            state.selectedSociety?.let { society ->
                state.selectedBlock?.let { block ->
                    viewModel.onEvent(LocationManagerEvent.AddTower(
                        societyId = society.id,
                        blockId = block.id,
                        tower = entity as Tower
                    ))
                }
            }
        }
        DialogType.EDIT_TOWER -> {
            state.selectedSociety?.let { society ->
                state.selectedBlock?.let { block ->
                    viewModel.onEvent(LocationManagerEvent.UpdateTower(
                        societyId = society.id,
                        blockId = block.id,
                        tower = entity as Tower
                    ))
                }
            }
        }
        DialogType.ADD_FLAT -> {
            state.selectedSociety?.let { society ->
                val parentId = when {
                    state.selectedTower != null -> state.selectedTower.id
                    state.selectedBlock != null -> state.selectedBlock.id
                    else -> society.id
                }
                viewModel.onEvent(LocationManagerEvent.AddFlat(
                    societyId = society.id,
                    parentId = parentId,
                    flat = entity as Flat
                ))
            }
        }
        DialogType.EDIT_FLAT -> {
            state.selectedSociety?.let { society ->
                val parentId = when {
                    state.selectedTower != null -> state.selectedTower.id
                    state.selectedBlock != null -> state.selectedBlock.id
                    else -> society.id
                }
                viewModel.onEvent(LocationManagerEvent.UpdateFlat(
                    societyId = society.id,
                    parentId = parentId,
                    flat = entity as Flat
                ))
            }
        }
        else -> {}
    }
}

private fun canShowAddButton(state: LocationManagerState): Boolean {
    return when {
        state.selectedTower != null -> true  // Can add flats
        state.selectedBlock != null -> true  // Can add flats
        state.selectedSociety != null -> true  // Can add blocks, towers, or flats
        state.selectedCity != null -> true  // Can add societies
        state.selectedState != null -> true  // Can add cities
        state.selectedCountry != null -> true  // Can add states
        else -> true  // Can add countries
    }
}

// Add this function to handle add button clicks based on selection
private fun getAddDialogType(state: LocationManagerState): DialogType {
    return when {
        state.selectedTower != null -> DialogType.ADD_FLAT
        state.selectedBlock != null -> DialogType.ADD_FLAT
        state.selectedSociety != null -> DialogType.ADD_BLOCK // Show bottom sheet for multiple options
        state.selectedCity != null -> DialogType.ADD_SOCIETY
        state.selectedState != null -> DialogType.ADD_CITY
        state.selectedCountry != null -> DialogType.ADD_STATE
        else -> DialogType.ADD_COUNTRY
    }
}

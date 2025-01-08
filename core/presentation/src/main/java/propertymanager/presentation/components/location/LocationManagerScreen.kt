package propertymanager.presentation.components.location

import AddOptionsBottomSheet
import DialogType
import LocationDetails
import LocationDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
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

/*
Countries
└── States
    └── Cities
        └── Societies
            ├── Blocks
            │   └── Flats (if block selected)
            ├── Towers
            │   └── Flats (if tower selected)
            └── Flats (direct society flats)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationManagerScreen(
    viewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var currentDialog by remember { mutableStateOf<DialogType?>(null) }
    var showAddOptions by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Manager") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
            LoadingScreen()
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
                    LazyColumn {
                        items(state.countries) { country ->
                            LocationTreeItem(
                                name = country.name,
                                isSelected = country.id == state.selectedCountry?.id,
                                isExpanded = state.selectedCountry?.id == country.id,
                                level = 0,
                                onClick = { viewModel.onCountrySelected(country) },
                                onAddClick = { currentDialog = DialogType.ADD_STATE }
                            )

                            if (state.selectedCountry?.id == country.id) {
                                state.states.forEach { stateItem ->
                                    LocationTreeItem(
                                        name = stateItem.name,
                                        isSelected = stateItem.id == state.selectedState?.id,
                                        isExpanded = state.selectedState?.id == stateItem.id,
                                        level = 1,
                                        onClick = { viewModel.onStateSelected(stateItem) },
                                        onAddClick = { currentDialog = DialogType.ADD_CITY }
                                    )

                                    if (state.selectedState?.id == stateItem.id) {
                                        state.cities.forEach { city ->
                                            LocationTreeItem(
                                                name = city.name,
                                                isSelected = city.id == state.selectedCity?.id,
                                                isExpanded = state.selectedCity?.id == city.id,
                                                level = 2,
                                                onClick = { viewModel.onCitySelected(city) },
                                                onAddClick = { currentDialog = DialogType.ADD_SOCIETY }
                                            )

                                            if (state.selectedCity?.id == city.id) {
                                                state.societies.forEach { society ->
                                                    LocationTreeItem(
                                                        name = society.name,
                                                        isSelected = society.id == state.selectedSociety?.id,
                                                        isExpanded = state.selectedSociety?.id == society.id,
                                                        level = 3,
                                                        onClick = { viewModel.onSocietySelected(society) },
                                                        onAddClick = { showAddOptions = true }
                                                    )

                                                    if (state.selectedSociety?.id == society.id) {
                                                        // Blocks
                                                        state.blocks.forEach { block ->
                                                            LocationTreeItem(
                                                                name = block.name,
                                                                isSelected = block.id == state.selectedBlock?.id,
                                                                isExpanded = state.selectedBlock?.id == block.id,
                                                                level = 4,
                                                                onClick = { viewModel.onBlockSelected(block) },
                                                                onAddClick = { currentDialog = DialogType.ADD_FLAT }
                                                            )

                                                            if (state.selectedBlock?.id == block.id) {
                                                                state.flats.filter { it.blockId == block.id }
                                                                    .forEach { flat ->
                                                                        LocationTreeItem(
                                                                            name = "Flat ${flat.number}",
                                                                            isSelected = false,
                                                                            isExpanded = false,
                                                                            level = 5,
                                                                            onClick = { },
                                                                            onAddClick = { }
                                                                        )
                                                                    }
                                                            }
                                                        }

                                                        // Towers
                                                        state.towers.forEach { tower ->
                                                            LocationTreeItem(
                                                                name = tower.name,
                                                                isSelected = tower.id == state.selectedTower?.id,
                                                                isExpanded = state.selectedTower?.id == tower.id,
                                                                level = 4,
                                                                onClick = { viewModel.onTowerSelected(tower) },
                                                                onAddClick = { currentDialog = DialogType.ADD_FLAT }
                                                            )

                                                            if (state.selectedTower?.id == tower.id) {
                                                                state.flats.filter { it.towerId == tower.id }
                                                                    .forEach { flat ->
                                                                        LocationTreeItem(
                                                                            name = "Flat ${flat.number}",
                                                                            isSelected = false,
                                                                            isExpanded = false,
                                                                            level = 5,
                                                                            onClick = { },
                                                                            onAddClick = { }
                                                                        )
                                                                    }
                                                            }
                                                        }

                                                        // Society direct flats
                                                        state.flats.filter { it.blockId == null && it.towerId == null }
                                                            .forEach { flat ->
                                                                LocationTreeItem(
                                                                    name = "Flat ${flat.number}",
                                                                    isSelected = false,
                                                                    isExpanded = false,
                                                                    level = 4,
                                                                    onClick = { },
                                                                    onAddClick = { }
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

        // Dialogs
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

@Composable
private fun LocationItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(16.dp)
        )
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

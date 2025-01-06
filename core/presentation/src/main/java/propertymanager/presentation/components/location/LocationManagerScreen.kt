package propertymanager.presentation.components.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower
import propertymanager.presentation.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationManagerScreen(
    viewModel: LocationManagerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var currentDialog by remember { mutableStateOf<DialogType?>(null) }

    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedState by remember { mutableStateOf<State?>(null) }
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var selectedSociety by remember { mutableStateOf<Society?>(null) }
    var selectedBlock by remember { mutableStateOf<Block?>(null) }
    var selectedTower by remember { mutableStateOf<Tower?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(LocationManagerEvent.LoadLocations)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Manager") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingScreen()
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                // Navigation Panel
                NavigationPanel(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight(),
                    countries = state.countries,
                    selectedCountry = selectedCountry,
                    selectedState = selectedState,
                    selectedCity = selectedCity,
                    selectedSociety = selectedSociety,
                    selectedBlock = selectedBlock,
                    selectedTower = selectedTower,
                    onCountrySelected = { selectedCountry = it },
                    onStateSelected = { selectedState = it },
                    onCitySelected = { selectedCity = it },
                    onSocietySelected = { selectedSociety = it },
                    onBlockSelected = { selectedBlock = it },
                    onTowerSelected = { selectedTower = it },
                    onAddClick = { type -> currentDialog = type },
                )

                // Details Panel
                DetailsPanel(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    selectedCountry = selectedCountry,
                    selectedState = selectedState,
                    selectedCity = selectedCity,
                    selectedSociety = selectedSociety,
                    selectedBlock = selectedBlock,
                    selectedTower = selectedTower,
                    onAddClick = { type -> currentDialog = type },
                    onEdit = { type -> currentDialog = type },
                    onDelete = { entity ->
                        when (entity) {
                            is Country -> viewModel.onEvent(LocationManagerEvent.DeleteCountry(entity.id))
                            is State -> selectedCountry?.let { country ->
                                viewModel.onEvent(LocationManagerEvent.DeleteState(country.id, entity.id))
                            }

                            is City -> selectedState?.let { state ->
                                viewModel.onEvent(LocationManagerEvent.DeleteCity(state.id, entity.id))
                            }

                            is Society -> selectedCity?.let { city ->
                                viewModel.onEvent(LocationManagerEvent.DeleteSociety(city.id, entity.id))
                            }

                            is Block -> selectedSociety?.let { society ->
                                viewModel.onEvent(LocationManagerEvent.DeleteBlock(society.id, entity.id))
                            }

                            is Tower -> selectedBlock?.let { block ->
                                viewModel.onEvent(LocationManagerEvent.DeleteTower(block.id, entity.id))
                            }
                        }
                    },
                )
            }
        }
    }

    currentDialog?.let { dialogType ->
        when (dialogType) {
            DialogType.ADD_COUNTRY -> AddEditCountryDialog(
                country = null,
                onDismiss = { currentDialog = null },
                onConfirm = { country ->
                    viewModel.onEvent(LocationManagerEvent.AddCountry(country))
                    currentDialog = null
                },
            )

            DialogType.ADD_STATE -> {
                selectedCountry?.let { country ->
                    AddEditStateDialog(
                        state = null,
                        onDismiss = { currentDialog = null },
                        onConfirm = { state ->
                            viewModel.onEvent(LocationManagerEvent.AddState(country.id, state))
                            currentDialog = null
                        },
                    )
                }
            }

            DialogType.ADD_CITY -> {
                AddEditCityDialog(
                    city = null,
                    onDismiss = { currentDialog = null },
                    onConfirm = { city ->
                        viewModel.onEvent(
                            LocationManagerEvent.AddCity(
                                selectedState?.id ?: return@AddEditCityDialog,
                                city,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }

            DialogType.ADD_BLOCK -> {
                AddEditBlockDialog(
                    block = null,
                    onDismiss = { currentDialog = null },
                    onConfirm = { block ->
                        viewModel.onEvent(
                            LocationManagerEvent.AddBlock(
                                selectedSociety?.id ?: return@AddEditBlockDialog, block,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }

            DialogType.ADD_FLAT -> {
                AddEditFlatDialog(
                    flat = null,
                    onDismiss = { currentDialog = null },
                    onConfirm = { flat ->
                        // Handle adding the flat here
                        currentDialog = null
                    },
                )
            }

            DialogType.ADD_SOCIETY -> {
                AddEditSocietyDialog(
                    society = null,
                    onDismiss = { currentDialog = null },
                    onConfirm = { society ->
                        viewModel.onEvent(
                            LocationManagerEvent.AddSociety(
                                selectedCity?.id ?: return@AddEditSocietyDialog, society,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }

            DialogType.ADD_TOWER -> {
                AddEditTowerDialog(
                    tower = null,
                    onDismiss = { currentDialog = null },
                    onConfirm = { tower ->
                        viewModel.onEvent(
                            LocationManagerEvent.AddTower(
                                selectedBlock?.id ?: return@AddEditTowerDialog,
                                tower,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }

            DialogType.EDIT_BLOCK -> {
                AddEditBlockDialog(
                    block = selectedBlock,
                    onDismiss = { currentDialog = null },
                    onConfirm = { block ->
                        viewModel.onEvent(
                            LocationManagerEvent.UpdateBlock(
                                selectedSociety?.id ?: return@AddEditBlockDialog, block,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }

            DialogType.EDIT_CITY -> {
                AddEditCityDialog(
                    city = selectedCity,
                    onDismiss = { currentDialog = null },
                    onConfirm = { city ->
                        viewModel.onEvent(
                            LocationManagerEvent.UpdateCity(
                                selectedState?.id ?: return@AddEditCityDialog,
                                city,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }

            DialogType.EDIT_COUNTRY -> {
                // You can add the edit country dialog here if needed
                TODO()
            }

            DialogType.EDIT_FLAT -> {
                AddEditFlatDialog(
                    flat = null, // Provide the selected flat to edit
                    onDismiss = { currentDialog = null },
                    onConfirm = { flat ->
                        // Handle edit flat logic here
                        currentDialog = null
                    },
                )
            }

            DialogType.EDIT_SOCIETY -> {
                AddEditSocietyDialog(
                    society = selectedSociety,
                    onDismiss = { currentDialog = null },
                    onConfirm = { society ->
                        viewModel.onEvent(
                            LocationManagerEvent.UpdateSociety(
                                selectedCity?.id ?: return@AddEditSocietyDialog, society,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }

            DialogType.EDIT_STATE -> {
                // Add edit state dialog here if needed
                TODO()
            }

            DialogType.EDIT_TOWER -> {
                AddEditTowerDialog(
                    tower = selectedTower,
                    onDismiss = { currentDialog = null },
                    onConfirm = { tower ->
                        viewModel.onEvent(
                            LocationManagerEvent.UpdateTower(
                                selectedBlock?.id ?: return@AddEditTowerDialog, tower,
                            ),
                        )
                        currentDialog = null
                    },
                )
            }
        }
    }
}

@Composable
private fun NavigationPanel(
    modifier: Modifier = Modifier,
    countries: List<Country>,
    selectedCountry: Country?,
    selectedState: State?,
    selectedCity: City?,
    selectedSociety: Society?,
    selectedBlock: Block?,
    selectedTower: Tower?,
    onCountrySelected: (Country) -> Unit,
    onStateSelected: (State) -> Unit,
    onCitySelected: (City) -> Unit,
    onSocietySelected: (Society) -> Unit,
    onBlockSelected: (Block) -> Unit,
    onTowerSelected: (Tower) -> Unit,
    onAddClick: (DialogType) -> Unit,
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            LocationHeader(
                title = "Countries",
                onAddClick = { onAddClick(DialogType.ADD_COUNTRY) },
            )

            LazyColumn {
                items(countries) { country ->
                    LocationTreeItem(
                        name = country.name,
                        isSelected = country == selectedCountry,
                        onClick = { onCountrySelected(country) },
                        onAddChild = { onAddClick(DialogType.ADD_STATE) },
                    )

                    if (country == selectedCountry) {
                        country.states.forEach { state ->
                            LocationTreeItem(
                                name = state.name,
                                isSelected = state == selectedState,
                                onClick = { onStateSelected(state) },
                                onAddChild = { onAddClick(DialogType.ADD_CITY) },
                                indentLevel = 1,
                            )

                            if (state == selectedState) {
                                state.cities.forEach { city ->
                                    LocationTreeItem(
                                        name = city.name,
                                        isSelected = city == selectedCity,
                                        onClick = { onCitySelected(city) },
                                        onAddChild = { onAddClick(DialogType.ADD_SOCIETY) },
                                        indentLevel = 2,
                                    )
                                    if (city == selectedCity) {
                                        city.societies.forEach { society ->
                                            LocationTreeItem(
                                                name = society.name,
                                                isSelected = society == selectedSociety,
                                                onClick = { onSocietySelected(society) },
                                                onAddChild = { onAddClick(DialogType.ADD_BLOCK) },
                                                indentLevel = 3,
                                            )
                                            if (society == selectedSociety) {
                                                society.blocks.forEach { block ->
                                                    LocationTreeItem(
                                                        name = block.name,
                                                        isSelected = block == selectedBlock,
                                                        onClick = { onBlockSelected(block) },
                                                        onAddChild = { onAddClick(DialogType.ADD_TOWER) },
                                                        indentLevel = 4,
                                                    )
                                                    if (block == selectedBlock) {
                                                        block.towers.forEach { tower ->
                                                            LocationTreeItem(
                                                                name = tower.name,
                                                                isSelected = tower == selectedTower,
                                                                onClick = { onTowerSelected(tower) },
                                                                onAddChild = { onAddClick(DialogType.ADD_FLAT) },
                                                                indentLevel = 4,
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
        }
    }
}

@Composable
private fun LocationHeader(
    title: String,
    onAddClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
private fun LocationTreeItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onAddChild: () -> Unit,
    indentLevel: Int = 0,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (indentLevel * 16).dp)
            .padding(vertical = 2.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            IconButton(
                onClick = onAddChild,
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = "Add Child",
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditCountryDialog(
    country: Country?,
    onDismiss: () -> Unit,
    onConfirm: (Country) -> Unit,
) {
    var name by remember { mutableStateOf(country?.name ?: "") }
    var iso2 by remember { mutableStateOf(country?.iso2 ?: "") }
    var iso3 by remember { mutableStateOf(country?.iso3 ?: "") }
    var phoneCode by remember { mutableStateOf(country?.phoneCode ?: "") }
    var currency by remember { mutableStateOf(country?.currency ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (country == null) "Add Country" else "Edit Country")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Country Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = iso2,
                    onValueChange = { iso2 = it },
                    label = { Text("ISO2") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = iso3,
                    onValueChange = { iso3 = it },
                    label = { Text("ISO3") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = phoneCode,
                    onValueChange = { phoneCode = it },
                    label = { Text("Phone Code") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Currency") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Country(
                            id = country?.id ?: 0,
                            name = name,
                            iso2 = iso2,
                            iso3 = iso3,
                            phoneCode = phoneCode,
                            currency = currency,
                        ),
                    )
                },
            ) {
                Text(if (country == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun AddEditStateDialog(
    state: State?,
    onDismiss: () -> Unit,
    onConfirm: (State) -> Unit,
) {
    var name by remember { mutableStateOf(state?.name ?: "") }
    var stateCode by remember { mutableStateOf(state?.stateCode ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (state == null) "Add State" else "Edit State")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("State Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = stateCode,
                    onValueChange = { stateCode = it },
                    label = { Text("State Code") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        State(
                            id = state?.id ?: 0,
                            name = name,
                            stateCode = stateCode,
                        ),
                    )
                },
            ) {
                Text(if (state == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun AddEditCityDialog(
    city: City?,
    onDismiss: () -> Unit,
    onConfirm: (City) -> Unit,
) {
    var name by remember { mutableStateOf(city?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (city == null) "Add City" else "Edit City")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("City Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        City(
                            id = city?.id ?: 0,
                            name = name,
                        ),
                    )
                },
            ) {
                Text(if (city == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun AddEditSocietyDialog(
    society: Society?,
    onDismiss: () -> Unit,
    onConfirm: (Society) -> Unit,
) {
    var name by remember { mutableStateOf(society?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (society == null) "Add Society" else "Edit Society")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Society Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Society(
                            id = society?.id ?: 0,
                            name = name,
                        ),
                    )
                },
            ) {
                Text(if (society == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun AddEditBlockDialog(
    block: Block?,
    onDismiss: () -> Unit,
    onConfirm: (Block) -> Unit,
) {
    var name by remember { mutableStateOf(block?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (block == null) "Add Block" else "Edit Block")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Block Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Block(
                            id = block?.id ?: 0,
                            name = name,
                        ),
                    )
                },
            ) {
                Text(if (block == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun AddEditTowerDialog(
    tower: Tower?,
    onDismiss: () -> Unit,
    onConfirm: (Tower) -> Unit,
) {
    var name by remember { mutableStateOf(tower?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (tower == null) "Add Tower" else "Edit Tower")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tower Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Tower(
                            id = tower?.id ?: 0,
                            name = name,
                        ),
                    )
                },
            ) {
                Text(if (tower == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun AddEditFlatDialog(
    flat: Flat?,
    onDismiss: () -> Unit,
    onConfirm: (Flat) -> Unit,
) {
    var number by remember { mutableStateOf(flat?.number ?: "") }
    var type by remember { mutableStateOf(flat?.type ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (flat == null) "Add Flat" else "Edit Flat")
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Flat Name/Number") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Flat(
                            id = flat?.id ?: 0,
                            number = number,
                            type = type,
                            floor = 0,
                            area = 0.0,
                            status = "",
                        ),
                    )
                },
            ) {
                Text(if (flat == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

sealed class DialogType {
    object ADD_COUNTRY : DialogType()
    object EDIT_COUNTRY : DialogType()
    object ADD_STATE : DialogType()
    object EDIT_STATE : DialogType()
    object ADD_CITY : DialogType()
    object EDIT_CITY : DialogType()
    object ADD_SOCIETY : DialogType()
    object EDIT_SOCIETY : DialogType()
    object ADD_BLOCK : DialogType()
    object EDIT_BLOCK : DialogType()
    object ADD_TOWER : DialogType()
    object EDIT_TOWER : DialogType()
    object ADD_FLAT : DialogType()
    object EDIT_FLAT : DialogType()
}

@Composable
private fun DetailsPanel(
    modifier: Modifier = Modifier,
    selectedCountry: Country?,
    selectedState: State?,
    selectedCity: City?,
    selectedSociety: Society?,
    selectedBlock: Block?,
    selectedTower: Tower?,
    onAddClick: (DialogType) -> Unit,
    onEdit: (DialogType) -> Unit,
    onDelete: (Any) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            when {
                selectedTower != null -> TowerDetails(
                    tower = selectedTower,
                    onAddFlat = { onAddClick(DialogType.ADD_FLAT) },
                    onEdit = { onEdit(DialogType.EDIT_TOWER) },
                    onDelete = { onDelete(selectedTower) },
                )

                selectedBlock != null -> BlockDetails(
                    block = selectedBlock,
                    onAddTower = {
                        onAddClick(
                            DialogType.ADD_TOWER,
                        )
                    },
                    onEdit = { onEdit(DialogType.EDIT_BLOCK) },
                    onDelete = { onDelete(selectedBlock) },
                )

                selectedSociety != null -> SocietyDetails(
                    society = selectedSociety,
                    onAddBlock = { onAddClick(DialogType.ADD_BLOCK) },
                    onEdit = { onEdit(DialogType.EDIT_SOCIETY) },
                    onDelete = { onDelete(selectedSociety) },
                )

                selectedCity != null -> CityDetails(
                    city = selectedCity,
                    onAddSociety = { onAddClick(DialogType.ADD_SOCIETY) },
                    onEdit = { onEdit(DialogType.EDIT_CITY) },
                    onDelete = { onDelete(selectedCity) },
                )

                selectedState != null -> StateDetails(
                    state = selectedState,
                    onAddCity = { onAddClick(DialogType.ADD_CITY) },
                    onEdit = { onEdit(DialogType.EDIT_STATE) },
                    onDelete = { onDelete(selectedState) },
                )

                selectedCountry != null -> CountryDetails(
                    country = selectedCountry,
                    onAddState = { onAddClick(DialogType.ADD_STATE) },
                    onEdit = { onEdit(DialogType.EDIT_COUNTRY) },
                    onDelete = { onDelete(selectedCountry) },
                )
            }
        }
    }
}

@Composable
private fun TowerDetails(
    tower: Tower,
    onAddFlat: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        Text("Tower: ${tower.name}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TextButton(onClick = onAddFlat) {
                Text("Add Flat")
            }
            TextButton(onClick = onEdit) {
                Text("Edit Tower")
            }
            TextButton(onClick = onDelete) {
                Text("Delete Tower")
            }
        }
    }
}

@Composable
private fun BlockDetails(
    block: Block,
    onAddTower: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        Text("Block: ${block.name}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TextButton(onClick = onAddTower) {
                Text("Add Tower")
            }
            TextButton(onClick = onEdit) {
                Text("Edit Block")
            }
            TextButton(onClick = onDelete) {
                Text("Delete Block")
            }
        }
    }
}

@Composable
private fun SocietyDetails(
    society: Society,
    onAddBlock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        Text("Society: ${society.name}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TextButton(onClick = onAddBlock) {
                Text("Add Block")
            }
            TextButton(onClick = onEdit) {
                Text("Edit Society")
            }
            TextButton(onClick = onDelete) {
                Text("Delete Society")
            }
        }
    }
}

@Composable
private fun CityDetails(
    city: City,
    onAddSociety: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        Text("City: ${city.name}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TextButton(onClick = onAddSociety) {
                Text("Add Society")
            }
            TextButton(onClick = onEdit) {
                Text("Edit City")
            }
            TextButton(onClick = onDelete) {
                Text("Delete City")
            }
        }
    }
}

@Composable
private fun StateDetails(
    state: State,
    onAddCity: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        Text("State: ${state.name}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TextButton(onClick = onAddCity) {
                Text("Add City")
            }
            TextButton(onClick = onEdit) {
                Text("Edit State")
            }
            TextButton(onClick = onDelete) {
                Text("Delete State")
            }
        }
    }
}

@Composable
private fun CountryDetails(
    country: Country,
    onAddState: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Column {
        Text("Country: ${country.name}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TextButton(onClick = onAddState) {
                Text("Add State")
            }
            TextButton(onClick = onEdit) {
                Text("Edit Country")
            }
            TextButton(onClick = onDelete) {
                Text("Delete Country")
            }
        }
    }
}

/*
@Composable
fun LocationManagerScreen(
    viewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showLocationDialog by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedState by remember { mutableStateOf<State?>(null) }
    var selectedCity by remember { mutableStateOf<City?>(null) }
    var dialogMode by remember { mutableStateOf<LocationDialogMode>(LocationDialogMode.None) }

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                }

                is UiEvent.Error -> {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(MR.strings.staff_location_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    dialogMode = LocationDialogMode.AddCountry
                    showLocationDialog = true
                },
            ) {
                Icon(Icons.Default.Add, "Add Location")
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(state.countries) { country ->
                        LocationCard(
                            country = country,
                            onEditCountry = {
                                selectedCountry = country
                                dialogMode = LocationDialogMode.EditCountry
                                showLocationDialog = true
                            },
                            onDeleteCountry = {
                                scope.launch {
                                    viewModel.onEvent(LocationManagerEvent.DeleteCountry(country.id))
                                }
                            },
                            onAddState = {
                                selectedCountry = country
                                dialogMode = LocationDialogMode.AddState
                                showLocationDialog = true
                            },
                            onEditState = { state ->
                                selectedCountry = country
                                selectedState = state
                                dialogMode = LocationDialogMode.EditState
                                showLocationDialog = true
                            },
                            onDeleteState = { stateId ->
                                scope.launch {
                                    viewModel.onEvent(LocationManagerEvent.DeleteState(country.id, stateId))
                                }
                            },
                            onAddCity = { state ->
                                selectedState = state
                                dialogMode = LocationDialogMode.AddCity
                                showLocationDialog = true
                            },
                            onEditCity = { state, city ->
                                selectedState = state
                                selectedCity = city
                                dialogMode = LocationDialogMode.EditCity
                                showLocationDialog = true
                            },
                            onDeleteCity = { stateId, cityId ->
                                scope.launch {
                                    viewModel.onEvent(LocationManagerEvent.DeleteCity(stateId, cityId))
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    if (showLocationDialog) {
        EnhancedLocationDialog(
            mode = dialogMode,
            selectedCountry = selectedCountry,
            selectedState = selectedState,
            selectedCity = selectedCity,
            onDismiss = {
                showLocationDialog = false
                selectedCountry = null
                selectedState = null
                selectedCity = null
                dialogMode = LocationDialogMode.None
            },
            onSave = { name ->
                when (dialogMode) {
                    LocationDialogMode.AddCountry -> {
                        val country = Country(id = System.currentTimeMillis().toInt(), name = name)
                        viewModel.onEvent(LocationManagerEvent.AddCountry(country))
                    }

                    LocationDialogMode.EditCountry -> {
                        selectedCountry?.let { country ->
                            viewModel.onEvent(LocationManagerEvent.UpdateCountry(country.copy(name = name)))
                        }
                    }

                    LocationDialogMode.AddState -> {
                        selectedCountry?.let { country ->
                            val state = State(id = System.currentTimeMillis().toInt(), name = name)
                            viewModel.onEvent(LocationManagerEvent.AddState(country.id, state))
                        }
                    }

                    LocationDialogMode.EditState -> {
                        selectedCountry?.let { country ->
                            selectedState?.let { state ->
                                viewModel.onEvent(LocationManagerEvent.UpdateState(country.id, state.copy(name = name)))
                            }
                        }
                    }

                    LocationDialogMode.AddCity -> {
                        selectedState?.let { state ->
                            val city = City(id = System.currentTimeMillis().toInt(), name = name)
                            viewModel.onEvent(LocationManagerEvent.AddCity(state.id, city))
                        }
                    }

                    LocationDialogMode.EditCity -> {
                        selectedState?.let { state ->
                            selectedCity?.let { city ->
                                viewModel.onEvent(LocationManagerEvent.UpdateCity(state.id, city.copy(name = name)))
                            }
                        }
                    }

                    LocationDialogMode.None -> {}
                }
                showLocationDialog = false
                selectedCountry = null
                selectedState = null
                selectedCity = null
                dialogMode = LocationDialogMode.None
            },
        )
    }
}

@Composable
fun LocationCard(
    country: Country,
    onEditCountry: () -> Unit,
    onDeleteCountry: () -> Unit,
    onAddState: () -> Unit,
    onEditState: (State) -> Unit,
    onDeleteState: (Int) -> Unit,
    onAddCity: (State) -> Unit,
    onEditCity: (State, City) -> Unit,
    onDeleteCity: (Int, Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = country.name,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "${country.states.size} states",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row {
                    IconButton(onClick = onEditCountry) {
                        Icon(Icons.Default.Edit, "Edit Country")
                    }
                    IconButton(onClick = onDeleteCountry) {
                        Icon(Icons.Default.Delete, "Delete Country")
                    }
                    IconButton(onClick = onAddState) {
                        Icon(Icons.Default.Add, "Add State")
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            "Toggle States",
                        )
                    }
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                country.states.forEach { state ->
                    StateItem(
                        state = state,
                        onEditState = { onEditState(state) },
                        onDeleteState = { onDeleteState(state.id) },
                        onAddCity = { onAddCity(state) },
                        onEditCity = { city -> onEditCity(state, city) },
                        onDeleteCity = { cityId -> onDeleteCity(state.id, cityId) },
                    )
                }
            }
        }
    }
}

@Composable
fun StateItem(
    state: State,
    onEditState: () -> Unit,
    onDeleteState: () -> Unit,
    onAddCity: () -> Unit,
    onEditCity: (City) -> Unit,
    onDeleteCity: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${state.cities.size} cities",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row {
                IconButton(onClick = onEditState) {
                    Icon(Icons.Default.Edit, "Edit State")
                }
                IconButton(onClick = onDeleteState) {
                    Icon(Icons.Default.Delete, "Delete State")
                }
                IconButton(onClick = onAddCity) {
                    Icon(Icons.Default.Add, "Add City")
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        "Toggle Cities",
                    )
                }
            }
        }

        if (expanded) {
            state.cities.forEach { city ->
                CityItem(
                    city = city,
                    onEditCity = { onEditCity(city) },
                    onDeleteCity = { onDeleteCity(city.id) },
                )
            }
        }
    }
}

@Composable
fun CityItem(
    city: City,
    onEditCity: () -> Unit,
    onDeleteCity: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = city.name,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row {
            IconButton(onClick = onEditCity) {
                Icon(Icons.Default.Edit, "Edit City")
            }
            IconButton(onClick = onDeleteCity) {
                Icon(Icons.Default.Delete, "Delete City")
            }
        }
    }
}

@Composable
fun EnhancedLocationDialog(
    mode: LocationDialogMode,
    selectedCountry: Country?,
    selectedState: State?,
    selectedCity: City?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember {
        mutableStateOf(
            when (mode) {
                LocationDialogMode.EditCountry -> selectedCountry?.name
                LocationDialogMode.EditState -> selectedState?.name
                LocationDialogMode.EditCity -> selectedCity?.name
                else -> ""
            } ?: "",
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = when (mode) {
                        LocationDialogMode.AddCountry -> "Add Country"
                        LocationDialogMode.EditCountry -> "Edit Country"
                        LocationDialogMode.AddState -> "Add State"
                        LocationDialogMode.EditState -> "Edit State"
                        LocationDialogMode.AddCity -> "Add City"
                        LocationDialogMode.EditCity -> "Edit City"
                        LocationDialogMode.None -> ""
                    },
                    style = MaterialTheme.typography.titleLarge,
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(name) },
                        enabled = name.isNotBlank(),
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

sealed class LocationDialogMode {
    data object None : LocationDialogMode()
    data object AddCountry : LocationDialogMode()
    data object EditCountry : LocationDialogMode()
    data object AddState : LocationDialogMode()
    data object EditState : LocationDialogMode()
    data object AddCity : LocationDialogMode()
    data object EditCity : LocationDialogMode()
}
*/

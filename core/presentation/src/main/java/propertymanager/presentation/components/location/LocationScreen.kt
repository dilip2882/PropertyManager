package propertymanager.presentation.components.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.Tower
import propertymanager.presentation.screens.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    cityId: Int,
    onNavigateBack: () -> Unit,
    viewModel: LocationManagerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Dialog states
    var showDialog by remember { mutableStateOf<DialogType?>(null) }
    var selectedSociety by remember { mutableStateOf<Society?>(null) }
    var selectedBlock by remember { mutableStateOf<Block?>(null) }
    var selectedTower by remember { mutableStateOf<Tower?>(null) }
    var selectedFlat by remember { mutableStateOf<Flat?>(null) }
    var selectedBlockForFlat by remember { mutableStateOf<Block?>(null) }
    var selectedTowerForFlat by remember { mutableStateOf<Tower?>(null) }


    // Load initial data
    LaunchedEffect(cityId) {
        viewModel.loadSocietiesForCity(cityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = DialogType.ADD_SOCIETY },
                icon = { Icon(Icons.Default.Add, "Add Society") },
                text = { Text("Add Society") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.isLoading) {
                LoadingScreen()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.societies) { society ->
                        SocietyCard(
                            society = society,
                            state = state,
                            viewModel = viewModel,
                            onEditSociety = {
                                selectedSociety = society
                                showDialog = DialogType.EDIT_SOCIETY
                            },
                            onDeleteSociety = {
                                viewModel.onEvent(LocationManagerEvent.DeleteSociety(society.id))
                            },
                            onAddBlock = {
                                selectedSociety = society
                                showDialog = DialogType.ADD_BLOCK
                            },
                            onAddTower = {
                                selectedSociety = society
                                showDialog = DialogType.ADD_TOWER
                            },
                            onAddFlat = {
                                selectedSociety = society
                                showDialog = DialogType.ADD_FLAT
                            },
                            onEditBlock = { block ->
                                selectedBlock = block
                                showDialog = DialogType.EDIT_BLOCK
                            },
                            onEditTower = { tower ->
                                selectedTower = tower
                                showDialog = DialogType.EDIT_TOWER
                            },
                            onEditFlat = { flat ->
                                selectedFlat = flat
                                showDialog = DialogType.EDIT_FLAT
                            },
                        )
                    }
                }
            }

            // Handle dialogs
            when (showDialog) {
                DialogType.ADD_SOCIETY -> {
                    AddEditSocietyDialog(
                        society = null,
                        onDismiss = { showDialog = null },
                        onConfirm = { name ->
                            viewModel.onEvent(
                                LocationManagerEvent.AddSociety(
                                    cityId = cityId,
                                    society = Society(
                                        id = 0,
                                        countryId = 0,
                                        stateId = 0,
                                        cityId = cityId,
                                        name = name,
                                    ),
                                ),
                            )
                            showDialog = null
                        },
                    )
                }

                DialogType.EDIT_SOCIETY -> {
                    selectedSociety?.let { society ->
                        AddEditSocietyDialog(
                            society = society,
                            onDismiss = { showDialog = null },
                            onConfirm = { name ->
                                viewModel.onEvent(
                                    LocationManagerEvent.UpdateSociety(
                                        society.copy(name = name),
                                    ),
                                )
                                showDialog = null
                            },
                        )
                    }
                }

                DialogType.ADD_BLOCK -> {
                    AddEditBlockDialog(
                        block = null,
                        society = selectedSociety!!,
                        onDismiss = { showDialog = null },
                        onConfirm = { name, type ->
                            viewModel.onEvent(
                                LocationManagerEvent.AddBlock(
                                    Block(
                                        id = 0,
                                        societyId = selectedSociety!!.id,
                                        name = name,
                                        type = type,
                                    ),
                                ),
                            )
                            showDialog = null
                        },
                    )
                }

                DialogType.EDIT_BLOCK -> {
                    selectedBlock?.let { block ->
                        AddEditBlockDialog(
                            block = block,
                            society = selectedSociety!!,
                            onDismiss = {
                                showDialog = null
                                selectedBlock = null
                            },
                            onConfirm = { name, type ->
                                viewModel.onEvent(
                                    LocationManagerEvent.UpdateBlock(
                                        block.copy(
                                            name = name,
                                            type = type,
                                        ),
                                    ),
                                )
                                showDialog = null
                                selectedBlock = null
                            },
                        )
                    }
                }

                DialogType.ADD_TOWER -> {
                    AddEditTowerDialog(
                        tower = null,
                        society = selectedSociety!!,
                        onDismiss = { showDialog = null },
                        onConfirm = { name ->
                            viewModel.onEvent(
                                LocationManagerEvent.AddTower(
                                    Tower(
                                        id = 0,
                                        societyId = selectedSociety!!.id,
                                        blockId = 0,
                                        name = name,
                                    ),
                                ),
                            )
                            showDialog = null
                        },
                    )
                }

                DialogType.EDIT_TOWER -> {
                    selectedTower?.let { tower ->
                        AddEditTowerDialog(
                            tower = tower,
                            society = selectedSociety!!,
                            onDismiss = {
                                showDialog = null
                                selectedTower = null
                            },
                            onConfirm = { name ->
                                viewModel.onEvent(
                                    LocationManagerEvent.UpdateTower(
                                        tower.copy(
                                            name = name,
                                        ),
                                    ),
                                )
                                showDialog = null
                                selectedTower = null
                            },
                        )
                    }
                }

                DialogType.ADD_FLAT -> {
                    AddEditFlatDialog(
                        flat = null,
                        society = selectedSociety!!,
                        onDismiss = {
                            showDialog = null
                            selectedBlockForFlat = null
                            selectedTowerForFlat = null
                        },
                        onConfirm = { number, floor, type, area, status ->
                            viewModel.onEvent(
                                LocationManagerEvent.AddFlat(
                                    Flat(
                                        id = 0,
                                        societyId = selectedSociety!!.id,
                                        blockId = selectedBlockForFlat?.id ?: 0,
                                        towerId = selectedTowerForFlat?.id ?: 0,
                                        number = number,
                                        floor = floor,
                                        type = type,
                                        area = area,
                                        status = status,
                                    ),
                                ),
                            )
                            showDialog = null
                            selectedBlockForFlat = null
                            selectedTowerForFlat = null
                        },
                    )
                }

                DialogType.EDIT_FLAT -> {
                    selectedFlat?.let { flat ->
                        AddEditFlatDialog(
                            flat = flat,
                            society = selectedSociety!!,
                            onDismiss = {
                                showDialog = null
                                selectedFlat = null
                            },
                            onConfirm = { number, floor, type, area, status ->
                                viewModel.onEvent(
                                    LocationManagerEvent.UpdateFlat(
                                        flat.copy(
                                            number = number,
                                            floor = floor,
                                            type = type,
                                            area = area,
                                            status = status,
                                        ),
                                    ),
                                )
                                showDialog = null
                                selectedFlat = null
                            },
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun SocietyCard(
    society: Society,
    state: LocationManagerState,
    viewModel: LocationManagerViewModel,
    onEditSociety: () -> Unit,
    onDeleteSociety: () -> Unit,
    onAddBlock: () -> Unit,
    onAddTower: () -> Unit,
    onAddFlat: () -> Unit,
    onEditBlock: (Block) -> Unit,
    onEditTower: (Tower) -> Unit,
    onEditFlat: (Flat) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf<DialogType?>(null) }
    var selectedBlock by remember { mutableStateOf<Block?>(null) }
    var selectedTower by remember { mutableStateOf<Tower?>(null) }
    var selectedFlat by remember { mutableStateOf<Flat?>(null) }
    var selectedBlockForFlat by remember { mutableStateOf<Block?>(null) }
    var selectedTowerForFlat by remember { mutableStateOf<Tower?>(null) }

    // Load data when expanded
    LaunchedEffect(expanded) {
        if (expanded) {
            viewModel.loadBlocksForSociety(society.id)
            viewModel.loadTowersForSociety(society.id)
            viewModel.loadFlatsForSociety(society.id)
        }
    }

    LaunchedEffect(state.towers) {
        println("Towers for society ${society.id}: ${state.towers.filter { it.societyId == society.id }}")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = society.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onEditSociety) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = onDeleteSociety) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                        )
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Button(
                            onClick = { showDialog = DialogType.ADD_BLOCK },
                        ) {
                            Text("Add Block")
                        }
                        Button(
                            onClick = { showDialog = DialogType.ADD_TOWER },
                        ) {
                            Text("Add Tower")
                        }
                        Button(
                            onClick = { showDialog = DialogType.ADD_FLAT },
                        ) {
                            Text("Add Flat")
                        }
                    }

                    // Blocks Section with their flats
                    val blocks = state.blocks.filter { it.societyId == society.id }
                    if (blocks.isNotEmpty()) {
                        Text(
                            text = "Blocks",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        blocks.forEach { block ->
                            val blockFlats = state.flats.filter { it.blockId == block.id }
                            BlockItem(
                                block = block,
                                flats = blockFlats,
                                onEdit = { onEditBlock(block) },
                                onDelete = {
                                    viewModel.onEvent(LocationManagerEvent.DeleteBlock(block.id))
                                },
                                onAddFlat = {
                                    selectedBlockForFlat = block
                                    showDialog = DialogType.ADD_FLAT
                                },
                                onEditFlat = { flat ->
                                    selectedFlat = flat
                                    showDialog = DialogType.EDIT_FLAT
                                },
                                onDeleteFlat = { flat ->
                                    viewModel.onEvent(LocationManagerEvent.DeleteFlat(flat.id))
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Towers Section with their flats
                    val towers = state.towers.filter { it.societyId == society.id }
                    if (towers.isNotEmpty()) {
                        Text(
                            text = "Towers",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        towers.forEach { tower ->
                            val towerFlats = state.flats.filter { it.towerId == tower.id }
                            TowerItem(
                                tower = tower,
                                flats = towerFlats,
                                onEdit = { onEditTower(tower) },
                                onDelete = {
                                    viewModel.onEvent(LocationManagerEvent.DeleteTower(tower.id))
                                },
                                onAddFlat = {
                                    selectedTowerForFlat = tower
                                    showDialog = DialogType.ADD_FLAT
                                },
                                onEditFlat = { flat ->
                                    selectedFlat = flat
                                    showDialog = DialogType.EDIT_FLAT
                                },
                                onDeleteFlat = { flat ->
                                    viewModel.onEvent(LocationManagerEvent.DeleteFlat(flat.id))
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Society-level Flats Section
                    val societyFlats = state.flats.filter {
                        it.societyId == society.id &&
                            it.blockId == 0 &&
                            it.towerId == 0
                    }
                    if (societyFlats.isNotEmpty()) {
                        Text(
                            text = "Society Flats",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        societyFlats.forEach { flat ->
                            FlatItem(
                                flat = flat,
                                onEdit = {
                                    selectedFlat = flat
                                    showDialog = DialogType.EDIT_FLAT
                                },
                                onDelete = {
                                    viewModel.onEvent(LocationManagerEvent.DeleteFlat(flat.id))
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    when (showDialog) {
        DialogType.ADD_BLOCK -> {
            AddEditBlockDialog(
                block = null,
                society = society,
                onDismiss = { showDialog = null },
                onConfirm = { name, type ->
                    viewModel.onEvent(
                        LocationManagerEvent.AddBlock(
                            Block(
                                id = 0,
                                societyId = society.id,
                                name = name,
                                type = type,
                            ),
                        ),
                    )
                    showDialog = null
                },
            )
        }

        DialogType.EDIT_BLOCK -> {
            selectedBlock?.let { block ->
                AddEditBlockDialog(
                    block = block,
                    society = society,
                    onDismiss = {
                        showDialog = null
                        selectedBlock = null
                    },
                    onConfirm = { name, type ->
                        viewModel.onEvent(
                            LocationManagerEvent.UpdateBlock(
                                block.copy(
                                    name = name,
                                    type = type,
                                ),
                            ),
                        )
                        showDialog = null
                        selectedBlock = null
                    },
                )
            }
        }

        DialogType.ADD_TOWER -> {
            AddEditTowerDialog(
                tower = null,
                society = society,
                onDismiss = { showDialog = null },
                onConfirm = { name ->
                    viewModel.onEvent(
                        LocationManagerEvent.AddTower(
                            Tower(
                                id = 0,
                                societyId = society.id,
                                blockId = 0,
                                name = name,
                            ),
                        ),
                    )
                    showDialog = null
                },
            )
        }

        DialogType.EDIT_TOWER -> {
            selectedTower?.let { tower ->
                AddEditTowerDialog(
                    tower = tower,
                    society = society,
                    onDismiss = {
                        showDialog = null
                        selectedTower = null
                    },
                    onConfirm = { name ->
                        viewModel.onEvent(
                            LocationManagerEvent.UpdateTower(
                                tower.copy(
                                    name = name,
                                ),
                            ),
                        )
                        showDialog = null
                        selectedTower = null
                    },
                )
            }
        }

        DialogType.ADD_FLAT -> {
            AddEditFlatDialog(
                flat = null,
                society = society,
                onDismiss = {
                    showDialog = null
                    selectedBlockForFlat = null
                    selectedTowerForFlat = null
                },
                onConfirm = { number, floor, type, area, status ->
                    viewModel.onEvent(
                        LocationManagerEvent.AddFlat(
                            Flat(
                                id = 0,
                                societyId = society.id,
                                blockId = selectedBlockForFlat?.id ?: 0,
                                towerId = selectedTowerForFlat?.id ?: 0,
                                number = number,
                                floor = floor,
                                type = type,
                                area = area,
                                status = status,
                            ),
                        ),
                    )
                    showDialog = null
                    selectedBlockForFlat = null
                    selectedTowerForFlat = null
                },
            )
        }

        DialogType.EDIT_FLAT -> {
            selectedFlat?.let { flat ->
                AddEditFlatDialog(
                    flat = flat,
                    society = society,
                    onDismiss = {
                        showDialog = null
                        selectedFlat = null
                    },
                    onConfirm = { number, floor, type, area, status ->
                        viewModel.onEvent(
                            LocationManagerEvent.UpdateFlat(
                                flat.copy(
                                    number = number,
                                    floor = floor,
                                    type = type,
                                    area = area,
                                    status = status,
                                ),
                            ),
                        )
                        showDialog = null
                        selectedFlat = null
                    },
                )
            }
        }

        else -> {}
    }
}

@Composable
private fun BlockItem(
    block: Block,
    flats: List<Flat>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddFlat: () -> Unit,
    onEditFlat: (Flat) -> Unit,
    onDeleteFlat: (Flat) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        // Block header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Block ${block.name}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Row {
                IconButton(onClick = onAddFlat) {
                    Icon(Icons.Default.Add, "Add Flat")
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit Block")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete Block")
                }
            }
        }

        // Block's flats
        if (flats.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
            ) {
                flats.forEach { flat ->
                    FlatItem(
                        flat = flat,
                        onEdit = { onEditFlat(flat) },
                        onDelete = { onDeleteFlat(flat) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TowerItem(
    tower: Tower,
    flats: List<Flat>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddFlat: () -> Unit,
    onEditFlat: (Flat) -> Unit,
    onDeleteFlat: (Flat) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        // Tower header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Tower ${tower.name}",
                style = MaterialTheme.typography.bodyLarge,
            )
            Row {
                IconButton(onClick = onAddFlat) {
                    Icon(Icons.Default.Add, "Add Flat")
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit Tower")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete Tower")
                }
            }
        }

        // Tower's flats
        if (flats.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
            ) {
                flats.forEach { flat ->
                    FlatItem(
                        flat = flat,
                        onEdit = { onEditFlat(flat) },
                        onDelete = { onDeleteFlat(flat) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FlatItem(
    flat: Flat,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Flat ${flat.number}",
                style = MaterialTheme.typography.bodyLarge,
            )
//            Text(
//                text = "Floor: ${flat.floor} | Type: ${flat.type} | Area: ${flat.area}",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//            )
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit Flat")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete Flat")
            }
        }
    }
}

@Composable
fun AddEditSocietyDialog(
    society: Society?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(society?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (society == null) "Add Society" else "Edit Society") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Society Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
            ) {
                Text(if (society == null) "Add" else "Update")
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
fun AddEditBlockDialog(
    block: Block?,
    society: Society,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf(block?.name ?: "") }
    var type by remember { mutableStateOf(block?.type ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (block == null) "Add Block" else "Edit Block") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Block Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                /*                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = type,
                                    onValueChange = { type = it },
                                    label = { Text("Block Type") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )*/
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, type) },
                enabled = name.isNotBlank(),
            ) {
                Text(if (block == null) "Add" else "Update")
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
    society: Society,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(tower?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tower == null) "Add Tower" else "Edit Tower") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tower Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank(),
            ) {
                Text(if (tower == null) "Add" else "Update")
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
fun AddEditFlatDialog(
    flat: Flat?,
    society: Society,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, Double, String) -> Unit,
) {
    var number by remember { mutableStateOf(flat?.number ?: "") }
    var floor by remember { mutableStateOf((flat?.floor ?: 0).toString()) }
    var type by remember { mutableStateOf(flat?.type ?: "") }
    var area by remember { mutableStateOf((flat?.area ?: 0.0).toString()) }
    var status by remember { mutableStateOf(flat?.status ?: "Available") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (flat == null) "Add Flat" else "Edit Flat") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Flat Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                /*                OutlinedTextField(
                                    value = floor,
                                    onValueChange = { floor = it.filter { char -> char.isDigit() } },
                                    label = { Text("Floor") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                OutlinedTextField(
                                    value = type,
                                    onValueChange = { type = it },
                                    label = { Text("Type") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                OutlinedTextField(
                                    value = area,
                                    onValueChange = { area = it.filter { char -> char.isDigit() || char == '.' } },
                                    label = { Text("Area (sq ft)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                OutlinedTextField(
                                    value = status,
                                    onValueChange = { status = it },
                                    label = { Text("Status") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )*/
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        number,
                        floor.toIntOrNull() ?: 0,
                        type,
                        area.toDoubleOrNull() ?: 0.0,
                        status,
                    )
                },
                enabled = number.isNotBlank(),
            ) {
                Text(if (flat == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}


package propertymanager.presentation.components.location

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.Tower


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationManagerScreen(
    cityId: Int,
    onNavigateBack: () -> Unit,
    locationViewModel: LocationViewModel,
    locationManagerViewModel: LocationManagerViewModel,
) {
    val state by locationManagerViewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf<DialogType?>(null) }
    val context = LocalContext.current

    LaunchedEffect(cityId) {
        locationViewModel.loadSocietiesForCity(cityId)
    }

    LaunchedEffect(Unit) {
        locationViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is UiEvent.Error -> Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
            }
        }
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Add Society Button
            Button(
                onClick = { showDialog = DialogType.ADD_SOCIETY },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Icon(Icons.Default.Add, "Add Society")
                Spacer(Modifier.width(8.dp))
                Text("Add New Society")
            }

            // Location Tree
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                items(state.societies) { society ->
                    LocationItem(
                        name = society.name,
                        level = 0,
                        canExpand = true,
                        isExpanded = state.selectedSociety?.id == society.id,
                        onExpand = { locationViewModel.onEvent(LocationEvent.SelectSociety(society)) },
                        onAdd = { showDialog = DialogType.ADD_BLOCK },
                        onEdit = { showDialog = DialogType.EDIT_SOCIETY },
                        onDelete = {
                            locationManagerViewModel.onEvent(LocationManagerEvent.DeleteSociety(society.id))
                        },
                    )

                    if (state.selectedSociety?.id == society.id) {
                        // Blocks Section
                        state.blocks.forEach { block ->
                            LocationItem(
                                name = "Block ${block.name}",
                                level = 1,
                                canExpand = true,
                                isExpanded = state.selectedBlock?.id == block.id,
                                onExpand = { locationViewModel.onEvent(LocationEvent.SelectBlock(block)) },
                                onAdd = { showDialog = DialogType.ADD_FLAT },
                                onEdit = { showDialog = DialogType.EDIT_BLOCK },
                                onDelete = { locationManagerViewModel.onEvent(LocationManagerEvent.DeleteBlock(block.id)) },
                            )

                            // Show flats under block
                            if (state.selectedBlock?.id == block.id) {
                                state.flats
                                    .filter { it.blockId == block.id }
                                    .forEach { flat ->
                                        LocationItem(
                                            name = "Flat ${flat.number}",
                                            level = 2,
                                            canExpand = false,
                                            isExpanded = false,
                                            onExpand = {},
                                            onEdit = { showDialog = DialogType.EDIT_FLAT },
                                            onDelete = {
                                                locationManagerViewModel.onEvent(
                                                    LocationManagerEvent.DeleteFlat(
                                                        flat.id,
                                                    ),
                                                )
                                            },
                                        )
                                    }
                            }
                        }

                        // Towers Section
                        state.towers.forEach { tower ->
                            LocationItem(
                                name = "Tower ${tower.name}",
                                level = 1,
                                canExpand = true,
                                isExpanded = state.selectedTower?.id == tower.id,
                                onExpand = { locationViewModel.onEvent(LocationEvent.SelectTower(tower)) },
                                onAdd = { showDialog = DialogType.ADD_FLAT },
                                onEdit = { showDialog = DialogType.EDIT_TOWER },
                                onDelete = { locationManagerViewModel.onEvent(LocationManagerEvent.DeleteTower(tower.id)) },
                            )

                            // Show flats under tower
                            if (state.selectedTower?.id == tower.id) {
                                state.flats
                                    .filter { it.towerId == tower.id }
                                    .forEach { flat ->
                                        LocationItem(
                                            name = "Flat ${flat.number}",
                                            level = 2,
                                            canExpand = false,
                                            isExpanded = false,
                                            onExpand = {},
                                            onEdit = { showDialog = DialogType.EDIT_FLAT },
                                            onDelete = {
                                                locationManagerViewModel.onEvent(
                                                    LocationManagerEvent.DeleteFlat(
                                                        flat.id,
                                                    ),
                                                )
                                            },
                                        )
                                    }
                            }
                        }

                        // Direct Society Flats
                        state.flats
                            .filter { it.blockId == null && it.towerId == null }
                            .forEach { flat ->
                                LocationItem(
                                    name = "Flat ${flat.number}",
                                    level = 1,
                                    canExpand = false,
                                    isExpanded = false,
                                    onExpand = {},
                                    onEdit = { showDialog = DialogType.EDIT_FLAT },
                                    onDelete = { locationManagerViewModel.onEvent(LocationManagerEvent.DeleteFlat(flat.id)) },
                                )
                            }
                    }
                }
            }
        }

        // Handle Dialogs
        showDialog?.let { dialogType ->
            LocationDialog(
                dialogType = dialogType,
                state = state,
                onDismiss = { showDialog = null },
                onConfirm = { entity ->
                    when (dialogType) {
                        DialogType.ADD_SOCIETY -> locationManagerViewModel.onEvent(
                            LocationManagerEvent.AddSociety(
                                cityId = cityId,
                                entity as Society,
                            ),
                        )

                        DialogType.EDIT_SOCIETY -> locationManagerViewModel.onEvent(
                            LocationManagerEvent.UpdateSociety(
                                entity as Society,
                            ),
                        )

                        DialogType.ADD_BLOCK -> locationManagerViewModel.onEvent(LocationManagerEvent.AddBlock(entity as Block))
                        DialogType.EDIT_BLOCK -> locationManagerViewModel.onEvent(
                            LocationManagerEvent.UpdateBlock(
                                entity as Block,
                            ),
                        )

                        DialogType.ADD_TOWER -> locationManagerViewModel.onEvent(LocationManagerEvent.AddTower(entity as Tower))
                        DialogType.EDIT_TOWER -> locationManagerViewModel.onEvent(
                            LocationManagerEvent.UpdateTower(
                                entity as Tower,
                            ),
                        )

                        DialogType.ADD_FLAT -> locationManagerViewModel.onEvent(LocationManagerEvent.AddFlat(entity as Flat))
                        DialogType.EDIT_FLAT -> locationManagerViewModel.onEvent(LocationManagerEvent.UpdateFlat(entity as Flat))
                        else -> {}
                    }
                    showDialog = null
                },
            )
        }
    }
}

@Composable
private fun LocationItem(
    name: String,
    level: Int,
    canExpand: Boolean,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onAdd: (() -> Unit)? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 32).dp)
            .clickable(enabled = canExpand, onClick = onExpand)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (canExpand) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(text = name)
        }

        Row {
            onAdd?.let {
                IconButton(onClick = it) {
                    Icon(Icons.Default.Add, "Add")
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    }
}

package propertymanager.presentation.components.location

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDialog(
    dialogType: DialogType,
    state: LocationManagerState,
    onDismiss: () -> Unit,
    onConfirm: (Any) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    // Validate input based on dialog type
    val isValid = when (dialogType) {
        DialogType.ADD_SOCIETY, DialogType.EDIT_SOCIETY -> name.isNotBlank()
        DialogType.ADD_BLOCK, DialogType.EDIT_BLOCK -> name.isNotBlank()
        DialogType.ADD_TOWER, DialogType.EDIT_TOWER -> name.isNotBlank()
        DialogType.ADD_FLAT, DialogType.EDIT_FLAT ->
            name.isNotBlank() && number.isNotBlank()

        else -> false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = getDialogTitle(dialogType)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = name.isBlank(),
                )

                // Show error message if name is blank
                if (name.isBlank()) {
                    Text(
                        text = "Name is required",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    println("Debug: Creating entity with name: $name")
                    createEntity(
                        dialogType = dialogType,
                        name = name,
                        code = code,
                        type = type,
                        number = number,
                        floor = floor,
                        area = area,
                        status = status,
                        state = state,
                    )?.let { entity ->
                        println("Debug: Entity created: $entity")
                        onConfirm(entity)
                    } ?: println("Debug: Entity creation failed")
                },
                enabled = isValid,
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

private fun getDialogTitle(dialogType: DialogType): String {
    return when (dialogType) {
        DialogType.ADD_SOCIETY -> "Add Society"
        DialogType.EDIT_SOCIETY -> "Edit Society"
        DialogType.ADD_BLOCK -> "Add Block"
        DialogType.EDIT_BLOCK -> "Edit Block"
        DialogType.ADD_TOWER -> "Add Tower"
        DialogType.EDIT_TOWER -> "Edit Tower"
        DialogType.ADD_FLAT -> "Add Flat"
        DialogType.EDIT_FLAT -> "Edit Flat"
        else -> ""
    }
}

private fun createEntity(
    dialogType: DialogType,
    name: String,
    code: String,
    type: String,
    number: String,
    floor: String,
    area: String,
    status: String,
    state: LocationManagerState,
): Any? {
    return when (dialogType) {
        DialogType.ADD_COUNTRY, DialogType.EDIT_COUNTRY -> {
            Country(
                id = 0, // ID will be assigned by the repository
                name = name,
                iso2 = code,
            )
        }

        DialogType.ADD_STATE, DialogType.EDIT_STATE -> {
            state.selectedCountry?.let { country ->
                State(
                    id = 0,
                    countryId = country.id,
                    name = name,
                    stateCode = code,
                    type = type,
                )
            }
        }

        DialogType.ADD_CITY, DialogType.EDIT_CITY -> {
            state.selectedState?.let { selectedState ->
                City(
                    id = 0,
                    countryId = selectedState.countryId,
                    stateId = selectedState.id,
                    name = name,
                )
            }
        }

        DialogType.ADD_SOCIETY, DialogType.EDIT_SOCIETY -> {
            state.selectedCity?.let { city ->
                Society(
                    id = 0,
                    countryId = city.countryId,
                    stateId = city.stateId,
                    cityId = city.id,
                    name = name,
                )
            }
        }

        DialogType.ADD_BLOCK, DialogType.EDIT_BLOCK -> {
            state.selectedSociety?.let { society ->
                Block(
                    id = 0,
                    societyId = society.id,
                    name = name,
                    type = type,
                )
            }
        }

        DialogType.ADD_TOWER, DialogType.EDIT_TOWER -> {
            state.selectedSociety?.let { society ->
                state.selectedBlock?.let { block ->
                    Tower(
                        id = 0,
                        societyId = society.id,
                        blockId = block.id,
                        name = name,
                    )
                } ?: Tower(
                    id = 0,
                    societyId = society.id,
                    blockId = 0,
                    name = name,
                )
            }
        }

        DialogType.ADD_FLAT, DialogType.EDIT_FLAT -> {
            when {
                state.selectedTower != null -> {
                    // Create flat in tower
                    Flat(
                        id = 0,
                        societyId = state.selectedSociety?.id ?: 0,
                        blockId = state.selectedBlock?.id,
                        towerId = state.selectedTower.id,
                        number = number,
                        floor = floor.toIntOrNull() ?: 0,
                        type = type,
                        area = area.toDoubleOrNull() ?: 0.0,
                        status = status,
                    )
                }

                state.selectedBlock != null -> {
                    // Create flat in block
                    Flat(
                        id = 0,
                        societyId = state.selectedSociety?.id ?: 0,
                        blockId = state.selectedBlock.id,
                        towerId = null,
                        number = number,
                        floor = floor.toIntOrNull() ?: 0,
                        type = type,
                        area = area.toDoubleOrNull() ?: 0.0,
                        status = status,
                    )
                }

                state.selectedSociety != null -> {
                    // Create flat directly in society
                    Flat(
                        id = 0,
                        societyId = state.selectedSociety.id,
                        blockId = null,
                        towerId = null,
                        number = number,
                        floor = floor.toIntOrNull() ?: 0,
                        type = type,
                        area = area.toDoubleOrNull() ?: 0.0,
                        status = status,
                    )
                }

                else -> null
            }
        }

        else -> null
    }
}

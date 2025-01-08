import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower
import propertymanager.presentation.components.location.LocationManagerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDialog(
    dialogType: DialogType,
    state: LocationManagerState,
    onDismiss: () -> Unit,
    onConfirm: (Any) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (dialogType) {
                    DialogType.ADD_COUNTRY -> "Add Country"
                    DialogType.EDIT_COUNTRY -> "Edit Country"
                    DialogType.ADD_STATE -> "Add State"
                    DialogType.EDIT_STATE -> "Edit State"
                    DialogType.ADD_CITY -> "Add City"
                    DialogType.EDIT_CITY -> "Edit City"
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
            )
        },
        text = {
            Column {
                when (dialogType) {
                    DialogType.ADD_COUNTRY, DialogType.EDIT_COUNTRY -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Country Name") }
                        )
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Country Code") }
                        )
                    }
                    DialogType.ADD_STATE, DialogType.EDIT_STATE -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("State Name") }
                        )
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("State Code") }
                        )
                    }
                    DialogType.ADD_CITY, DialogType.EDIT_CITY -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("City Name") }
                        )
                    }
                    DialogType.ADD_SOCIETY, DialogType.EDIT_SOCIETY -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Society Name") }
                        )
                    }
                    DialogType.ADD_BLOCK, DialogType.EDIT_BLOCK -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Block Name") }
                        )
                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Block Type") }
                        )
                    }
                    DialogType.ADD_TOWER, DialogType.EDIT_TOWER -> {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Tower Name") }
                        )
                    }
                    DialogType.ADD_FLAT, DialogType.EDIT_FLAT -> {
                        OutlinedTextField(
                            value = number,
                            onValueChange = { number = it },
                            label = { Text("Flat Number") }
                        )
                        OutlinedTextField(
                            value = floor,
                            onValueChange = { floor = it },
                            label = { Text("Floor") }
                        )
                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Flat Type") }
                        )
                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it },
                            label = { Text("Area") }
                        )
                        OutlinedTextField(
                            value = status,
                            onValueChange = { status = it },
                            label = { Text("Status") }
                        )
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val entity = createEntity(
                        dialogType = dialogType,
                        name = name,
                        code = code,
                        type = type,
                        number = number,
                        floor = floor,
                        area = area,
                        status = status,
                        state = state
                    )
                    entity?.let { onConfirm(it) }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
    state: LocationManagerState
): Any? {
    return when (dialogType) {
        DialogType.ADD_COUNTRY, DialogType.EDIT_COUNTRY -> {
            Country(
                id = 0, // ID will be assigned by the repository
                name = name,
                iso2 = code
            )
        }
        DialogType.ADD_STATE, DialogType.EDIT_STATE -> {
            state.selectedCountry?.let { country ->
                State(
                    id = 0,
                    countryId = country.id,
                    name = name,
                    stateCode = code,
                    type = type
                )
            }
        }
        DialogType.ADD_CITY, DialogType.EDIT_CITY -> {
            state.selectedState?.let { selectedState ->
                City(
                    id = 0,
                    countryId = selectedState.countryId,
                    stateId = selectedState.id,
                    name = name
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
                    name = name
                )
            }
        }
        DialogType.ADD_BLOCK, DialogType.EDIT_BLOCK -> {
            state.selectedSociety?.let { society ->
                Block(
                    id = 0,
                    societyId = society.id,
                    name = name,
                    type = type
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
                        name = name
                    )
                } ?: Tower(
                    id = 0,
                    societyId = society.id,
                    blockId = 0,
                    name = name
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
                        status = status
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
                        status = status
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
                        status = status
                    )
                }
                else -> null
            }
        }
        else -> null
    }
} 

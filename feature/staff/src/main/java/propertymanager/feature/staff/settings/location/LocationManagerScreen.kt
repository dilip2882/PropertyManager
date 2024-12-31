package propertymanager.feature.staff.settings.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.State
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LocationManagerScreen(
    viewModel: LocationManagerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var showAddCountryDialog by remember { mutableStateOf(false) }
    var showAddStateDialog by remember { mutableStateOf(false) }
    var showAddCityDialog by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var selectedState by remember { mutableStateOf<State?>(null) }

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                    // Show snackbar or toast
                }
                is UiEvent.Error -> {
                    // Show error message
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Location Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddCountryDialog = true }) {
                        Icon(Icons.Default.Add, "Add Country")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.countries) { country ->
                        CountryCard(
                            country = country,
                            onEditClick = {
                                selectedCountry = country
                                showAddCountryDialog = true
                            },
                            onDeleteClick = {
                                scope.launch {
                                    viewModel.onEvent(LocationManagerEvent.DeleteCountry(country.id))
                                }
                            },
                            onAddStateClick = {
                                selectedCountry = country
                                showAddStateDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Country Dialog
    if (showAddCountryDialog) {
        LocationDialog(
            title = if (selectedCountry == null) "Add Country" else "Edit Country",
            onDismiss = {
                showAddCountryDialog = false
                selectedCountry = null
            },
            onSave = { name ->
                val country = Country(
                    id = selectedCountry?.id ?: System.currentTimeMillis().toInt(),
                    name = name,
                    states = emptyList(),
                    iso3 = "",
                    iso2 = "",
                    numericCode = "",
                    phoneCode = "",
                    capital = "",
                    currency = "",
                    currencyName = "",
                    currencySymbol = "",
                    tld = "",
                    native = "",
                    region = "",
                    regionId = 0,
                    subregion = "",
                    subregionId = 0,
                    nationality = "",
                    latitude = "",
                    longitude = "",
                    emoji = "",
                    emojiU = ""
                )
                if (selectedCountry == null) {
                    viewModel.onEvent(LocationManagerEvent.AddCountry(country))
                } else {
                    viewModel.onEvent(LocationManagerEvent.UpdateCountry(country))
                }
                showAddCountryDialog = false
                selectedCountry = null
            },
            initialValue = selectedCountry?.name
        )
    }

    // Add/Edit State Dialog
    if (showAddStateDialog && selectedCountry != null) {
        LocationDialog(
            title = if (selectedState == null) "Add State" else "Edit State",
            onDismiss = {
                showAddStateDialog = false
                selectedState = null
            },
            onSave = { name ->
                val state = State(
                    id = selectedState?.id ?: System.currentTimeMillis().toInt(),
                    name = name,
                    stateCode = "",
                    latitude = "",
                    longitude = "",
                    type = "",
                    cities = emptyList()
                )
                if (selectedState == null) {
                    viewModel.onEvent(LocationManagerEvent.AddState(selectedCountry!!.id, state))
                } else {
                    viewModel.onEvent(LocationManagerEvent.UpdateState(selectedCountry!!.id, state))
                }
                showAddStateDialog = false
                selectedState = null
            },
            initialValue = selectedState?.name
        )
    }

    if (showAddCityDialog && selectedState != null) {
        LocationDialog(
            title = "Add City",
            onDismiss = { showAddCityDialog = false },
            onSave = { name ->
                val city = City(
                    id = System.currentTimeMillis().toInt(),
                    name = name,
                    latitude = "",
                    longitude = ""
                )
                viewModel.onEvent(LocationManagerEvent.AddCity(selectedState!!.id, city))
                showAddCityDialog = false
            }
        )
    }
}

@Composable
fun CountryCard(
    country: Country,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddStateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                    IconButton(onClick = onAddStateClick) {
                        Icon(Icons.Default.Add, "Add State")
                    }
                }
            }
            Text(
                text = "States: ${country.states.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> LocationDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = when (selectedItem) {
                is Country -> selectedItem.name
                is State -> selectedItem.name
                is City -> selectedItem.name
                else -> ""
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (item) {
                                is Country -> item.name
                                is State -> item.name
                                is City -> item.name
                                else -> ""
                            }
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun LocationDialog(
    title: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    initialValue: String? = null
) {
    var name by remember { mutableStateOf(initialValue ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(name) },
                        enabled = name.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

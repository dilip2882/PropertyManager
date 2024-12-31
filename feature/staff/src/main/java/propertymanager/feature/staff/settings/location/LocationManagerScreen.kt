package propertymanager.feature.staff.settings.location

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
                title = { Text("Location Manager") },
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

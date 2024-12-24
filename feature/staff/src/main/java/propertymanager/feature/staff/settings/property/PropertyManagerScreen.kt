package propertymanager.feature.staff.settings.property

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Property
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun PropertyManagerScreen(
    onNavigateUp: () -> Unit,
    viewModel: PropertyViewModel = hiltViewModel()
) {
    val propertiesResponse by viewModel.propertiesResponse.collectAsState()
    val operationResponse by viewModel.operationResponse.collectAsState()

    var showAddPropertyFlow by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPropertyFlow = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Property")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("My Properties") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (propertiesResponse) {
                is Response.Loading -> LoadingScreen()
                is Response.Success -> {
                    val properties = (propertiesResponse as Response.Success<List<Property>>).data
                    if (properties.isEmpty()) {
                        EmptyPropertyList(onAddClick = { showAddPropertyFlow = true })
                    } else {
                        PropertyList(
                            properties = properties,
                            onDeleteProperty = { viewModel.deleteProperty(it.id) }
                        )
                    }
                }
                is Response.Error -> ErrorView(
                    message = (propertiesResponse as Response.Error).message,
                    onRetry = { /* Add retry logic */ }
                )
            }
        }

        // Show add property flow dialog/sheet
        if (showAddPropertyFlow) {
            AddPropertyFlow(
                onDismiss = { showAddPropertyFlow = false },
                onPropertyAdded = { property ->
                    viewModel.addProperty(property)
                    showAddPropertyFlow = false
                }
            )
        }

        // Show operation response
        when (operationResponse) {
            is Response.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is Response.Error -> {
                val context = LocalContext.current
                LaunchedEffect(operationResponse) {
                    Toast.makeText(
                        context,
                        (operationResponse as Response.Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> {}
        }
    }
}

@Composable
fun EmptyPropertyList(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "No Properties Added Yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Add your first property to get started",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
            onClick = onAddClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Property")
        }
    }
}

@Composable
fun PropertyList(
    properties: List<Property>,
    onDeleteProperty: (Property) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(properties) { property ->
            PropertyCard(
                property = property,
                onDelete = { onDeleteProperty(property) }
            )
        }
    }
}

@Composable
fun PropertyCard(
    property: Property,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${property.address.society}, ${property.address.building}-${property.address.flatNo}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${property.address.city}, ${property.address.country}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (property.address.ownershipType != null) {
                        Text(
                            text = property.address.ownershipType.name.replace('_', ' ').lowercase(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyFlow(
    onDismiss: () -> Unit,
    onPropertyAdded: (Property) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedCountry by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    var selectedSociety by remember { mutableStateOf("") }
    var selectedBuilding by remember { mutableStateOf("") }
    var selectedFlatNo by remember { mutableStateOf("") }
    var selectedOwnershipType by remember { mutableStateOf<Property.OwnershipType?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (currentStep) {
                0 -> CountrySelectionStep(
                    onCountrySelected = {
                        selectedCountry = it
                        currentStep++
                    }
                )
                1 -> CitySelectionStep(
                    country = selectedCountry,
                    onCitySelected = {
                        selectedCity = it
                        currentStep++
                    }
                )
                2 -> PropertyDetailsStep(
                    onDetailsSubmitted = { society, building, flatNo, ownershipType ->
                        selectedSociety = society
                        selectedBuilding = building
                        selectedFlatNo = flatNo
                        selectedOwnershipType = ownershipType

                        val newProperty = Property(
                            address = Property.Address(
                                country = selectedCountry,
                                city = selectedCity,
                                society = selectedSociety,
                                building = selectedBuilding,
                                flatNo = selectedFlatNo,
                                ownershipType = selectedOwnershipType!!
                            )
                        )
                        onPropertyAdded(newProperty)
                    }
                )
            }
        }
    }
}

@Composable
fun CountrySelectionStep(onCountrySelected: (String) -> Unit) {
    Column {
        Text(
            text = "Select Country",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val countries = remember {
            listOf(
                "India", "Kenya", "United Arab Emirates", "Philippines",
                "Canada", "Malaysia", "Nigeria", "United States of America",
                "Kingdom Of Saudi Arabia", "Bahrain", "Nepal", "Caribbean Islands"
            )
        }

        LazyColumn {
            items(countries) { country ->
                ListItem(
                    headlineContent = { Text(country) },
                    modifier = Modifier.clickable { onCountrySelected(country) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun CitySelectionStep(
    country: String,
    onCitySelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Select City",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Example cities for India
        val cities = remember {
            listOf(
                "Mumbai", "Delhi NCR", "Bangalore", "Pune", "Chennai",
                "Hyderabad", "Kolkata", "Ahmedabad", "Kochi"
            )
        }

        LazyColumn {
            items(cities) { city ->
                ListItem(
                    headlineContent = { Text(city) },
                    modifier = Modifier.clickable { onCitySelected(city) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun PropertyDetailsStep(
    onDetailsSubmitted: (String, String, String, Property.OwnershipType) -> Unit
) {
    var society by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var flatNo by remember { mutableStateOf("") }
    var ownershipType by remember { mutableStateOf<Property.OwnershipType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Property Details",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = society,
            onValueChange = { society = it },
            label = { Text("Society") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = building,
            onValueChange = { building = it },
            label = { Text("Building") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = flatNo,
            onValueChange = { flatNo = it },
            label = { Text("Flat No.") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text(
            text = "You are",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Column {
            Property.OwnershipType.values().forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = ownershipType == type,
                            onClick = { ownershipType = type }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = ownershipType == type,
                        onClick = { ownershipType = type }
                    )
                    Text(
                        text = type.name.replace('_', ' ').lowercase(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                if (society.isNotEmpty() && building.isNotEmpty() &&
                    flatNo.isNotEmpty() && ownershipType != null
                ) {
                    onDetailsSubmitted(society, building, flatNo, ownershipType!!)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = society.isNotEmpty() && building.isNotEmpty() &&
                flatNo.isNotEmpty() && ownershipType != null
        ) {
            Text("Add Property")
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Retry")
        }
    }
}

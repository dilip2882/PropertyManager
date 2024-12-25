package propertymanager.feature.staff.settings.property

import android.widget.Toast
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import propertymanager.feature.staff.R
import propertymanager.feature.staff.settings.property.componenets.EmptyPropertyList
import propertymanager.feature.staff.settings.property.componenets.PropertyList

@Composable
fun PropertyManagerScreen(
    viewModel: PropertyViewModel,
    onNavigateToAddProperty: () -> Unit
) {
    val propertiesResponse by viewModel.propertiesResponse.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Properties") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProperty,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add Property")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (propertiesResponse) {
                is Response.Loading -> {
                    LoadingScreen()
                }
                is Response.Success -> {
                    val properties = (propertiesResponse as Response.Success<List<Property>>).data
                    if (properties.isEmpty()) {
                        EmptyPropertyList(onAddClick = onNavigateToAddProperty)
                    } else {
                        PropertyList(
                            properties = properties,
                            onEditProperty = { /* edit */ },
                            onDeleteProperty = { viewModel.deleteProperty(propertyId = "") }
                        )
                    }
                }
                is Response.Error -> {
                    Text(
                        text = (propertiesResponse as Response.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun SelectCountryScreen(
    onCountrySelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val countries = listOf(
        "India", "Kenya", "United Arab Emirates", "Philippines", "Canada",
        "Malaysia", "Nigeria", "United States of America", "Kingdom Of Saudi Arabia",
        "Bahrain", "Nepal", "Caribbean Islands"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                title = { Text("Select Country") },
                actions = {
                    IconButton(onClick = { /* Handle search */ }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(countries) { country ->
                TextButton(
                    onClick = { onCountrySelected(country) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = country,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
                Divider()
            }
        }
    }
}

@Composable
fun SelectCityScreen(
    selectedCountry: String,
    onCitySelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val majorCities = listOf(
        Triple("Bangalore", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Mumbai", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Delhi NCR", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Pune", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Chennai", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Hyderabad", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Ahmedabad", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Kolkata", Icons.Default.LocationCity, R.drawable.home_filled),
        Triple("Kochi", Icons.Default.LocationCity, R.drawable.home_filled)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                title = { Text("Select City") },
                actions = {
                    IconButton(onClick = { /* Handle search */ }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(16.dp)
            ) {
                items(majorCities) { (city, _, _) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationCity,
                            contentDescription = city,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Divider()

            LazyColumn {
                items(listOf(
                    "Indirapuram", "Abu Road", "Adchini", "Adharwadi Jail road",
                    "Adilabad", "Adityapur", "Agra", "Ahmedabad", "Ahmedabad District"
                )) { city ->
                    TextButton(
                        onClick = { onCitySelected(city) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    viewModel: PropertyViewModel,
    selectedCountry: String,
    selectedCity: String,
    onNavigateBack: () -> Unit,
    onPropertyAdded: () -> Unit
) {
    var society by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var flatNo by remember { mutableStateOf("") }
    var ownershipType by remember { mutableStateOf<Property.OwnershipType?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                title = { Text("Add Home") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Country dropdown (disabled, showing selection)
            OutlinedTextField(
                value = selectedCountry,
                onValueChange = { },
                label = { Text("Country") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") }
            )

            // City dropdown (disabled, showing selection)
            OutlinedTextField(
                value = selectedCity,
                onValueChange = { },
                label = { Text("City") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") }
            )

            // Society input
            OutlinedTextField(
                value = society,
                onValueChange = { society = it },
                label = { Text("Society") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.Search, "search") }
            )

            // Building input
            OutlinedTextField(
                value = building,
                onValueChange = { building = it },
                label = { Text("Building") },
                modifier = Modifier.fillMaxWidth()
            )

            // Flat No input
            OutlinedTextField(
                value = flatNo,
                onValueChange = { flatNo = it },
                label = { Text("Flat No.") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Add Button
            Button(
                onClick = {
                    if (ownershipType != null) {
                        val property = Property(
                            address = Property.Address(
                                country = selectedCountry,
                                city = selectedCity,
                                society = society,
                                building = building,
                                flatNo = flatNo,
                                ownershipType = ownershipType!!
                            )
                        )
                        viewModel.addProperty(property)
                        onPropertyAdded()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = society.isNotBlank() && building.isNotBlank() &&
                    flatNo.isNotBlank() && ownershipType != null
            ) {
                Text("Add Flat/Villa")
            }
        }
    }
}

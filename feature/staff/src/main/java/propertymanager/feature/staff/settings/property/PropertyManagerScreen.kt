package propertymanager.feature.staff.settings.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Property
import propertymanager.feature.staff.settings.property.componenets.EmptyPropertyList
import propertymanager.feature.staff.settings.property.componenets.PropertyList
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun PropertyManagerScreen(
    viewModel: PropertyViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddProperty: () -> Unit,
) {
    val propertiesResponse by viewModel.propertiesResponse.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Properties") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProperty,
//                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Default.Add, "Add Property")
            }
        },
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
                            onDeleteProperty = { viewModel.deleteProperty(propertyId = "") },
                        )
                    }
                }

                is Response.Error -> {
                    Text(
                        text = (propertiesResponse as Response.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    viewModel: PropertyViewModel,
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit,
    onPropertyAdded: () -> Unit,
) {
    val selectedCountry by locationViewModel.selectedCountry.collectAsState()
    val selectedState by locationViewModel.selectedState.collectAsState()
    val selectedCity by locationViewModel.selectedCity.collectAsState()

    var society by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var flatNo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                title = { Text("Add Home") },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Country
            OutlinedTextField(
                value = selectedCountry?.name ?: "",
                onValueChange = { },
                label = { Text("Country") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") },
            )

            // State
            OutlinedTextField(
                value = selectedState?.name ?: "",
                onValueChange = { },
                label = { Text("State") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") },
            )

            // City
            OutlinedTextField(
                value = selectedCity?.name ?: "",
                onValueChange = { },
                label = { Text("City") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") },
            )

            // Society
            OutlinedTextField(
                value = society,
                onValueChange = { society = it },
                label = { Text("Society") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.Search, "search") },
            )

            // Building
            OutlinedTextField(
                value = building,
                onValueChange = { building = it },
                label = { Text("Building") },
                modifier = Modifier.fillMaxWidth(),
            )

            // Flat No
            OutlinedTextField(
                value = flatNo,
                onValueChange = { flatNo = it },
                label = { Text("Flat No.") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedCountry != null && selectedCity != null) {
                        val property = Property(
                            address = Property.Address(
                                country = selectedCountry!!.name,
                                state = selectedState?.name ?: "",
                                city = selectedCity!!.name,
                                society = society,
                                building = building,
                                flatNo = flatNo,
                            ),
                        )
                        viewModel.addProperty(property)
                        onPropertyAdded()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = society.isNotBlank() && building.isNotBlank() &&
                    flatNo.isNotBlank() && selectedCountry != null && selectedCity != null,
            ) {
                Text("Add Flat")
            }
        }
    }
}

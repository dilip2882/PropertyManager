package propertymanager.feature.staff.settings.property

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.Property
import propertymanager.feature.staff.settings.location.LocationDropdown
import propertymanager.feature.staff.settings.location.LocationEvent
import propertymanager.feature.staff.settings.location.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    viewModel: PropertyViewModel,
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit,
    onPropertyAdded: () -> Unit,
) {
    val locationState by locationViewModel.state.collectAsState()
    var society by remember { mutableStateOf("") }

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
            // Country Dropdown
            LocationDropdown(
                label = "Select Country",
                items = locationState.countries,
                selectedItem = locationState.selectedCountry,
                onItemSelected = { country ->
                    locationViewModel.onEvent(LocationEvent.SelectCountry(country))
                },
            )

            // State Dropdown
            AnimatedVisibility(visible = locationState.selectedCountry != null) {
                LocationDropdown(
                    label = "Select State",
                    items = locationState.states,
                    selectedItem = locationState.selectedState,
                    onItemSelected = { state ->
                        locationViewModel.onEvent(LocationEvent.SelectState(state))
                    },
                )
            }

            // City Dropdown
            AnimatedVisibility(visible = locationState.selectedState != null) {
                LocationDropdown(
                    label = "Select City",
                    items = locationState.cities,
                    selectedItem = locationState.selectedCity,
                    onItemSelected = { city ->
                        locationViewModel.onEvent(LocationEvent.SelectCity(city))
                    },
                )
            }

            // Society
            OutlinedTextField(
                value = society,
                onValueChange = { society = it },
                label = { Text("Society") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    locationState.selectedCountry?.let { country ->
                        locationState.selectedCity?.let { city ->
                            val property = Property(
                                address = Property.Address(
                                    country = country.name,
                                    state = locationState.selectedState?.name ?: "",
                                    city = city.name,
                                    society = society,
                                ),
                            )
                            viewModel.addProperty(property)
                            onPropertyAdded()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = locationState.selectedCountry != null &&
                    locationState.selectedCity != null && society.isNotBlank(),
            ) {
                Text("Add Society")
            }
        }
    }
}

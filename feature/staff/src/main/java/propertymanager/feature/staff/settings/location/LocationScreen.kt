package propertymanager.feature.staff.settings.location

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LocationScreen(
    viewModel: LocationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location") },
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
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Country Dropdown
            LocationDropdown(
                label = "Select Country",
                items = state.countries,
                selectedItem = state.selectedCountry,
                onItemSelected = { country ->
                    viewModel.onEvent(LocationEvent.SelectCountry(country))
                },
            )

            // State Dropdown
            AnimatedVisibility(visible = state.selectedCountry != null) {
                LocationDropdown(
                    label = "Select State",
                    items = state.states,
                    selectedItem = state.selectedState,
                    onItemSelected = { state ->
                        viewModel.onEvent(LocationEvent.SelectState(state))
                    },
                )
            }

            // City Dropdown
            AnimatedVisibility(visible = state.selectedState != null) {
                LocationDropdown(
                    label = "Select City",
                    items = state.cities,
                    selectedItem = state.selectedCity,
                    onItemSelected = { city ->
                        viewModel.onEvent(LocationEvent.SelectCity(city))
                    },
                )
            }
        }
    }
}

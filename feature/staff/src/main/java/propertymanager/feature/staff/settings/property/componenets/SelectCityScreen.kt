package propertymanager.feature.staff.settings.property.componenets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import propertymanager.core.network.model.CityData
import propertymanager.feature.staff.settings.property.LocationViewModel
import propertymanager.presentation.screens.EmptyScreen

@Composable
fun SelectCityScreen(
    viewModel: LocationViewModel,
    onCitySelected: (CityData) -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedState by viewModel.selectedState.collectAsState()

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
                    IconButton(onClick = { /* search */ }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { padding ->
        selectedState?.let { state ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(state.cities) { city ->
                    TextButton(
                        onClick = {
                            viewModel.selectCity(city)
                            onCitySelected(city)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = city.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                    Divider()
                }
            }
        } ?: run {
            EmptyScreen(message = "Please select a state first")
        }
    }
}


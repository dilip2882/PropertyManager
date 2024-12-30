package propertymanager.feature.staff.settings.property.componenets

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import propertymanager.core.network.model.StateData
import propertymanager.feature.staff.settings.property.LocationViewModel
import propertymanager.presentation.screens.EmptyScreen

@Composable
fun SelectStateScreen(
    viewModel: LocationViewModel,
    onStateSelected: (StateData) -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedCountry by viewModel.selectedCountry.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("SelectStateScreen", "Selected country: ${selectedCountry?.name}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                title = { Text("Select State") },
                actions = {
                    IconButton(onClick = { /* search */ }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { padding ->
        selectedCountry?.let { country ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(country.states) { state ->
                    TextButton(
                        onClick = {
                            viewModel.selectState(state)
                            onStateSelected(state)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.name,
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
            EmptyScreen(message = "Please select a country first")
        }
    }
}

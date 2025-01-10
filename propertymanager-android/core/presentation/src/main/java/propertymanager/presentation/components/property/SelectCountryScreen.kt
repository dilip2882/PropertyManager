package propertymanager.presentation.components.property

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun SelectCountryScreen(
    viewModel: LocationViewModel,
    onNavigateToState: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                title = { Text("Select Country") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search country") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") }
            )

            if (state.isLoading) {
                LoadingScreen()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        state.countries.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }
                    ) { country ->
                        ListItem(
                            headlineContent = { Text(country.name) },
                            leadingContent = { Text(country.emoji) },
                            modifier = Modifier.clickable {
                                viewModel.onEvent(LocationEvent.SelectCountry(country))
                                onNavigateToState()
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

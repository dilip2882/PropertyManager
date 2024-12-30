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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.propertymanager.common.utils.Response
import propertymanager.core.network.model.CountryData
import propertymanager.feature.staff.settings.property.LocationViewModel
import propertymanager.presentation.screens.EmptyScreen
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun SelectCountryScreen(
    viewModel: LocationViewModel,
    onCountrySelected: (CountryData) -> Unit,
    onNavigateBack: () -> Unit
) {
    val countriesResponse by viewModel.countries.collectAsState()

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
                    IconButton(onClick = { /* search */ }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        }
    ) { padding ->
        when (val response = countriesResponse) {
            is Response.Loading -> LoadingScreen()
            is Response.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(response.data) { country ->
                        TextButton(
                            onClick = {
                                viewModel.selectCountry(country)
                                onCountrySelected(country)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = country.name,
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
            is Response.Error -> {
                EmptyScreen(message = response.message)
                Log.d("TAG", "SelectCountryScreen: $response.message")
            }
        }
    }
}

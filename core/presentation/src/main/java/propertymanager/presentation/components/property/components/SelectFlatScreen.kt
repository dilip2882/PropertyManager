package propertymanager.presentation.components.property.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.propertymanager.domain.model.Property
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.property.PropertyViewModel

@Composable
fun SelectFlatScreen(
    locationViewModel: LocationViewModel,
    propertyViewModel: PropertyViewModel,
    buildingType: Property.Building,
    parentId: Int,
    onNavigateBack: () -> Unit
) {
    val state by locationViewModel.state.collectAsState()

    LaunchedEffect(parentId, buildingType) {
        // Load flats based on building type and parent ID
        when (buildingType) {
            Property.Building.BLOCK -> locationViewModel.loadFlatsForBlock(parentId)
            Property.Building.TOWER -> locationViewModel.loadFlatsForTower(parentId)
            else -> locationViewModel.loadFlatsForSociety(parentId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Flat") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
            items(state.flats) { flat ->
                FlatItem(
                    flat = flat,
                    onClick = {
                        // Update
                        locationViewModel.onEvent(LocationEvent.SelectFlat(flat)) // using this
//                        propertyViewModel.updateSelectedLocation(flat = flat) // not using currently
                        onNavigateBack()
                    }
                )
            }
        }
    }
} 

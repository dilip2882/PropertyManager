package propertymanager.presentation.components.property

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.location.Flat
import propertymanager.presentation.components.location.LocationState
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.location.UiEvent
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun SelectFlatScreen(
    parentId: Int,
    locationViewModel: LocationViewModel = hiltViewModel(),
    onFlatSelected: (Flat) -> Unit,
    onNavigateBack: () -> Unit,
    locationState: LocationState
) {
    val state by locationViewModel.state.collectAsState()
    val context = LocalContext.current

    // Load flats based on parent ID

    LaunchedEffect(parentId) {
        // Check if parent is in blocks or towers
        val isBlock = locationState.blocks.any { it.id == parentId }
        if (isBlock) {
            locationViewModel.loadFlatsForBlock(parentId)
        } else {
            locationViewModel.loadFlatsForTower(parentId)
        }
    }
    // Collect UI events
    LaunchedEffect(true) {
        locationViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
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
        if (state.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(state.flats) { flat ->
                    FlatItem(
                        flat = flat,
                        onClick = { onFlatSelected(flat) }
                    )
                }
            }
        }
    }
} 

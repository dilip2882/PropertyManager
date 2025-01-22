package propertymanager.feature.staff.settings.property

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.Property
import propertymanager.feature.staff.settings.property.componenets.PropertyList
import propertymanager.presentation.components.property.PropertyEvent
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.theme.PropertyManagerIcons

@Composable
fun PropertyManagerScreen(
    onNavigateToAddProperty: () -> Unit,
    onNavigateToEditProperty: (Property) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PropertyViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(PropertyManagerIcons.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddProperty) {
                        Icon(PropertyManagerIcons.Add, "Add Property")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                PropertyList(
                    properties = state.properties,
                    onEditProperty = { property ->
                        onNavigateToEditProperty(property)
                    },
                    onDeleteProperty = { property ->
                        viewModel.onEvent(PropertyEvent.DeleteProperty(property))
                    },
                )
            }

            // Error Handling
            state.error?.let { error ->
                LaunchedEffect(error) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    viewModel.onEvent(PropertyEvent.ClearError)
                }
            }
        }
    }
}

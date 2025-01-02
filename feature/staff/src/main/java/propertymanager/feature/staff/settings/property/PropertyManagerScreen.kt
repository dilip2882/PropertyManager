package propertymanager.feature.staff.settings.property

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                            onEditProperty = {
                                viewModel.updateProperty(it)
                            },
                            onDeleteProperty = { property ->
                                viewModel.deleteProperty(propertyId = property.id)
                            },
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

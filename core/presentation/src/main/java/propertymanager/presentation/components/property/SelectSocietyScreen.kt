package propertymanager.presentation.components.property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.location.Society
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSocietyScreen(
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit,
    onSocietySelected: (Society) -> Unit
) {
    val locationState by locationViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                title = { Text("Select Society") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            locationState.societies.forEach { society ->
                SocietyItem(
                    society = society,
                    onClick = { 
                        locationViewModel.onEvent(LocationEvent.SelectSociety(society))
                        onSocietySelected(society)
                    }
                )
            }
        }
    }
}

@Composable
private fun SocietyItem(
    society: Society,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = society.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select"
            )
        }
    }
} 

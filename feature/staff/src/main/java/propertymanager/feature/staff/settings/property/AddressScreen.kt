package propertymanager.feature.staff.settings.property

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Property
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun AddressScreen(
    propertyId: String,
    viewModel: PropertyViewModel,
    onNavigateBack: () -> Unit,
) {
    val addressState = remember { mutableStateOf(Property.Address()) }
    val operationResponse by viewModel.operationResponse.collectAsState()

    when (operationResponse) {
        is Response.Loading -> LoadingScreen()
        is Response.Error -> {
            val errorMessage = (operationResponse as Response.Error).message
            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
        }

        is Response.Success -> Unit
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Address") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Country
            OutlinedTextField(
                value = addressState.value.country,
                onValueChange = { addressState.value = addressState.value.copy(country = it) },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth(),
            )

            // State
            OutlinedTextField(
                value = addressState.value.state,
                onValueChange = { addressState.value = addressState.value.copy(state = it) },
                label = { Text("State") },
                modifier = Modifier.fillMaxWidth(),
            )

            // City
            OutlinedTextField(
                value = addressState.value.city,
                onValueChange = { addressState.value = addressState.value.copy(city = it) },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
            )

            // Society
            OutlinedTextField(
                value = addressState.value.society,
                onValueChange = { addressState.value = addressState.value.copy(society = it) },
                label = { Text("Society") },
                modifier = Modifier.fillMaxWidth(),
            )

            // Building
            OutlinedTextField(
                value = addressState.value.building,
                onValueChange = { addressState.value = addressState.value.copy(building = it) },
                label = { Text("Building") },
                modifier = Modifier.fillMaxWidth(),
            )

            // Flat Number
            OutlinedTextField(
                value = addressState.value.flatNo,
                onValueChange = { addressState.value = addressState.value.copy(flatNo = it) },
                label = { Text("Flat No") },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = {
                        viewModel.addAddress(propertyId, addressState.value)
                    },
                ) {
                    Text("Add Address")
                }

                Button(
                    onClick = {
                        viewModel.deleteAddress(propertyId)
                        addressState.value = Property.Address()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                ) {
                    Text("Delete Address")
                }
            }
        }
    }
}

package propertymanager.feature.staff

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.model.PropertyStatus
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import propertymanager.presentation.components.location.LocationEvent
import propertymanager.presentation.components.location.LocationState
import propertymanager.presentation.components.location.LocationViewModel
import propertymanager.presentation.components.property.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffFlatScreen(
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    staffViewModel: StaffViewModel = hiltViewModel(),
    onNavigateToHome: (Property) -> Unit
) {
    val propertyState by propertyViewModel.state.collectAsState()
    val locationState by locationViewModel.state.collectAsState()
    val maintenanceCountMap by staffViewModel.maintenanceRequestsMap.collectAsState()
    val context = LocalContext.current
    
    var searchQuery by remember { mutableStateOf("") }
    var showLocationSheet by remember { mutableStateOf(false) }
    var selectedLocationType by remember { mutableStateOf(LocationType.COUNTRY) }

    LaunchedEffect(Unit) {
        staffViewModel.fetchAssignedRequests("")  // Pass the appropriate staffId
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search properties...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // TopBar with Location Selectors
           /* TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Location Selectors
                        LocationSelector(
                            title = "Country",
                            selected = locationState.selectedCountry?.name ?: "Select Country",
                            onClick = {
                                selectedLocationType = LocationType.COUNTRY
                                showLocationSheet = true
                            },
                            modifier = Modifier.weight(1f)
                        )

                        LocationSelector(
                            title = "State",
                            selected = locationState.selectedState?.name ?: "Select State",
                            onClick = {
                                if (locationState.selectedCountry != null) {
                                    selectedLocationType = LocationType.STATE
                                    showLocationSheet = true
                                } else {
                                    Toast.makeText(context, "Please select country first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        LocationSelector(
                            title = "City",
                            selected = locationState.selectedCity?.name ?: "Select City",
                            onClick = {
                                if (locationState.selectedState != null) {
                                    selectedLocationType = LocationType.CITY
                                    showLocationSheet = true
                                } else {
                                    Toast.makeText(context, "Please select state first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        LocationSelector(
                            title = "Society",
                            selected = locationState.selectedSociety?.name ?: "Select Society",
                            onClick = {
                                if (locationState.selectedCity != null) {
                                    selectedLocationType = LocationType.SOCIETY
                                    showLocationSheet = true
                                } else {
                                    Toast.makeText(context, "Please select city first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        LocationSelector(
                            title = "Flat",
                            selected = locationState.selectedFlat?.number?.toString() ?: "Select Flat",
                            onClick = {
                                if (locationState.selectedSociety != null) {
                                    selectedLocationType = LocationType.FLAT
                                    showLocationSheet = true
                                } else {
                                    Toast.makeText(context, "Please select society first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            )*/

            // Properties List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (propertyState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (propertyState.properties.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No properties found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(propertyState.properties) { property ->
                        // Only show properties that match the location filters
                        val matchesLocation = when {
                            locationState.selectedFlat != null -> 
                                property.address.flatNo == locationState.selectedFlat!!.number &&
                                property.address.society == locationState.selectedSociety?.name
                            locationState.selectedSociety != null -> 
                                property.address.society == locationState.selectedSociety!!.name
                            locationState.selectedCity != null -> 
                                property.address.city == locationState.selectedCity!!.name
                            locationState.selectedState != null -> 
                                property.address.state == locationState.selectedState!!.name
                            locationState.selectedCountry != null -> 
                                property.address.country == locationState.selectedCountry!!.name
                            else -> true
                        }

                        val matchesSearch = if (searchQuery.isNotEmpty()) {
                            property.address.toString().contains(searchQuery, ignoreCase = true)
                        } else true

                        if (matchesLocation && matchesSearch) {
                            PropertyCard(
                                property = property,
                                maintenanceCount = maintenanceCountMap[property.id] ?: 0,
                                onPropertyClick = { onNavigateToHome(property) }
                            )
                        }
                    }
                }
            }
        }

        // Location Selection Sheet
        if (showLocationSheet) {
            ModalBottomSheet(
                onDismissRequest = { showLocationSheet = false },
                contentWindowInsets = { WindowInsets(0) }
            ) {
                LocationSelectionSheet(
                    locationType = selectedLocationType,
                    locationState = locationState,
                    onLocationSelected = { type, location ->
                        when (type) {
                            LocationType.COUNTRY -> {
                                val country = location as Country
                                locationViewModel.onEvent(LocationEvent.SelectCountry(country))
                            }
                            LocationType.STATE -> {
                                val state = location as State
                                locationViewModel.onEvent(LocationEvent.SelectState(state))
                            }
                            LocationType.CITY -> {
                                val city = location as City
                                locationViewModel.onEvent(LocationEvent.SelectCity(city))
                            }
                            LocationType.SOCIETY -> {
                                val society = location as Society
                                locationViewModel.onEvent(LocationEvent.SelectSociety(society))
                            }
                            LocationType.FLAT -> {
                                val flat = location as Flat
                                locationViewModel.onEvent(LocationEvent.SelectFlat(flat))
                            }
                        }
                        showLocationSheet = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LocationSelector(
    title: String,
    selected: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selected,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select $title"
            )
        }
    }
}

enum class LocationType {
    COUNTRY,
    STATE,
    CITY,
    SOCIETY,
    FLAT
}

@Composable
private fun LocationSelectionSheet(
    locationType: LocationType,
    locationState: LocationState,
    onLocationSelected: (LocationType, Any) -> Unit,
    locationViewModel: LocationViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val viewModelState by locationViewModel.state.collectAsState()

    LaunchedEffect(locationType, locationState) {
        when (locationType) {
            LocationType.STATE -> locationState.selectedCountry?.let { country ->
                locationViewModel.onEvent(LocationEvent.GetStatesForCountry(country.id))
            }
            LocationType.CITY -> locationState.selectedState?.let { state ->
                locationViewModel.onEvent(LocationEvent.GetCitiesForState(state.id))
            }
            LocationType.SOCIETY -> locationState.selectedCity?.let { city ->
                locationViewModel.onEvent(LocationEvent.GetSocietiesForCity(city.id))
            }
            LocationType.FLAT -> locationState.selectedSociety?.let { society ->
                locationViewModel.onEvent(LocationEvent.GetFlatsForSociety(society.id))
            }
            LocationType.COUNTRY -> {
                // Countries should be loaded by default in ViewModel
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search ${locationType.name.lowercase()}...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            val items = when (locationType) {
                LocationType.COUNTRY -> viewModelState.countries
                LocationType.STATE -> viewModelState.states
                LocationType.CITY -> viewModelState.cities
                LocationType.SOCIETY -> viewModelState.societies
                LocationType.FLAT -> viewModelState.flats
            }.filter { location ->
                when (location) {
                    is Country -> location.name.contains(searchQuery, ignoreCase = true)
                    is State -> location.name.contains(searchQuery, ignoreCase = true)
                    is City -> location.name.contains(searchQuery, ignoreCase = true)
                    is Society -> location.name.contains(searchQuery, ignoreCase = true)
                    is Flat -> location.number.toString().contains(searchQuery, ignoreCase = true)
                    else -> false
                }
            }

            if (items.isEmpty()) {
                item {
                    Text(
                        text = "No ${locationType.name.lowercase()}s found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            } else {
                items(items) { location ->
                    val locationName = when (location) {
                        is Country -> location.name
                        is State -> location.name
                        is City -> location.name
                        is Society -> location.name
                        is Flat -> "Flat ${location.number}"
                        else -> ""
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (location) {
                                    is Country -> onLocationSelected(LocationType.COUNTRY, location)
                                    is State -> onLocationSelected(LocationType.STATE, location)
                                    is City -> onLocationSelected(LocationType.CITY, location)
                                    is Society -> onLocationSelected(LocationType.SOCIETY, location)
                                    is Flat -> onLocationSelected(LocationType.FLAT, location)
                                }
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = locationName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun PropertyCard(
    property: Property,
    maintenanceCount: Int = 0,
    onPropertyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPropertyClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = buildString {
                            append(property.address.building)
                            if (property.address.flatNo != null) {
                                append(" ")
                                append(property.address.flatNo)
                            }
                            append("\n")
                            append(property.address.society)
                            append(", ")
                            append(property.address.city)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "$maintenanceCount",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = property.address.state,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package propertymanager.feature.tenant.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Property
import propertymanager.presentation.components.property.PropertyState
import propertymanager.presentation.components.property.PropertyViewModel
import propertymanager.presentation.components.user.UserEvent
import propertymanager.presentation.components.user.UserViewModel
import propertymanager.presentation.screens.LoadingScreen
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun TenantHomeScreen(
    propertyViewModel: PropertyViewModel,
    userViewModel: UserViewModel = hiltViewModel(),
    onNavigateToAddProperty: () -> Unit,
    modifier: Modifier = Modifier
) {
    val propertyState by propertyViewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()
    var showDropdown by remember { mutableStateOf(false) }

    val selectedProperty = remember(propertyState.properties, userState.user?.selectedPropertyId) {
        propertyState.properties.find { it.id == userState.user?.selectedPropertyId }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDropdown = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar/Initial
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Z", // First letter of user
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedProperty?.let {
                                    with(it.address) {
                                        "Block-${building ?: "1"} ${flatNo}"
                                    }
                                } ?: "Select Location",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }
                    }
                },
                actions = {
                    // Search Icon
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                    // Notification Icon
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.Notifications, "Notifications")
                    }
                    // Chat Icon
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Chat, "Chat")
                    }
                    // Profile Icon
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp)
                            .background(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "N", // User's initial
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            )

            // Main content
            if (showDropdown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { 
                            showDropdown = false 
                        }
                ) {
                    // Property List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* Prevent clicks from reaching the background */ }
                    ) {
                        items(propertyState.properties) { property ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        userState.user?.userId?.let { userId ->
                                            userViewModel.onEvent(UserEvent.SelectProperty(property.id))
                                        }
                                        showDropdown = false
                                    }
                            ) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = with(property.address) {
                                                buildString {
                                                    append("Block-${building ?: "1"}-$flatNo, ")
                                                    append(society)
                                                }
                                            }
                                        )
                                    },
                                    trailingContent = if (property.id == userState.user?.selectedPropertyId) {
                                        {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.error,
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "✔",
                                                    color = MaterialTheme.colorScheme.onError,
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                        }
                                    } else null
                                )
                                HorizontalDivider()
                            }
                        }

                        item {
                            ListItem(
                                headlineContent = { Text("Add Flat/Villa/Office") },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add property",
                                        modifier = Modifier
                                            .background(
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = CircleShape
                                            )
                                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                            .padding(8.dp)
                                    )
                                },
                                modifier = Modifier.clickable {
                                    showDropdown = false
                                    onNavigateToAddProperty()
                                }
                            )
                        }
                    }
                }
            }

            if (propertyState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            propertyState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    LaunchedEffect(userState.user?.selectedPropertyId) {
        userState.user?.selectedPropertyId?.let { selectedId ->
            propertyViewModel.loadProperties()
        }
    }
}




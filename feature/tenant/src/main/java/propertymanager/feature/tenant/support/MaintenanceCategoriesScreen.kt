package propertymanager.feature.tenant.support

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Category

@Composable
fun MaintenanceCategoriesScreen(
    onNavigateUp: () -> Unit,
    onCategorySelected: (String, String) -> Unit,
) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val categoriesResponse by viewModel.categoriesResponse.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf<String?>(null) }

    // Filter categories based on search query
    val filteredCategories = remember(categoriesResponse, searchQuery) {
        when (categoriesResponse) {
            is Response.Success -> {
                val categories = (categoriesResponse as Response.Success<List<Category>>).data
                if (searchQuery.isBlank()) {
                    categories
                } else {
                    categories.filter { category ->
                        category.name.contains(searchQuery, ignoreCase = true) ||
                            category.subcategories.any {
                                it.contains(searchQuery, ignoreCase = true)
                            }
                    }
                }
            }

            else -> emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Category") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search categories...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear search",
                            )
                        }
                    }
                } else null,
                singleLine = true,
            )

            when (val response = categoriesResponse) {
                is Response.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Response.Success -> {
                    if (filteredCategories.isEmpty() && searchQuery.isNotEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No matching categories found",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(filteredCategories) { category ->
                                CategoryItem(
                                    category = category,
                                    expandedCategory = expandedCategory,
                                    onCategorySelected = onCategorySelected,
                                    onExpandToggle = {
                                        expandedCategory = if (expandedCategory == category.name) {
                                            null
                                        } else {
                                            category.name
                                        }
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }

                is Response.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = response.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    expandedCategory: String?,
    onCategorySelected: (String, String) -> Unit,
    onExpandToggle: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (category.subcategories.isNotEmpty()) {
                        onExpandToggle()
                    } else {
                        onCategorySelected(category.name, "")
                    }
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = category.name, style = MaterialTheme.typography.bodyMedium)

            if (category.subcategories.isNotEmpty()) {
                Icon(
                    imageVector = if (expandedCategory == category.name)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                )
            }
        }

        // Subcategories
        if (expandedCategory == category.name) {
            Column(modifier = Modifier.padding(start = 32.dp)) {
                category.subcategories.forEach { subcategory ->
                    Text(
                        text = subcategory,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category.name, subcategory) }
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

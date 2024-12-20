package propertymanager.feature.tenant.home

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Category

@Composable
fun MaintenanceCategoriesScreen(
    onNavigateUp: () -> Unit,
    onCategorySelected: (category: String, subCategory: String) -> Unit,
    ) {
    val viewModel = hiltViewModel<MaintenanceRequestViewModel>()
    val categoriesResponse: Response<List<Category>> by viewModel.categoriesResponse.collectAsState()
    val expandedCategory = remember { mutableStateOf<String>("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raise Complaint", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
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
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            )

            when (categoriesResponse) {
                is Response.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Response.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items((categoriesResponse as Response.Success<List<Category>>).data) { category ->
                            CategoryItem(
                                category = category,
                                expandedCategory = expandedCategory.value,
                                onCategorySelected = onCategorySelected,
                                onExpandToggle = {
                                    expandedCategory.value =
                                        if (expandedCategory.value == category.name) "" else category.name
                                },
                            )
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                        }
                    }
                }

                is Response.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Error: ${(categoriesResponse as Response.Error).message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
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
    onExpandToggle: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.name, style = MaterialTheme.typography.bodyMedium)

            if (category.subcategories.isNotEmpty()) {
                Icon(
                    imageVector = if (expandedCategory == category.name) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse"
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
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

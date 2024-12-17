package propertymanager.feature.staff.categories

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Category
import propertymanager.feature.staff.categories.components.CategoryDialog
import propertymanager.feature.staff.categories.components.CategoryItem
import propertymanager.feature.staff.categories.components.SubcategoryDialog

@Composable
fun CategoryManagerScreen() {
    val viewModel = hiltViewModel<CategoryViewModel>()
    val categoriesResponse by viewModel.categoriesResponse.collectAsState()
    val operationResponse by viewModel.operationResponse.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showSubcategoryDialog by remember { mutableStateOf(false) }

    var isEditMode by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedSubcategory by remember { mutableStateOf<String?>(null) }

    var categoryName by remember { mutableStateOf("") }
    var subcategoryName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isEditMode = false
                showCategoryDialog = true
                categoryName = ""
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories") },
                actions = {
                    IconButton(onClick = { viewModel.toggleSortCategories() }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort Categories")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (categoriesResponse) {
                is Response.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is Response.Success -> {
                    val categories = (categoriesResponse as Response.Success<List<Category>>).data

                    LazyColumn {
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                onMoveUp = { viewModel.moveCategoryUp(category) },
                                onMoveDown = { viewModel.moveCategoryDown(category) },
                                onEditCategory = {
                                    isEditMode = true
                                    showCategoryDialog = true
                                    selectedCategoryId = category.id
                                    categoryName = category.name
                                },
                                onDeleteCategory = {
                                    viewModel.deleteCategory(category.id)
                                },
                                onAddSubcategory = {
                                    selectedCategoryId = category.id
                                    showSubcategoryDialog = true
                                    subcategoryName = ""
                                },
                                onEditSubcategory = { subName ->
                                    selectedCategoryId = category.id
                                    selectedSubcategory = subName
                                    showSubcategoryDialog = true
                                    subcategoryName = subName
                                },
                                onDeleteSubcategory = { subName ->
                                    viewModel.deleteSubcategory(category.id, subName)
                                }
                            )
                        }
                    }
                }
                is Response.Error -> Text(
                    "Error: ${(categoriesResponse as Response.Error).message}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Dialog for adding/editing categories
        if (showCategoryDialog) {
            CategoryDialog(
                categoryName = categoryName,
                onNameChange = { categoryName = it },
                onDismiss = { showCategoryDialog = false },
                onConfirm = {
                    if (isEditMode && selectedCategoryId != null) {
                        viewModel.updateCategory(Category(selectedCategoryId!!, categoryName, emptyList()))
                    } else {
                        viewModel.addCategory(Category("", categoryName, emptyList()))
                    }
                    showCategoryDialog = false
                }
            )
        }

        // Dialog for adding/editing subcategories
        if (showSubcategoryDialog) {
            SubcategoryDialog(
                subcategoryName = subcategoryName,
                onNameChange = { subcategoryName = it },
                onDismiss = { showSubcategoryDialog = false },
                onConfirm = {
                    if (selectedCategoryId != null) {
                        if (selectedSubcategory == null) {
                            viewModel.addSubcategory(selectedCategoryId!!, subcategoryName)
                        } else {
                            viewModel.updateSubcategory(selectedCategoryId!!, selectedSubcategory!!, subcategoryName)
                        }
                    }
                    showSubcategoryDialog = false
                    selectedSubcategory = null
                }
            )
        }

        // Operation Feedback
        when (operationResponse) {
            is Response.Loading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is Response.Error -> Toast.makeText(
                LocalContext.current,
                (operationResponse as Response.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> {}
        }
    }
}

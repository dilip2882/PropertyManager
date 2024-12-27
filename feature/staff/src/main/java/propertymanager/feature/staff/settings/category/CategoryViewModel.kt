package propertymanager.feature.staff.settings.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Category
import com.propertymanager.domain.usecase.CategoryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryUseCases: CategoryUseCases,
) : ViewModel() {

    private val _categoriesResponse = MutableStateFlow<Response<List<Category>>>(Response.Loading)
    val categoriesResponse: StateFlow<Response<List<Category>>> = _categoriesResponse

    private val _operationResponse = MutableStateFlow<Response<Unit>>(Response.Success(Unit))
    val operationResponse: StateFlow<Response<Unit>> = _operationResponse

    private var isSortedAscending = true

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _categoriesResponse.value = Response.Loading
            try {
                val categories = categoryUseCases.fetchCategories().sortedBy { it.name.lowercase() }
                _categoriesResponse.value = Response.Success(categories)
            } catch (e: Exception) {
                _categoriesResponse.value = Response.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun toggleSortCategories() {
        if (_categoriesResponse.value is Response.Success) {
            val sortedList = (categoriesResponse.value as Response.Success).data.sortedBy {
                if (isSortedAscending) it.name.lowercase() else it.name.lowercase().reversed()
            }
            _categoriesResponse.value = Response.Success(sortedList)
            isSortedAscending = !isSortedAscending
        }
    }

    fun moveCategoryUp(category: Category) {
        if (_categoriesResponse.value is Response.Success) {
            val categories = (categoriesResponse.value as Response.Success).data.toMutableList()
            val index = categories.indexOf(category)
            if (index > 0) { // Not first item
                categories.swap(index, index - 1)
                _categoriesResponse.value = Response.Success(categories)
            }
        }
    }

    fun moveCategoryDown(category: Category) {
        if (_categoriesResponse.value is Response.Success) {
            val categories = (categoriesResponse.value as Response.Success).data.toMutableList()
            val index = categories.indexOf(category)
            if (index < categories.size - 1) { // Not last item
                categories.swap(index, index + 1)
                _categoriesResponse.value = Response.Success(categories)
            }
        }
    }

    private fun MutableList<Category>.swap(i: Int, j: Int) {
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }


fun addCategory(category: Category) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                categoryUseCases.addCategory(category)
                fetchCategories() // Refresh
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to add category")
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                categoryUseCases.deleteCategory(categoryId)
                fetchCategories()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to delete category")
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                categoryUseCases.updateCategory(category)
                fetchCategories()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to update category")
            }
        }
    }

    fun addSubcategory(categoryId: String, subcategoryName: String) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                categoryUseCases.addSubcategory(categoryId, subcategoryName)
                fetchCategories()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to add subcategory")
            }
        }
    }

    fun deleteSubcategory(categoryId: String, subcategoryName: String) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                categoryUseCases.deleteSubcategory(categoryId, subcategoryName)
                fetchCategories()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to delete subcategory")
            }
        }
    }

    fun updateSubcategory(categoryId: String, oldName: String, newName: String) {
        viewModelScope.launch {
            _operationResponse.value = Response.Loading
            try {
                categoryUseCases.updateSubcategory(categoryId, oldName, newName)
                fetchCategories()
                _operationResponse.value = Response.Success(Unit)
            } catch (e: Exception) {
                _operationResponse.value = Response.Error(e.message ?: "Failed to update subcategory")
            }
        }
    }
}

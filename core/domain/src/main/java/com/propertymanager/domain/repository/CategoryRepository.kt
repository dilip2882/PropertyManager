package com.propertymanager.domain.repository

import com.propertymanager.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category)
    suspend fun deleteCategory(categoryId: String)
    suspend fun updateCategory(category: Category)

    suspend fun addSubcategory(categoryId: String, subcategoryName: String)
    suspend fun deleteSubcategory(categoryId: String, subcategoryName: String)
    suspend fun updateSubcategory(categoryId: String, oldName: String, newName: String)

}

package com.propertymanager.domain.usecase.category

import com.propertymanager.domain.model.Category
import com.propertymanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class FetchCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): Flow<List<Category>> {
        return repository.getCategories()
    }
}

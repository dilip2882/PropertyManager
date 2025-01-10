package com.propertymanager.domain.usecase.category

import com.propertymanager.domain.model.Category
import com.propertymanager.domain.repository.CategoryRepository

class FetchCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): List<Category> {
        return repository.getCategories()
    }
}

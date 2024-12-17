package com.propertymanager.domain.usecase.category

import com.propertymanager.domain.model.Category
import com.propertymanager.domain.repository.CategoryRepository

class AddCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category) {
        repository.addCategory(category)
    }
}

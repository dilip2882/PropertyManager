package com.propertymanager.domain.usecase.category

import com.propertymanager.domain.repository.CategoryRepository

class DeleteCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String) {
        repository.deleteCategory(categoryId)
    }
}

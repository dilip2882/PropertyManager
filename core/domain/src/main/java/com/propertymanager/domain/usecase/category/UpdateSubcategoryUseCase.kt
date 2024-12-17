package com.propertymanager.domain.usecase.category

import com.propertymanager.domain.repository.CategoryRepository

class UpdateSubcategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String, oldName: String, newName: String) {
        repository.updateSubcategory(categoryId, oldName, newName)
    }
}

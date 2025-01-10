package com.propertymanager.domain.usecase.category

import com.propertymanager.domain.repository.CategoryRepository

class DeleteSubcategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(categoryId: String, subcategoryName: String) {
        repository.deleteSubcategory(categoryId, subcategoryName)
    }
}

package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.category.AddCategoryUseCase
import com.propertymanager.domain.usecase.category.AddSubcategoryUseCase
import com.propertymanager.domain.usecase.category.DeleteCategoryUseCase
import com.propertymanager.domain.usecase.category.DeleteSubcategoryUseCase
import com.propertymanager.domain.usecase.category.FetchCategoriesUseCase
import com.propertymanager.domain.usecase.category.UpdateCategoryUseCase
import com.propertymanager.domain.usecase.category.UpdateSubcategoryUseCase

data class CategoryUseCases(
    val fetchCategories: FetchCategoriesUseCase,
    val addCategory: AddCategoryUseCase,
    val deleteCategory: DeleteCategoryUseCase,
    val updateCategory: UpdateCategoryUseCase,
    val addSubcategory: AddSubcategoryUseCase,
    val deleteSubcategory: DeleteSubcategoryUseCase,
    val updateSubcategory: UpdateSubcategoryUseCase,
)

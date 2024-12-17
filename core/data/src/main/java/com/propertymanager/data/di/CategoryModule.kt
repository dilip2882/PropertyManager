package com.propertymanager.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.data.repository.CategoryRepositoryImpl
import com.propertymanager.domain.repository.CategoryRepository
import com.propertymanager.domain.usecase.CategoryUseCases
import com.propertymanager.domain.usecase.category.AddCategoryUseCase
import com.propertymanager.domain.usecase.category.AddSubcategoryUseCase
import com.propertymanager.domain.usecase.category.DeleteCategoryUseCase
import com.propertymanager.domain.usecase.category.DeleteSubcategoryUseCase
import com.propertymanager.domain.usecase.category.FetchCategoriesUseCase
import com.propertymanager.domain.usecase.category.UpdateCategoryUseCase
import com.propertymanager.domain.usecase.category.UpdateSubcategoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Provides
    @Singleton
    fun provideCategoryRepository(firestore: FirebaseFirestore): CategoryRepository {
        return CategoryRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideCategoryUseCases(repository: CategoryRepository): CategoryUseCases {
        return CategoryUseCases(
            fetchCategories = FetchCategoriesUseCase(repository),
            addCategory = AddCategoryUseCase(repository),
            deleteCategory = DeleteCategoryUseCase(repository),
            updateCategory = UpdateCategoryUseCase(repository),
            addSubcategory = AddSubcategoryUseCase(repository),
            deleteSubcategory = DeleteSubcategoryUseCase(repository),
            updateSubcategory = UpdateSubcategoryUseCase(repository)
        )
    }
}

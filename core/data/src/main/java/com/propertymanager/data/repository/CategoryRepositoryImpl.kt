package com.propertymanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.domain.model.Category
import com.propertymanager.domain.repository.CategoryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    private val categoryCollection = firestore.collection("categories")

    override suspend fun getCategories(): Flow<List<Category>> = callbackFlow {
        val subscription = categoryCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val categories = snapshot.documents.map { document ->
                    document.toObject(Category::class.java)?.copy(id = document.id)
                        ?: Category("", "")
                }
                trySend(categories)
            }
        }

        awaitClose { subscription.remove() }
    }


    override suspend fun addCategory(category: Category) {
        categoryCollection.add(category).await()
    }

    override suspend fun updateCategory(category: Category) {
        categoryCollection.document(category.id).set(category).await()
    }

    override suspend fun deleteCategory(categoryId: String) {
        categoryCollection.document(categoryId).delete().await()
    }

    override suspend fun addSubcategory(categoryId: String, subcategoryName: String) {
        val document = categoryCollection.document(categoryId)
        val snapshot = document.get().await()
        val category = snapshot.toObject(Category::class.java)

        category?.let {
            val updatedSubcategories = it.subcategories + subcategoryName
            document.update("subcategories", updatedSubcategories).await()
        }
    }

    override suspend fun deleteSubcategory(categoryId: String, subcategoryName: String) {
        val document = categoryCollection.document(categoryId)
        val snapshot = document.get().await()
        val category = snapshot.toObject(Category::class.java)

        category?.let {
            val updatedSubcategories = it.subcategories.filter { sub -> sub != subcategoryName }
            document.update("subcategories", updatedSubcategories).await()
        }
    }

    override suspend fun updateSubcategory(categoryId: String, oldName: String, newName: String) {
        val document = categoryCollection.document(categoryId)
        val snapshot = document.get().await()
        val category = snapshot.toObject(Category::class.java)

        category?.let {
            val updatedSubcategories = it.subcategories.map { sub -> if (sub == oldName) newName else sub }
            document.update("subcategories", updatedSubcategories).await()
        }
    }
}

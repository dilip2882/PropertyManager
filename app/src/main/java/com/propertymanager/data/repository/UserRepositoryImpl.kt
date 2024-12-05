package com.propertymanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.domain.model.Role
import com.propertymanager.domain.model.User
import com.propertymanager.domain.repository.UserRepository
import com.propertymanager.utils.Constants.COLLECTION_NAME_USERS
import com.propertymanager.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : UserRepository {
    private var operationSuccessful = false

    override fun getUserDetails(userid: String): Flow<Response<User>> = callbackFlow {
        trySend(Response.Loading)

        val listener = firebaseFirestore.collection(COLLECTION_NAME_USERS)
            .document(userid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Response.Error(error.message ?: "Unknown error")).isSuccess
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        trySend(Response.Success(user)).isSuccess
                    } else {
                        trySend(Response.Error("Failed to deserialize user")).isSuccess
                    }
                } else {
                    trySend(Response.Error("User not found")).isSuccess
                }
            }

        awaitClose { listener.remove() }
    }


    override fun setUserDetails(user: User): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            val userMap = user.toMap()

            firebaseFirestore.collection(COLLECTION_NAME_USERS)
                .document(user.userId)
                .set(userMap) // Using set() for overwriting, update() for partial updates
                .await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to set user details"))
        }
    }

    // `User` to a map for Firestore
    private fun User.toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "name" to name,
            "username" to username,
            "imageUrl" to imageUrl,
            "bio" to bio,
            "url" to url,
            "phone" to phone,
            "email" to email,
            "role" to role.name,
            "address" to address,
            "location" to location,
            "properties" to properties,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "profileImage" to profileImage
        )
    }

}
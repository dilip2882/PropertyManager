package com.propertymanager.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.propertymanager.common.utils.Constants.COLLECTION_NAME_USERS
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.User
import com.propertymanager.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {
    private val usersCollection = firestore.collection("users")

    override fun getUserDetails(userid: String): Flow<Response<User>> = callbackFlow {
        trySend(Response.Loading)

        val listener = firestore.collection(COLLECTION_NAME_USERS)
            .document(userid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Response.Error(error.message ?: "Unknown error"))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {

                        val userWithId = user.copy(userId = snapshot.id)
                        trySend(Response.Success(userWithId))
                    } else {
                        trySend(Response.Error("Failed to deserialize user"))
                    }
                } else {
                    trySend(Response.Error("User not found"))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun setUserDetails(user: User): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        try {
            if (user.userId?.isEmpty() == true) {
                throw Exception("User ID cannot be empty")
            }

            val userMap = user.toMap()
            Log.d("UserRepositoryImpl", "Uploading user: $userMap") // Logging user details

            firestore.collection(COLLECTION_NAME_USERS)
                .document(user.userId!!)
                .set(userMap)
                .await()

            Log.d("UserRepositoryImpl", "User uploaded successfully")
            emit(Response.Success(true))
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error setting user details: ${e.message}")
            emit(Response.Error(e.message ?: "Failed to set user details"))
        }
    }

    override suspend fun associateProperty(userId: String, propertyId: String) {
        usersCollection.document(userId)
            .update("associatedProperties", FieldValue.arrayUnion(propertyId))
            .await()
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun updateSelectedProperty(userId: String, propertyId: String?) {
        usersCollection.document(userId)
            .update("selectedPropertyId", propertyId)
            .await()
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: return@callbackFlow
        
        val listener = usersCollection.document(currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val user = snapshot?.toObject(User::class.java)?.copy(userId = snapshot.id)
                trySend(user)
            }
            
        awaitClose { listener.remove() }
    }

    private fun User.toMap(): Map<String, Any?> {
        val map = mapOf(
            "userId" to userId,
            "name" to name,
            "username" to username,
            "imageUrl" to imageUrl,
            "bio" to bio,
            "url" to url,
            "phone" to phone,
            "email" to email,
            "address" to address,
            "location" to location,
            "associatedProperties" to associatedProperties,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "profileImage" to profileImage,
            "bannerImage" to bannerImage,
            "role" to role,
            "token" to token
        )
        Log.d("UserMap", "Converting User to Map: role = ${map["role"]}")
        return map
    }
}

fun addToken() {
    // Fetch the FCM token asynchronously
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Retrieve the FCM token
                val notiToken: String = task.result
                Log.d("TAG", "FCM Token: $notiToken")

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                Log.d("TAG", "docid: ${userId} ")
                // Update the Firestore document with the new token
                FirebaseFirestore.getInstance().collection(COLLECTION_NAME_USERS)
                    .document(userId!!)
                    .update("token", FieldValue.arrayUnion(notiToken))
                    .addOnSuccessListener {
                        Log.d("TAG", "Token added successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("TAG", "Error adding token", e)
                    }
            } else {
                Log.e("TAG", "Failed to fetch FCM token", task.exception)
            }
        }
}


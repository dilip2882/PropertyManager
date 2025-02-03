package com.propertymanager.navigation

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.propertymanager.domain.model.Role
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleNavigationHelper @Inject constructor() {
    suspend fun determineUserRole(): Role {
        val userId = Firebase.auth.currentUser?.uid
        return if (userId != null) {
            fetchUserRole(userId)
        } else {
            Log.d("RoleNavigationHelper", "No user ID found, defaulting to TENANT role")
            Role.TENANT
        }
    }

    private suspend fun fetchUserRole(userId: String): Role {
        val firestore = Firebase.firestore
        return try {
            val documentReference = firestore.collection("users").document(userId)
            Log.d("RoleNavigationHelper", "Fetching document for user ID: $userId")

            val snapshot = documentReference.get().await()
            Log.d("RoleNavigationHelper", "Full Document Data: ${snapshot.data}")

            val roleName = listOfNotNull(
                snapshot.getString("role"),
                snapshot.get("role")?.toString(),
                snapshot.data?.get("role")?.toString()
            ).firstOrNull() ?: Role.TENANT.name

            Log.d("RoleNavigationHelper", "Raw Role Name Retrieved: $roleName")

            try {
                Role.valueOf(roleName.uppercase()).also {
                    Log.d("RoleNavigationHelper", "Final Role Determined: $it")
                }
            } catch (e: IllegalArgumentException) {
                Log.e("RoleNavigationHelper", "Invalid role: $roleName, defaulting to TENANT")
                Role.TENANT
            }
        } catch (e: Exception) {
            Log.e("RoleNavigationHelper", "Error fetching user role", e)
            Role.TENANT
        }
    }
} 
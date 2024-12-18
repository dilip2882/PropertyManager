package propertymanager.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.propertymanager.domain.model.Role
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun HomeScreen(
    onNavigateToTenantScreen: () -> Unit,
    onNavigateToLandlordScreen: () -> Unit,
    onNavigateToManagerScreen: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userId = Firebase.auth.currentUser?.uid

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        Log.d("HomeScreen", "Current Authenticated User ID: $userId")

        if (userId != null) {
            coroutineScope.launch {
                try {
                    // Explicitly log Firebase Authentication details
                    val currentUser = Firebase.auth.currentUser
                    Log.d("HomeScreen", "Current Firebase User Details: " +
                        "UID: ${currentUser?.uid}, " +
                        "Phone: ${currentUser?.phoneNumber}, " +
                        "Email: ${currentUser?.email}")

                    val role = fetchUserRole(userId)
                    Log.d("HomeScreen", "User Role Retrieved: $role")

                    // Log the navigation decision
                    Log.d("HomeScreen", "Navigating based on role: $role")

                    when (role) {
                        Role.TENANT -> {
                            Log.d("HomeScreen", "Navigating to Tenant Screen")
                            onNavigateToTenantScreen()
                        }
                        Role.LANDLORD -> {
                            Log.d("HomeScreen", "Navigating to Landlord Screen")
                            onNavigateToLandlordScreen()
                        }
                        Role.MANAGER -> {
                            Log.d("HomeScreen", "Navigating to Manager Screen")
                            onNavigateToManagerScreen()
                        }
                        else -> {
                            Log.d("HomeScreen", "Defaulting to Tenant Screen")
                            onNavigateToTenantScreen()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Exception in role navigation", e)
                    e.printStackTrace()
                    onNavigateToTenantScreen()
                } finally {
                    isLoading = false
                }
            }
        } else {
            Log.d("HomeScreen", "No user ID, defaulting to Tenant Screen")
            onNavigateToTenantScreen()
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
suspend fun fetchUserRole(userId: String): Role {
    val firestore = Firebase.firestore
    return try {
        val documentReference = firestore.collection("users").document(userId)

        Log.d("FetchUserRole", "Fetching document for user ID: $userId")

        val snapshot = documentReference.get().await()

        Log.d("FetchUserRole", "Full Document Data: ${snapshot.data}")

        val roleName = listOfNotNull(
            snapshot.getString("role"),
            snapshot.get("role")?.toString(),
            snapshot.data?.get("role")?.toString()
        ).firstOrNull() ?: Role.TENANT.name

        Log.d("FetchUserRole", "Raw Role Name Retrieved: $roleName")

        // convert to Role enum
        val role = try {
            Role.valueOf(roleName.uppercase())
        } catch (e: IllegalArgumentException) {
            Log.e("FetchUserRole", "Invalid role: $roleName, defaulting to TENANT")
            Role.TENANT
        }

        Log.d("FetchUserRole", "Final Role Determined: $role")

        role
    } catch (e: Exception) {
        Log.e("FetchUserRole", "Comprehensive Error in fetchUserRole", e)
        Role.TENANT
    }
}

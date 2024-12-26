package com.propertymanager.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.propertymanager.domain.model.Role
import com.propertymanager.navigation.Dest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import propertymanager.feature.staff.settings.BiometricViewModel

@Composable
fun PropertyManagerApp(
    onNavigateToTenantScreen: () -> Unit,
    onNavigateToLandlordScreen: () -> Unit,
    onNavigateToManagerScreen: () -> Unit,
    ) {
    val biometricViewModel = hiltViewModel<BiometricViewModel>()
    val hasAuthenticated by biometricViewModel.hasAuthenticated.collectAsState()

    val userId = Firebase.auth.currentUser?.uid
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    var hideBottomBar by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = hasAuthenticated) {
        if (hasAuthenticated) {
            if (userId != null) {
                scope.launch {
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

    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    RequestNotificationPermission {

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
            snapshot.data?.get("role")?.toString(),
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

@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit,
) {
    val context = LocalContext.current

    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            showRationale =
                context.shouldShowRequestPermissionRationaleCompat(Manifest.permission.POST_NOTIFICATIONS)
            if (!showRationale) {
                showSettingsDialog = true
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onPermissionGranted()
                }

                context.shouldShowRequestPermissionRationaleCompat(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showRationale = true
                }

                else -> {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            onPermissionGranted()
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text(text = "Notification Permission Required") },
            text = { Text(text = "This app needs notification permission.") },
            confirmButton = {
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        showRationale = false
                    },
                ) {
                    Text("Allow")
                }
            },
            dismissButton = {
                Button(onClick = { showRationale = false }) {
                    Text("Deny")
                }
            },
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(text = "Notification Permission Required") },
            text = { Text(text = "You have denied the notification permission. Please enable it in the app settings.") },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        showSettingsDialog = false
                    },
                ) {
                    Text("Go to Settings")
                }
            },
            dismissButton = {
                Button(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

fun Context.shouldShowRequestPermissionRationaleCompat(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this as Activity, permission)
}

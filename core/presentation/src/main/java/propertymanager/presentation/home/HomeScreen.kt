package propertymanager.presentation.home

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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.propertymanager.domain.model.Role
import com.propertymanager.domain.model.User
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
        if (userId != null) {
            coroutineScope.launch {
                try {
                    val role = fetchUserRole(userId)
                    when (role) {
                        Role.TENANT -> onNavigateToTenantScreen()
                        Role.LANDLORD -> onNavigateToLandlordScreen()
                        Role.MANAGER -> onNavigateToManagerScreen()
                        else -> onNavigateToTenantScreen()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onNavigateToTenantScreen()
                } finally {
                    isLoading = false
                }
            }
        } else {
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
        val snapshot = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        val roleName = snapshot.getString("role") ?: Role.MANAGER.name
        Role.valueOf(roleName)
    } catch (e: Exception) {
        e.printStackTrace()
        Role.MANAGER
    }
}

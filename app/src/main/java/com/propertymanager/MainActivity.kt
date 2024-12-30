package com.propertymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.propertymanager.common.preferences.AppPreferences
import com.propertymanager.domain.model.biometrics.BiometricAuthState
import com.propertymanager.domain.model.category.Country
import com.propertymanager.navigation.MainNavigation
import com.propertymanager.ui.theme.PropertyManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import propertymanager.feature.staff.settings.BiometricViewModel
import propertymanager.feature.staff.settings.ThemeViewModel
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var appPreferences: AppPreferences

    val navOptions = NavOptions.Builder()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ThemeViewModel = hiltViewModel()
            val biometricViewModel: BiometricViewModel = hiltViewModel()

            val dynamicColor by viewModel.dynamicColor.collectAsState()
            val darkMode by viewModel.darkMode.collectAsState()

            val hasAuthenticated by biometricViewModel.hasAuthenticated.collectAsState()
            val biometricAuthState by biometricViewModel
                .biometricAuthState
                .collectAsState(BiometricAuthState.LOADING)
            val biometricAuthResult by biometricViewModel.authResult.collectAsState()

            LaunchedEffect(biometricAuthState) {

                if (biometricAuthState == BiometricAuthState.ENABLED) {
                    biometricViewModel.authenticate(this@MainActivity)
                }
            }

            biometricViewModel.handleBiometricAuth(biometricAuthResult, this)

            PropertyManagerTheme(
                darkTheme = darkMode, dynamicColor = dynamicColor,
            ) {
                MainNavigation(
                    navController = rememberNavController(),
                    appPreferences = appPreferences,
                )

            }

        }
    }
}


/*
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
*/

package com.propertymanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.propertymanager.common.preferences.AppPreferences
import com.propertymanager.navigation.MainNavigation
import com.propertymanager.ui.theme.PropertyManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appPreferences: AppPreferences

    val navOptions = NavOptions.Builder()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().subscribeToTopic("broadcast").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "onCreate: success")
            } else {
                Log.d("TAG", "onCreate: failed")
            }
        }

        setContent {
            PropertyManagerTheme {
                MainNavigation(
                    navController = rememberNavController(),
                    appPreferences = appPreferences,
                )
            }
        }
    }
}


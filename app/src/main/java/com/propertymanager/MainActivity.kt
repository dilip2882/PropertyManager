package com.propertymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.propertymanager.navigation.MainNavigation
import com.propertymanager.ui.theme.PropertyManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PropertyManagerTheme {
                MainNavigation(
                    navController = rememberNavController()
                )
            }
        }
    }
}

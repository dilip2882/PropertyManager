package com.propertymanager.presentation.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.propertymanager.presentation.navigation.BottomNavigationItem
import com.propertymanager.presentation.navigation.BottomNavigationMenu

@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Settings Screen")
        }

        BottomNavigationMenu(selectedItem = BottomNavigationItem.SETTINGS, navController = navController)
    }
}
package com.propertymanager.presentation.ui.userprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.GeoPoint
import com.propertymanager.domain.model.Role
import com.propertymanager.domain.model.User
import com.propertymanager.presentation.navigation.BottomNavigationItem
import com.propertymanager.presentation.navigation.BottomNavigationMenu
import com.propertymanager.utils.Response
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen2(navController: NavController) {
    val userViewModel: UserViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        userViewModel.getUserInfo()
    }

    when (val response = userViewModel.getUserData.value) {
        is Response.Loading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is Response.Success -> {
            val userData = response.data ?: return
            var username by remember { mutableStateOf(userData.username) }
            var bio by remember { mutableStateOf(userData.bio) }
            var email by remember { mutableStateOf(userData.email) }
            var address by remember { mutableStateOf(userData.address) }
            var location by remember { mutableStateOf(userData.location) }
            var base64Image by remember { mutableStateOf(userData.imageUrl) }

            var isEditing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.fillMaxSize()) {
                Column(Modifier.weight(1f)) {
                    TopAppBar(
                        title = { Text(text = userData.name, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                        actions = {
                            if (isEditing) {
                                IconButton(onClick = {
                                    // Save changes when in edit mode
                                    val updatedUser = User(
                                        userId = userViewModel.auth.currentUser?.uid.orEmpty(),
                                        name = userData.name,
                                        username = username,
                                        imageUrl = base64Image ?: userData.imageUrl,
                                        bio = bio,
                                        url = userData.url,
                                        phone = userData.phone,
                                        email = email,
                                        role = userData.role,
                                        address = address,
                                        location = location, // Pass location as GeoPoint
                                        properties = userData.properties,
                                        createdAt = userData.createdAt,
                                        updatedAt = userData.updatedAt,
                                        profileImage = userData.profileImage
                                    )

                                    userViewModel.setUserInfo(updatedUser)
                                    isEditing = false
                                }) {
                                    Icon(imageVector = Icons.Default.Done, contentDescription = "Save")
                                }
                            } else {
                                IconButton(onClick = { isEditing = true }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Editable Fields with Edit Icons
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        EditableTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Username",
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        EditableTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = "Bio",
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        EditableTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        EditableTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = "Address",
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        EditableTextField(
                            value = location.toString(),
                            onValueChange = { location = GeoPoint(it.toDouble(), it.toDouble()) }, // Update as GeoPoint
                            label = "Location",
                            isEditing = isEditing
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        EditableTextField(
                            value = base64Image ?: "",
                            onValueChange = { base64Image = it },
                            label = "Profile Image (Base64)",
                            isEditing = isEditing
                        )
                    }
                }
            }
        }

        is Response.Error -> {
            Text("Error loading profile data")
        }
    }
}

@Composable
fun EditableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isEditing: Boolean
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        readOnly = !isEditing,
        trailingIcon = {
            if (isEditing) {
                IconButton(onClick = { /* Logic to enable editing on tap */ }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

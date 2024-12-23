package propertymanager.feature.tenant.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.propertymanager.common.utils.Constants.COLLECTION_NAME_USERS

@Composable
fun TenantProfileScreen(
    onNavigateToEditProfile: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                ),
                title = { Text("") },
                actions = {
                    IconButton(onClick = onNavigateToEditProfile) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Edit Profile",
                            tint = Color.Black,
                        )
                    }
                },
            )
        },
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            Profile()

            /*            Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Profile Image
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF556E8D)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "D",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                        )
                                    }
                                    Column(modifier = Modifier.padding(start = 12.dp)) {
                                        Text(
                                            text = "Dilip",
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black,
                                        )
                                        Text(
                                            text = "Gorwa, Vadodara",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "I have water problem",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                )
                                Text(
                                    text = "Only single bed without mattress",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }*/
        }
    }
}

@SuppressLint("InvalidColorHexValue")
@Composable
fun Profile() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),

        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(15.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.run {
                            linearGradient(
                                listOf(Color(0xFF5CD6FF), Color(0xFF5CD6FF)),
                            )
                        },
                    ),

                )

            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera Icon",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(28.dp),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, top = 50.dp)
                    .offset(y = 15.dp),

                ) {

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF556E8D)),
                    contentAlignment = Alignment.Center,
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Icon",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )

                }

                // Edit Icon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.BottomEnd)
                        .offset(y = 0.dp, x = (-2).dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        modifier = Modifier.size(20.dp),
                        contentDescription = "Edit Icon",
                        tint = Color.Black,
                    )
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Dilip",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black,
            )
            Text(
                text = "6-152    Residing Owner",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Bio",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "Add bio",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                    Text(
                        text = "Tell your neighbours about yourself",
                        fontSize = 12.sp,
                        color = Color.Gray,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Work & Hometown
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = "Work",
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = "Add work",
                    color = Color(0xFF007AFF),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            Row {

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Hometown",
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(24.dp),
                )

                Text(
                    text = "Add Address",
                    color = Color(0xFF007AFF),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 2.dp),
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interests
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Interests",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
            )
            OutlinedButton(
                onClick = { /* Add Interests Click */ },
                modifier = Modifier.padding(top = 8.dp),
                shape = RoundedCornerShape(50),
            ) {
                Text(text = "+ Add Interests", color = Color(0xFF007AFF))
            }
        }

    }
}



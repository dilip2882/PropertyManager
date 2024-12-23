package com.propertymanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.propertymanager.common.preferences.AppPreferences
import com.propertymanager.common.utils.Constants.COLLECTION_NAME_USERS
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

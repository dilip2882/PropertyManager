package com.propertymanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.collectAsState
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.propertymanager.common.utils.Constants.COLLECTION_NAME_USERS
import com.propertymanager.domain.model.User
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.tasks.await

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "PropertyManager",
                "PropertyManager Notification Channel",
                NotificationManager.IMPORTANCE_HIGH,

                )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}

package com.propertymanager.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.propertymanager.presentation.R

class CloudNotification: FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            showNotification(this,it.title.toString(),it.body.toString())
            Log.d("TAG", "onMessageReceived: ${it.title.toString()}")
        }
    }
}


fun showNotification(context: Context, title:String, message:String) {
    // Check if we have the POST_NOTIFICATIONS permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request permission or handle accordingly
            Log.w("NotificationPermission", "POST_NOTIFICATIONS permission not granted")
            Toast.makeText(context, "Notification permission is required", Toast.LENGTH_SHORT).show()
            return
        }
    }

    try {
        val notification = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setChannelId("PropertyManager")
            .build()

        // Display the notification
        NotificationManagerCompat.from(context).notify(1, notification)
    } catch (e: SecurityException) {
        Log.e("NotificationError", "Permission to post notifications is missing: ${e.message}")
        Toast.makeText(context, "Permission to post notifications is missing", Toast.LENGTH_SHORT).show()
    }
}

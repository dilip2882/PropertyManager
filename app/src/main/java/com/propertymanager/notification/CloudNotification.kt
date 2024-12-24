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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.propertymanager.common.utils.Constants
import com.propertymanager.common.utils.Response
import com.propertymanager.presentation.R

class CloudNotification: FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
/*        val updateData = mapOf(
            "token" to FieldValue.arrayUnion(token)
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userDocRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_NAME_USERS)
            .document(userId)

        userDocRef.update(updateData).addOnSuccessListener {
            Log.d("onNewToken", ": $updateData")
        }.addOnFailureListener { e ->
            Log.e("onNewToken", "User update failed", e)
        }*/
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

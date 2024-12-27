package com.propertymanager.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.messaging.FirebaseMessaging
import com.propertymanager.common.utils.Constants
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Role
import com.propertymanager.domain.model.User
import com.propertymanager.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val context: Activity
) : AuthRepository {

    private lateinit var verificationCode: String
    private lateinit var verificationToken: PhoneAuthProvider.ForceResendingToken

    override fun getFirebaseAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            trySend(auth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun createUserWithPhone(
        phone: String,
        activity: Activity,
    ): Flow<Response<String>> = callbackFlow {
        trySend(Response.Loading)

        val onVerificationCallback =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {}

                override fun onVerificationFailed(p0: FirebaseException) {
                    trySend(Response.Error(p0.toString()))
                }

                override fun onCodeSent(
                    sentVerificationCode: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    super.onCodeSent(sentVerificationCode, token)
                    trySend(Response.Success("OTP Sent Successfully"))
                    verificationCode = sentVerificationCode // Store the actual verification code
                    verificationToken = token // Store the token for resending
                }
            }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(onVerificationCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose {
            close()
        }
    }

    private suspend fun checkPlayServices(context: Activity): Boolean {
        return try {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            resultCode == ConnectionResult.SUCCESS
        } catch (e: Exception) {
            Log.e("AuthRepo", "Error checking Play Services", e)
            false
        }
    }

    private suspend fun getFcmToken(): String? {
        if (!checkPlayServices(context = context)) {
            Log.w("AuthRepo", "Google Play Services not available")
            return null
        }

        return try {
            withTimeout(30000) { // 30 seconds timeout
                var attempts = 0
                var token: String? = null

                while (attempts < 3 && token == null) {
                    try {
                        token = suspendCancellableCoroutine { continuation ->
                            FirebaseMessaging.getInstance().token
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        continuation.resume(task.result)
                                    } else {
                                        Log.e("AuthRepo", "FCM token task failed", task.exception)
                                        continuation.resume(null)
                                    }
                                }
                        }

                        if (token == null) {
                            attempts++
                            if (attempts < 3) {
                                Log.d("AuthRepo", "FCM token attempt $attempts failed, retrying...")
                                delay(2000L * attempts) // Exponential backoff
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AuthRepo", "FCM token fetch attempt $attempts failed", e)
                        attempts++
                        if (attempts < 3) delay(2000L * attempts)
                    }
                }
                token
            }
        } catch (e: Exception) {
            Log.w("AuthRepo", "Failed to fetch FCM token after retries", e)
            null
        }
    }

    override suspend fun signInWithCredential(otp: String): Flow<Response<String>> = callbackFlow {
        trySend(Response.Loading)

        if (!::verificationCode.isInitialized) {
            trySend(Response.Error("Verification code not initialized"))
            close()
            return@callbackFlow
        }

        try {
            val credential = PhoneAuthProvider.getCredential(verificationCode, otp)
            val authResult = auth.signInWithCredential(credential).await()

            val user = authResult.user ?: run {
                trySend(Response.Error("Authentication failed: User is null"))
                close()
                return@callbackFlow
            }

            if (user.uid.isEmpty()) {
                trySend(Response.Error("User ID cannot be empty"))
                close()
                return@callbackFlow
            }

            val userDocRef = firestore.collection(Constants.COLLECTION_NAME_USERS)
                .document(user.uid)

            // Fetch existing user data
            val documentSnapshot = try {
                userDocRef.get().await()
            } catch (e: Exception) {
                Log.e("AuthRepo", "Failed to fetch user document", e)
                trySend(Response.Error("Failed to fetch user data: ${e.message}"))
                close()
                return@callbackFlow
            }

            // Preserve existing role
            val existingRole = documentSnapshot.getString("role")
                ?: documentSnapshot.get("role")?.toString()
                ?: Role.TENANT.name

            Log.d("AuthRepo", "Existing Role in Document: $existingRole")

            // Get FCM token with device compatibility check
            val fcmToken = getFcmToken()

            // Prepare update data
            val updateData = mutableMapOf(
                "phone" to (user.phoneNumber ?: ""),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            try {
                // First, update the basic user data
                userDocRef.update(updateData).await()

                // If we have an FCM token, update it in a separate transaction
                if (fcmToken != null) {
                    try {
                        firestore.runTransaction { transaction ->
                            val snapshot = transaction.get(userDocRef)
                            val tokens = snapshot.get("token") as? List<String> ?: listOf()

                            if (fcmToken !in tokens) {
                                transaction.update(userDocRef, "token", FieldValue.arrayUnion(fcmToken))
                            }
                        }.await()
                    } catch (e: Exception) {
                        Log.e("AuthRepo", "Failed to update FCM token", e)
                        // Continue without FCM token update
                    }
                }

                // Finally, ensure role is preserved
                userDocRef.update("role", existingRole).await()

                val successMessage = if (fcmToken != null) {
                    "Signed in with role: $existingRole"
                } else {
                    "Signed in with role: $existingRole (FCM token update skipped)"
                }

                trySend(Response.Success(successMessage))
            } catch (e: Exception) {
                Log.e("AuthRepo", "Failed to update user document", e)
                trySend(Response.Error("Failed to update user data: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Authentication failed", e)
            trySend(Response.Error(e.message ?: "Authentication failed"))
        }

        awaitClose {
            close()
        }
    }

    override suspend fun resendOtp(
        phone: String,
        activity: Activity,
    ): Flow<Response<String>> = callbackFlow {
        trySend(Response.Loading)

        val onVerificationCallback =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {}

                override fun onVerificationFailed(p0: FirebaseException) {
                    trySend(Response.Error(p0.toString()))
                }

                override fun onCodeSent(
                    sentVerificationCode: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    super.onCodeSent(sentVerificationCode, token)
                    trySend(Response.Success("OTP Resent Successfully"))
                    verificationCode = sentVerificationCode // Store the new verification code
                    verificationToken = token // Update the token
                }
            }

        // Resend OTP with the stored ForceResendingToken
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(onVerificationCallback)
            .setForceResendingToken(verificationToken) // Use the stored token
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        awaitClose {
            close()
        }
    }

    override fun firebaseSignOut(): Flow<Response<Boolean>> = flow {
        try {
            emit(Response.Loading)
            auth.signOut()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "An Unexpected Error"))
        }
    }

    override suspend fun firebaseSignUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String,
    ): Flow<Response<Boolean>> = flow {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return@flow emit(Response.Error("User creation failed"))

            if (userId.isNotEmpty()) {
                val user = User(
                    username = username,
                    email = email,
                    userId = userId,
                    name = "",
                    phone = "",
                    address = "",
                    location = GeoPoint(0.0, 0.0)
                )
                firestore.collection(Constants.COLLECTION_NAME_USERS).document(userId).set(user).await()
                emit(Response.Success(true))
            } else {
                emit(Response.Error("User ID is empty"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Sign up failed"))
        }
    }

    override suspend fun isUserAuthenticatedInFirebase(): Boolean {
        return auth.currentUser != null
    }

}

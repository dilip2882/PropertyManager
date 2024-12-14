package com.propertymanager.data.repository

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.propertymanager.common.utils.Constants
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.Role
import com.propertymanager.domain.model.User
import com.propertymanager.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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
        activity: Activity
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
                    token: PhoneAuthProvider.ForceResendingToken
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

    override suspend fun signInWithCredential(otp: String): Flow<Response<String>> = callbackFlow {
        trySend(Response.Loading)

        if (::verificationCode.isInitialized) {
            val credential = PhoneAuthProvider.getCredential(verificationCode, otp)
            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // User authenticated successfully with OTP
                        val user = auth.currentUser
                        Log.d("AuthRepo", "Current User: ${user?.uid}")

                        if (user != null && user.uid.isNotEmpty()) {
                            val userId = user.uid
                            val userDocRef = firestore.collection(Constants.COLLECTION_NAME_USERS)
                                .document(userId)

                            // Check if the user already exists in Firestore
                            userDocRef.get().addOnSuccessListener { document ->
                                if (!document.exists()) {
                                    val newUser = User(
                                        userId = userId,
                                        phone = user.phoneNumber ?: "",
                                        name = "",
                                        username = "",
                                        imageUrl = "",
                                        bio = "",
                                        url = "",
                                        role = Role.TENANT, // Default role for first-time creation
                                        address = "",
                                        location = GeoPoint(0.0, 0.0),
                                        associatedProperties = emptyList(),
                                        createdAt = Timestamp.now(),
                                        updatedAt = Timestamp.now(),
                                        profileImage = null,
                                        email = ""
                                    )

                                    // Store the new user in Firestore
                                    userDocRef.set(newUser).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            trySend(Response.Success("User Created Successfully"))
                                        } else {
                                            trySend(Response.Error("Failed to add user to Firestore"))
                                        }
                                    }
                                } else {
                                    // User already exists, do not change the role
                                    trySend(Response.Success("User already exists in Firestore"))
                                }
                            }
                        } else {
                            Log.e("AuthRepo", "User ID is empty or user is null!")
                            trySend(Response.Error("User ID cannot be empty"))
                        }
                    } else {
                        trySend(Response.Error("Invalid OTP"))
                    }
                }
                .addOnFailureListener {
                    trySend(Response.Error(it.toString()))
                }
        } else {
            trySend(Response.Error("Verification code not initialized"))
        }

        awaitClose {
            close()
        }
    }

    override suspend fun resendOtp(
        phone: String,
        activity: Activity
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
                    token: PhoneAuthProvider.ForceResendingToken
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
        username: String
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

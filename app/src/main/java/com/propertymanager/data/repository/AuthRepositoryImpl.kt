package com.propertymanager.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.propertymanager.domain.model.User
import com.propertymanager.domain.repository.AuthRepository
import com.propertymanager.utils.Response
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

    private lateinit var omVerificationCode: String

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
                    verificationCode: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationCode, p1)
                    trySend(Response.Success("OTP Sent Successfully"))
                    omVerificationCode = verificationCode
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

        val code = omVerificationCode

        if (code.isNullOrEmpty()) {
            trySend(Response.Error(IllegalStateException("Verification code not initialized").toString()))

        } else {
            val credential = PhoneAuthProvider.getCredential(code, otp)
            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(Response.Success("OTP verified"))
                    } else {
                        trySend(
                            Response.Error(
                                it.toString()
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    trySend(Response.Error(it.toString()))
                }
        }

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
            val userid =
                result.user?.uid ?: return@flow emit(Response.Error("User creation failed"))
            val user = User(username = username, email = email, userid = userid)
            firestore.collection("users").document(userid).set(user).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Sign up failed"))
        }
    }

    override suspend fun isUserAuthenticatedInFirebase(): Boolean {
        return auth.currentUser != null
    }
}

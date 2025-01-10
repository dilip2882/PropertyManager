package com.propertymanager.domain.repository

import android.app.Activity
import com.propertymanager.common.utils.Response
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getFirebaseAuthState(): Flow<Boolean>

    suspend fun createUserWithPhone(phone: String, activity: Activity): Flow<Response<String>>
    suspend fun signInWithCredential(otp: String): Flow<Response<String>>
    suspend fun resendOtp(phone: String, activity: Activity): Flow<Response<String>>
    fun firebaseSignOut(): Flow<Response<Boolean>>

    suspend fun firebaseSignUpWithEmailAndPassword(
        email: String,
        password: String,
        username: String
    ): Flow<Response<Boolean>>

    suspend fun isUserAuthenticatedInFirebase(): Boolean
}

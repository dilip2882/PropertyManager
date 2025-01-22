package com.propertymanager.domain.repository

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserDetails(userid: String): Flow<Response<User>>

    fun setUserDetails(user: User): Flow<Response<Boolean>>

    fun getCurrentUser(): Flow<User?>

    suspend fun updateSelectedProperty(userId: String, propertyId: String?)

    suspend fun associateProperty(userId: String, propertyId: String)

    suspend fun updateUser(user: User)

    suspend fun getUserById(userId: String): User?
}

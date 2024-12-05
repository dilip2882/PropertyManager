package com.propertymanager.domain.repository

import com.propertymanager.domain.model.User
import com.propertymanager.utils.Response
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUserDetails(userid: String): Flow<Response<User>>

    fun setUserDetails(user: User): Flow<Response<Boolean>>
}
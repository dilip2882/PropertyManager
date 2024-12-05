package com.propertymanager.presentation.ui.userprofile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.propertymanager.domain.model.User
import com.propertymanager.domain.usecase.UserUseCases
import com.propertymanager.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val auth: FirebaseAuth,
    private val userUseCases: UserUseCases
) : ViewModel(){

    private val userid = auth.currentUser?.uid

    private val _getUserData = mutableStateOf<Response<User?>>(Response.Success(null))
    val getUserData: State<Response<User?>> = _getUserData

    private val _setUserData = mutableStateOf<Response<Boolean>>(Response.Success(false))
    val setUserData: State<Response<Boolean>> = _setUserData

    fun getUserInfo(){
        if (userid!=null) {
            viewModelScope.launch {
                userUseCases.getUserDetailsUseCases(userid = userid).collect{
                    _getUserData.value = it
                }
            }
        }
    }

    fun setUserInfo(user: User) {
        viewModelScope.launch {
            userUseCases.setUserDetailsUseCase(user).collect { response ->
                _setUserData.value = response
            }
        }
    }

}
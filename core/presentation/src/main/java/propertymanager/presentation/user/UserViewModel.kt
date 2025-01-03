package propertymanager.presentation.user

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.User
import com.propertymanager.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val auth: FirebaseAuth,
    private val userUseCases: UserUseCases,
    private val firebaseStorage: FirebaseStorage,
) : ViewModel(){

    private val userid = auth.currentUser?.uid

    private val _getUserData = MutableStateFlow<Response<User?>>(Response.Success(null))
    val getUserData: StateFlow<Response<User?>> = _getUserData

    private val _setUserData = MutableStateFlow<Response<Boolean>>(Response.Success(false))
    val setUserData: StateFlow<Response<Boolean>> = _setUserData

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

    fun getUserDetailsByIdFlow(userId: String): Flow<Response<User>> {
        return userUseCases.getUserDetailsUseCases(userId)
    }

    suspend fun uploadImageToFirebase(uri: Uri, userId: String): String {
        val storageRef = firebaseStorage.reference.child("profile_pictures/$userId.jpg")
        return try {
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("OnboardingViewModel", "Error uploading image: ${e.message}")
            throw Exception("Failed to upload profile picture. Please try again.")
        }
    }
}

package propertymanager.presentation.onboarding

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.User
import com.propertymanager.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import propertymanager.presentation.onboarding.mvi.OnboardingContract
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel(), OnboardingContract {

    private val userid = firebaseAuth.currentUser?.uid

    private val _state = MutableStateFlow<OnboardingContract.OnboardingState>(OnboardingContract.OnboardingState.Idle)
    override val state: StateFlow<OnboardingContract.OnboardingState> = _state

    private val _effect = MutableSharedFlow<OnboardingContract.OnboardingEffect>()
    override val effect: SharedFlow<OnboardingContract.OnboardingEffect> = _effect

    override fun event(event: OnboardingContract.OnboardingEvent) {
        when (event) {
            is OnboardingContract.OnboardingEvent.SubmitUserDetails -> {
                submitUserDetails(event.user, event.imageUri)
            }

            is OnboardingContract.OnboardingEvent.GetUserDetails -> {
                getUserDetails(event.userId)
            }
        }
    }

    private fun getUserDetails(userId: String) {
        viewModelScope.launch {
            _state.value = OnboardingContract.OnboardingState.Loading
            try {
                val docRef = firestore.collection("users").document(userId)
                docRef.get().await().let { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(User::class.java)
                        user?.let {
                            _state.value = OnboardingContract.OnboardingState.Success(user)
                        }
                    } else {
                        _state.value = OnboardingContract.OnboardingState.Error("User not found.")
                    }
                }
            } catch (e: Exception) {
                _state.value = OnboardingContract.OnboardingState.Error("Failed to fetch user details.")
            }
        }
    }

    private fun submitUserDetails(user: User, imageUri: Uri?) {
        viewModelScope.launch {
            _state.value = OnboardingContract.OnboardingState.Loading

            try {
                val currentUser = firebaseAuth.currentUser
                if (currentUser == null || currentUser.uid.isNullOrEmpty()) {
                    _state.value = OnboardingContract.OnboardingState.Error("User is not authenticated.")
                    return@launch
                }

                Log.d("OnboardingViewModel", "Authenticated User UID: ${currentUser.uid}")

                val imageUrl = imageUri?.let { uploadImageToFirebase(it, currentUser.uid) }
                val updatedUser = user.copy(imageUrl = imageUrl ?: user.imageUrl)

                // Save user details in Firestore
                firestore.collection("users").document(currentUser.uid).set(updatedUser)
                    .await()

                _effect.emit(OnboardingContract.OnboardingEffect.NavigateToHome)
                _state.value = OnboardingContract.OnboardingState.Success(updatedUser)
            } catch (e: Exception) {
                _state.value = OnboardingContract.OnboardingState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    private suspend fun uploadImageToFirebase(uri: Uri, userId: String): String {
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

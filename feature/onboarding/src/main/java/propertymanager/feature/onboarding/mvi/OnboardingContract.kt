package propertymanager.feature.onboarding.mvi

import android.net.Uri
import com.propertymanager.domain.model.User
import propertymanager.presentation.mvi.MVIContract

interface OnboardingContract : MVIContract<OnboardingContract.OnboardingState, OnboardingContract.OnboardingEffect, OnboardingContract.OnboardingEvent> {

    sealed class OnboardingEvent {
        data class SubmitUserDetails(val user: User, val imageUri: Uri?) : OnboardingEvent()
        data class GetUserDetails(val userId: String) : OnboardingEvent()
    }

    sealed class OnboardingState {
        object Idle : OnboardingState()
        object Loading : OnboardingState()
        data class Success(val user: User) : OnboardingState()
        data class Error(val message: String) : OnboardingState()
    }

    sealed class OnboardingEffect {
        object NavigateToHome : OnboardingEffect()
        data class ShowToast(val message: String) : OnboardingEffect()
    }
}

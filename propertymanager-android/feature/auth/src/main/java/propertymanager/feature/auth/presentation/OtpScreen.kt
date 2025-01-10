package propertymanager.feature.auth.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import propertymanager.feature.auth.R
import propertymanager.feature.auth.presentation.mvi.AuthContract
import propertymanager.presentation.screens.LoadingScreen

@Composable
fun OtpScreen(
    state: AuthContract.AuthState,
    effect: AuthContract.AuthEffect?,
    dispatch: (AuthContract.AuthEvent) -> Unit,
    onNavigateToPhoneScreen: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    phoneNumber: String,
) {
    val context = LocalContext.current
    var otpInput by remember { mutableStateOf("") }
    var resendOtpEnabled by remember { mutableStateOf(true) }
    var countdownTime by remember { mutableIntStateOf(30) }

    BackHandler {
        onNavigateToPhoneScreen()
    }

    LaunchedEffect(effect) {
        when (effect) {
            is AuthContract.AuthEffect.NavigateToHome -> {
                onNavigateToOnboarding()
            }

            is AuthContract.AuthEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is AuthContract.AuthState.Error -> {
                Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(resendOtpEnabled) {
        if (!resendOtpEnabled) {
            while (countdownTime > 0) {
                delay(1000) // Delay for 1 second
                countdownTime -= 1
            }
            resendOtpEnabled = true
            countdownTime = 30
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.mail_box_img),
            contentDescription = null,
            modifier = Modifier.size(150.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "OTP Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2b472b),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Enter OTP sent to $phoneNumber",
            color = Color.Gray,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))

        BasicTextField(
            value = otpInput,
            onValueChange = {
                if (it.length <= 6) otpInput = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(6) { index ->
                    val char = otpInput.getOrNull(index)?.toString() ?: ""

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = char, fontSize = 20.sp, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (otpInput.length == 6) {
                    dispatch(AuthContract.AuthEvent.SubmitOtp(otpInput))
                }
            },
            colors = ButtonDefaults.buttonColors(
                if (otpInput.length == 6) Color(0xFF2b472b) else Color.Gray,
            ),
            enabled = otpInput.length == 6,
        ) {
            Text(text = "Verify OTP", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = {
                resendOtpEnabled = false
                dispatch(AuthContract.AuthEvent.ResendOtp(phoneNumber = phoneNumber, context as Activity))
            },
            enabled = resendOtpEnabled,
        ) {
            if (resendOtpEnabled) {
                Text(text = "Resend OTP", color = Color(0xFF2b472b))
            } else {
                Text(text = "Resend in $countdownTime sec", color = Color.Gray)
            }
        }
    }

    if (state is AuthContract.AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            LoadingScreen()
        }
    }
}

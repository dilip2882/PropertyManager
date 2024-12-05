package com.propertymanager.presentation.ui.auth

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import com.propertymanager.R
import com.propertymanager.presentation.components.ShowToast
import com.propertymanager.presentation.navigation.Destinations
import com.propertymanager.utils.CommonDialog
import com.propertymanager.utils.Response

@Composable
fun OtpScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    phoneNumber: String
) {
    val context = LocalContext.current
    var otpInput by remember { mutableStateOf("") }
    var hasSubmittedOtp by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }

    // State for OTP verification state
    val otpVerificationState = viewModel.otpVerificationState.value

    // State to handle error message
    var errorMessage by remember { mutableStateOf("") }

    // Handle OTP verification response
    LaunchedEffect(otpVerificationState, hasSubmittedOtp) {
        if (hasSubmittedOtp) {
            when (otpVerificationState) {
                is Response.Success -> {
                    isDialogVisible = false
                    navController.navigate(Destinations.HomeScreen.route)
                    hasSubmittedOtp = false
                }
                is Response.Error -> {
                    isDialogVisible = false
                    hasSubmittedOtp = false
                    errorMessage = otpVerificationState.message
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(otpInput) {
        if (otpInput.length == 6) {
            errorMessage = "" // Clear error message if a new OTP is being typed
        }
    }

    // Show the error toast if errorMessage is not empty
    if (errorMessage.isNotEmpty()) {
        ShowToast(message = errorMessage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.mail_box_img),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "OTP Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2b472b)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Enter OTP sent to $phoneNumber",
            color = Color.Gray,
            fontSize = 16.sp
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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val char = otpInput.getOrNull(index)?.toString() ?: ""

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = char, fontSize = 20.sp, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                // Reset errorMessage when user submits OTP again
                errorMessage = ""

                if (otpInput.length == 6) {
                    hasSubmittedOtp = true
                    isDialogVisible = true
                    viewModel.signInWithCredential(otpInput)
                } else {
                }
            },
            colors = ButtonDefaults.buttonColors(
                if (otpInput.length == 6) Color(0xFF2b472b) else Color.Gray
            ),
            enabled = otpInput.length == 6
        ) {
            Text(text = "Verify OTP", color = Color.White)
        }
    }

    if (isDialogVisible) {
        CommonDialog()
    }
}


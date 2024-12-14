package propertymanager.feature.auth.presentation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.hbb20.CountryCodePicker
import propertymanager.feature.auth.R
import propertymanager.feature.auth.presentation.mvi.AuthContract

@Composable
fun PhoneScreen(
    state: AuthContract.AuthState,
    effect: AuthContract.AuthEffect?,
    dispatch: (AuthContract.AuthEvent) -> Unit,
    onNavigateToOtpScreen: (String) -> Unit
) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }

    LaunchedEffect(effect) {
        when (effect) {
            is AuthContract.AuthEffect.NavigateToOtpScreen -> {
                onNavigateToOtpScreen(phoneNumber)
            }

            is AuthContract.AuthEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.mail_box_img),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "OTP Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2b472b)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "We will send you a One Time Password",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Text(
            text = "on this mobile number",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Country Code Picker
            AndroidView(
                factory = { context ->
                    CountryCodePicker(context).apply {
                        setDefaultCountryUsingPhoneCode(91) // Default
                        setOnCountryChangeListener {
                            countryCode = selectedCountryCodeWithPlus
                        }
                    }
                },
                modifier = Modifier
                    .width(70.dp)
                    .height(56.dp)
                    .padding(top = 5.dp),
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 10) phoneNumber = it
                },
                label = { Text(text = "Enter Mobile Number", color = Color(0xFF2b472b)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color(0xFF2b472b)),
                singleLine = true,
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val fullNumber = "$countryCode$phoneNumber"
                dispatch(AuthContract.AuthEvent.SubmitPhoneNumber(fullNumber, context as Activity))
            },
            colors = ButtonDefaults.buttonColors(
                if (phoneNumber.length == 10) Color(0xFF2b472b) else Color.Gray,
            ),
            enabled = phoneNumber.length == 10,
        ) {
            Text(text = "Generate OTP", color = Color.White)
        }
    }

    if (state is AuthContract.AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

package propertymanager.feature.auth.presentation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dilip.country_code_picker.CCPUtils
import com.dilip.country_code_picker.CCPValidator
import com.dilip.country_code_picker.CountryCodePickerTextField
import com.dilip.country_code_picker.PickerCustomization
import com.dilip.country_code_picker.ViewCustomization
import propertymanager.feature.auth.R
import propertymanager.feature.auth.presentation.mvi.AuthContract

@Composable
fun PhoneScreen(
    state: AuthContract.AuthState,
    effect: AuthContract.AuthEffect?,
    dispatch: (AuthContract.AuthEvent) -> Unit,
    onNavigateToOtpScreen: (String) -> Unit,
) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var country by remember { mutableStateOf(com.dilip.country_code_picker.Country.India) }
    val validatePhoneNumber = remember { CCPValidator(context = context) }
    var isNumberValid by remember(country, phoneNumber) {
        mutableStateOf(validatePhoneNumber(number = phoneNumber, countryCode = country.countryCode))
    }

    // Automatically fetch country based on device locale
    if (!LocalInspectionMode.current) {
        CCPUtils.getCountryAutomatically(context = context)?.let {
            country = it
        }
    }

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
            modifier = Modifier.size(150.dp),
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "OTP Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2b472b),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "We will send you a One Time Password",
            color = Color.Gray,
            fontSize = 16.sp,
        )

        Text(
            text = "on this mobile number",
            color = Color.Gray,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))

        CountryCodePickerTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = if (phoneNumber.isNotEmpty()) {
                {
                    IconButton(onClick = { phoneNumber = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            } else null,
            label = {
                Text(text = "Enter Mobile Number", style = MaterialTheme.typography.bodyMedium)
            },
            showError = !isNumberValid && phoneNumber.isNotEmpty(),
            shape = RoundedCornerShape(10.dp),
            onValueChange = { countryCode, value, isValid ->
                phoneNumber = value
                isNumberValid = isValid
            },
            number = phoneNumber,
            showSheet = true,
            selectedCountry = country,
            countryList = com.dilip.country_code_picker.Country.getAllCountries(),
            viewCustomization = ViewCustomization(
                showFlag = true,
                showCountryCode = true,
                showCountryName = true,
            ),
            pickerCustomization = PickerCustomization(
                showFlag = true,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = true,
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val fullNumber = "${country.countryCode}$phoneNumber"
                dispatch(AuthContract.AuthEvent.SubmitPhoneNumber(fullNumber, context as Activity))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isNumberValid && phoneNumber.isNotEmpty()) Color(0xFF2b472b) else Color.Gray,
            ),
            enabled = isNumberValid && phoneNumber.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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

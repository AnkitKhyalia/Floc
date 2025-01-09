package com.example.floc.authentication.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.floc.R
import com.example.floc.authentication.viewmodels.ForgotPasswordState
import com.example.floc.authentication.viewmodels.ForgotPasswordViewModel
import com.example.floc.authentication.viewmodels.UserLoginViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val forgotPasswordState by viewModel.forgotPasswordState.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    var showPassword by remember { mutableStateOf(value = false) }
    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = { email = it
                            viewModel.validateEmail(it)},
            label = { Text("Email") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            visualTransformation = if (showPassword) {

                VisualTransformation.None

            } else {

                PasswordVisualTransformation()

            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = Color(0xFF5CBDF8),
                unfocusedBorderColor = Color(0xFF5CBDF8),
                focusedLabelColor = Color(0xFF5CBDF8),
                unfocusedLabelColor = Color(0xFF5CBDF8),
                cursorColor = Color(0xFF5CBDF8),
//                            focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray


            ),
            trailingIcon = {
                if (showPassword) {
                    IconButton(onClick = { showPassword = false }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_visibility_24),
                            contentDescription = "hide_password"
                        )
                    }
                } else {
                    IconButton(
                        onClick = { showPassword = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_visibility_off_24),

                            contentDescription = "hide_password"
                        )
                    }
                }
            }
        )
        if (emailError != null) {
            Text(text = emailError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.sendPasswordResetEmail(email) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && forgotPasswordState !is ForgotPasswordState.Loading,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White,

                ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Reset Password")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (forgotPasswordState) {
            is ForgotPasswordState.Loading -> CircularProgressIndicator(modifier = Modifier.size(16.dp))
            is ForgotPasswordState.Success -> {
                Text("Password reset email sent. Check your inbox.", color = Color.Black, style = MaterialTheme.typography.labelMedium)
            }
            is ForgotPasswordState.Error -> {
                Text("Something went wrong.", color = MaterialTheme.colorScheme.error,style = MaterialTheme.typography.labelMedium)
            }
            else -> {
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Back to Login", color = Color.Black, style = MaterialTheme.typography.titleMedium)
        }
    }
}
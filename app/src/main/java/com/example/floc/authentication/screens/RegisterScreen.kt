package com.example.floc.authentication.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.floc.R
import com.example.floc.authentication.viewmodels.UserRegisterViewModel
import com.example.floc.data.User

import com.example.floc.util.Resource
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    userRegisterViewModel: UserRegisterViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val result = userRegisterViewModel.register.collectAsState(initial = Resource.Unspecified())
    val context = LocalContext.current
    val nameError by userRegisterViewModel.nameError.collectAsState()
    val emailError by userRegisterViewModel.emailError.collectAsState()
    val mobileNumberError by userRegisterViewModel.mobileNumberError.collectAsState()
    val passwordError by userRegisterViewModel.passwordError.collectAsState()
    var confirmPasswordError by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(value = false) }
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize().background(Color.White),

            contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp).verticalScroll(scrollState)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 10.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )

            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            userRegisterViewModel.validateName(it)
                                        },
                        label = { Text(text = "Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nameError != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color(0xFF5CBDF8),
                            unfocusedBorderColor = Color(0xFF5CBDF8),
                            focusedLabelColor = Color(0xFF5CBDF8),
                            unfocusedLabelColor = Color(0xFF5CBDF8),
                            cursorColor = Color(0xFF5CBDF8),
//                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = Color.Gray


                        )
                    )
                    if (nameError != null) {
                        Text(text = nameError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it
                                        userRegisterViewModel.validateEmail(it)},
                        label = { Text(text = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = emailError != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color(0xFF5CBDF8),
                            unfocusedBorderColor = Color(0xFF5CBDF8),
                            focusedLabelColor = Color(0xFF5CBDF8),
                            unfocusedLabelColor = Color(0xFF5CBDF8),
                            cursorColor = Color(0xFF5CBDF8),
//                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Gray


                        )
                    )
                    if (emailError != null) {
                        Text(text = emailError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it
                                        userRegisterViewModel.validateMobile(it)},
                        label = { Text(text = "Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = mobileNumberError != null,
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color(0xFF5CBDF8),
                            unfocusedBorderColor = Color(0xFF5CBDF8),
                            focusedLabelColor = Color(0xFF5CBDF8),
                            unfocusedLabelColor = Color(0xFF5CBDF8),
                            cursorColor = Color(0xFF5CBDF8),
//                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Gray


                        )
                    )
                    if (mobileNumberError != null) {
                        Text(text = mobileNumberError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it
                                        userRegisterViewModel.validatePassword(it)},
                        label = { Text(text = "Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showPassword) {

                            VisualTransformation.None

                        } else {

                            PasswordVisualTransformation()

                        },
                        shape = RoundedCornerShape(12.dp),
                        isError = passwordError != null,
                        placeholder = {Text("Example: Abcd@123", color = Color.Gray)},
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
                    if (passwordError != null) {
                        Text(text = passwordError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it
                            confirmPasswordError = it != password
                                        },
                        label = { Text(text = "Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        isError = confirmPasswordError,
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor = Color(0xFF5CBDF8),
                            unfocusedBorderColor = Color(0xFF5CBDF8),
                            focusedLabelColor = Color(0xFF5CBDF8),
                            unfocusedLabelColor = Color(0xFF5CBDF8),
                            cursorColor = Color(0xFF5CBDF8),
//                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = Color.Gray


                        )

                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center){

                    OutlinedButton(
                        onClick = {val id = UUID.randomUUID().toString()
                            val user = User(id, name, email, phoneNumber)

                            userRegisterViewModel.createAccountWithEmailAndPassword(user, password)
                                  },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier. padding( horizontal = 8.dp) ,
                        border = BorderStroke(0.dp, Color(0xFF5CBDF8)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5CBDF8) ,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFB9E2FC),
                            disabledContentColor = Color.Black,

                        ),
                        enabled = nameError == null && emailError == null && mobileNumberError == null && passwordError == null && name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phoneNumber.isNotEmpty() && !confirmPasswordError

                    ) {
                        Row(

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text("Register")
                        }
                    }
                    }
                }
            }

            when (result.value) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 4.dp
                    )
                }
                is Resource.Success -> {

                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate("Login_Screen")
                    }
                }
                is Resource.Error -> {
                    LaunchedEffect(Unit ) {
                        Toast.makeText(context, result.value.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is Resource.Unspecified -> {}
            }
        }
    }
}
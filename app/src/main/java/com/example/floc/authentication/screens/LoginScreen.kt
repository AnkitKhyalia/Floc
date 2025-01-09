package com.example.floc.authentication.screens

import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.floc.R
import com.example.floc.authentication.viewmodels.UserLoginViewModel
import com.example.floc.data.User

import com.example.floc.util.Resource
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    userLoginViewModel: UserLoginViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val login = userLoginViewModel.login.collectAsState()
    val passwordError by userLoginViewModel.passwordError.collectAsState()
    val emailError by userLoginViewModel.emailError.collectAsState()
    var showPassword by remember { mutableStateOf(value = false) }

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
                .padding(horizontal = 32.dp)
        )
        {
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
                        value = email,
                        onValueChange = { email = it
                                        userLoginViewModel.validateEmail(it)
                                        },
                        label = { Text(text = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = emailError != null,
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
                        value = password,
                        onValueChange = { password = it
                                        userLoginViewModel.validatePassword(it)
                                        },
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
                    TextButton(onClick = {
                        navController.navigate("Forgot_Password_Screen")
                    }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Forgot Password", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                        }

                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { navController.navigate("Register_Screen") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Don't have an account? Register", color = Color.Black, style = MaterialTheme.typography.labelMedium)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

//                    Button(
//                        onClick = { userLoginViewModel.loginWithEmailAndPassword(email, password) },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = ButtonDefaults.buttonColors(
////                            containerColor = MaterialTheme.colorScheme.primaryVariant,
//                            contentColor = MaterialTheme.colorScheme.onPrimary
//                        ),
//                        shape = RoundedCornerShape(8.dp)
//                    ) {
//                        Text(text = "Login")
//                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center){

                        OutlinedButton(
                            onClick = {
                                userLoginViewModel.loginWithEmailAndPassword(email, password)
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
                            enabled = email.isNotEmpty() && password.isNotEmpty()&& emailError == null && passwordError == null

                        ) {
                            Row(

                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text("Login")
                            }
                        }
                    }
                }
            }
//            Text(text = "ankit")
            when (login.value) {
                is Resource.Error -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, login.value.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 24.dp),
//                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 4.dp
                    )
                }

                is Resource.Success -> {

                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    }

                        navController.navigate("App_Navigation"){
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }

                }

                is Resource.Unspecified -> {}
            }
        }
    }
}
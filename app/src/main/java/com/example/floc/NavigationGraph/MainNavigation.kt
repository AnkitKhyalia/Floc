package com.example.floc.NavigationGraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.floc.authentication.screens.ForgotPasswordScreen
import com.example.floc.authentication.screens.LoginScreen
import com.example.floc.authentication.screens.RegisterScreen
import com.example.floc.ui.screens.AboutUsScreen
import com.example.floc.ui.screens.HomeScreen
import com.example.floc.ui.screens.PaymentScreen
import com.example.floc.ui.screens.TermsAndConditions

import com.example.floc.ui.screens.TutorialScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun MainNavigation(modifier: Modifier = Modifier,navController: NavHostController,minimizeApp:()->Unit) {
    val currentUser = Firebase.auth.currentUser
    var startDestination = "Auth_Graph"
    if(currentUser!=null){
        startDestination = "App_Navigation"
    }

    NavHost(navController = navController, startDestination = startDestination ) {
        navigation(route= "Auth_Graph",startDestination = "Login_Screen"){
            composable("Login_Screen"){
                LoginScreen(navController)
            }
            composable("Register_Screen"){
                RegisterScreen(navController)
            }
            composable("Forgot_Password_Screen"){
                ForgotPasswordScreen { navController.popBackStack() }
            }
        }
        navigation(route = "App_Navigation",startDestination = "Home_Screen" ){
            composable("Home_Screen"){
                HomeScreen(navController,minimizeApp)
            }
            composable("Payments_Screen"){
                PaymentScreen(navController)
            }
            composable("Tutorial_Screen"){
                TutorialScreen(navController)
            }
            composable("AboutUs_Screen"){
                AboutUsScreen(navController)
            }
            composable("Terms_And_Conditions_Screen"){
                TermsAndConditions(navController)
            }

        }



    }
    
}
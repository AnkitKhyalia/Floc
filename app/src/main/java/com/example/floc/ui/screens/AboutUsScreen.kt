package com.example.floc.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column



import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.floc.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    // The main column that contains both sections
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Your top bar content

            CenterAlignedTopAppBar(
                title = {

                    Text("About Us",


                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp // Add letter spacing here
                        )
                    )

                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }
                    ) {
                        Icon(ImageVector.vectorResource(id = R.drawable.baseline_arrow_back_ios_new_24), contentDescription = "", modifier = Modifier.size(20.dp))

                    }
                },
                colors = TopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.Gray,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.shadow(elevation = 4.dp)

            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Spacer(Modifier.height(0.dp).padding(innerPadding))
    Column(
        modifier = Modifier
            .fillMaxSize().padding( innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // About Us Section
        AboutUsSection()


        // Contact Us Section
        ContactUsSection(modifier = Modifier.weight(1f)){
            navController.navigate("Terms_And_Conditions_Screen")
        }
    }
    }
}

@Composable
fun AboutUsSection() {
    Card(
//        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(bottomEnd = 18.dp, bottomStart = 18.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
//        backgroundColor = Color(0xFFB3E5FC) // Light blue color
    ) {

//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
////            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
            Column(
//                verticalAlignment = Alignment.CenterVertically,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.Black)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {

                Image(painter = painterResource(R.drawable.logo), contentDescription = "App Logo", modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "FLOC",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White // Darker blue text
                    )
                }
//                Spacer(modifier = Modifier.height(8.dp))
                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                ) {

                    Text(
                        text = "FLoc is a cutting-edge mock location app designed to help developers and testers simulate GPS locations effortlessly. Whether you're testing location-based features or creating geofencing solutions, FLoc provides a seamless experience to change your location to anywhere in the world.",
                        textAlign = TextAlign.Justify,

                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth().padding(2.dp)
                    )


                }
            }

//        }
    }
}

@Composable
fun ContactUsSection(modifier: Modifier, navigation:()->Unit) {
    val context = LocalContext.current
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
//            containerColor = Color.Black
        ),
        border = BorderStroke(1.dp,Color.Black)


//        backgroundColor = Color(0xFFC8E6C9) // Light green color
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Contact Us",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Darker green text
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "For any questions, feedback, or support inquiries, please feel free to reach out to us.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            TextButton(
                onClick = navigation
            ) {
                Text(
                    text = "You can have a look at our privacy policy ",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4FC3F7),

                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:connect.floc@gmail.com")
                    }
                    if (emailIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(emailIntent)
                    }
                }
            ) {


                Text(
                    text = "Email: connect.floc@gmail.com",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AboutAndContactUsScreenPreview() {
//    AboutUsScreen()
//}
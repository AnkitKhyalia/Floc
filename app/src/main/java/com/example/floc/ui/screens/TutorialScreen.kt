package com.example.floc.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.floc.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen(navController: NavHostController) {
    val brands = listOf(
        "Samsung" to listOf(
            "1. Go to Settings",
            "2. Tap About device or About phone",
            "3. Tap Software information",
            "4. Tap Build number seven times",
            "5. Enter your pattern, PIN or password to enable the Developer options menu",
            "6. The Developer options menu will now appear in your Settings menu",
            "7. In Developer options, select Mock location app and choose com.example.floc"
        ),
        "Vivo" to listOf(
            "1. Go to Settings",
            "2. Tap More settings",
            "3. Tap About phone",
            "4. Tap Software version or Build number seven times",
            "5. Enter your pattern, PIN or password to enable the Developer options menu",
            "6. Go back to Settings, and the Developer options will now be visible",
            "7. In Developer options, select Mock location app and choose com.example.floc"
        ),
        "Oppo" to listOf(
            "1. Go to Settings",
            "2. Tap About phone",
            "3. Tap Version number seven times",
            "4. Enter your pattern, PIN or password to enable Developer options",
            "5. The Developer options will now be available in Settings",
            "6. In Developer options, select Mock location app and choose com.example.floc"
        ),
        "Google Pixel" to listOf(
            "1. Open Settings",
            "2. Scroll to About phone",
            "3. Tap Build number seven times",
            "4. Enter your PIN or password when prompted",
            "5. The Developer options will appear under System in Settings",
            "6. In Developer options, tap Select mock location app and choose com.example.floc"
        ),
        "OnePlus" to listOf(
            "1. Open Settings",
            "2. Scroll to About phone",
            "3. Tap Build number seven times",
            "4. Enter your PIN or password when prompted",
            "5. The Developer options will now be accessible in Settings",
            "6. In Developer options, go to Mock location app and select com.example.floc"
        )
    )
    val ytLinks = mapOf(
        "Samsung" to "https://youtube.com/@ankitkhyalia3194?si=JG98l96jVQNjw3_E",
        "Vivo" to "https://youtube.com/@ankitkhyalia3194?si=JG98l96jVQNjw3_E",
        "Oppo" to "https://youtube.com/@ankitkhyalia3194?si=JG98l96jVQNjw3_E",
        "Google Pixel" to "https://youtube.com/@ankitkhyalia3194?si=JG98l96jVQNjw3_E",
        "OnePlus" to "https://youtube.com/@ankitkhyalia3194?si=JG98l96jVQNjw3_E"
    )
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {

                    Text("Tutorial",
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

    ) { padding ->
        Column(
            modifier = Modifier

                .padding(padding).padding(16.dp)
                . clip(RoundedCornerShape(18.dp))
                .fillMaxWidth().fillMaxHeight()

                .background(Color(0XFFE6E6E6)).verticalScroll(scrollState),

        ) {
            brands.forEach { (brand, steps) ->
                val ytLink = ytLinks[brand]
                ExpandableCard(brand, steps,ytLink)
            }
        }
    }
}

@Composable
fun ExpandableCard(brand: String, steps: List<String>, ytLink: String? = null) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.clickable{
                expanded = !expanded
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                Text(text = brand, fontSize = 20.sp, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            if (expanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    ytLink?.let { link ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                          Icon(ImageVector.vectorResource(id = R.drawable.baseline_smart_display_24), contentDescription = "Youtube", modifier = Modifier.size(20.dp),tint= Color(
                              0xFFC40313
                          )
                          )
                              Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Tap to See Video Tutorial", fontSize = 18.sp,color = Color(0xFF00497d), modifier = Modifier.padding(vertical = 4.dp).clickable {
                            val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(ytIntent)
                        })
                        }
                    }
                    steps.forEach { step ->
                        Text(text = step, fontSize = 16.sp, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}


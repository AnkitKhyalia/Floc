package com.example.floc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.floc.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditions(navController: NavController) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {

                        Text("Privacy Policy",


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
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
//                Text(
//                    text = "Privacy Policy",
//                    style = MaterialTheme.typography.headlineMedium.copy(
//                        fontWeight = FontWeight.Bold
//                    ),
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Effective Date: October 7, 2024",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                PrivacySection(
                    title = "1. Information We Collect",
                    content = """
                    We collect both personal and non-personal information to provide and improve our services:
                    • Personal Information: Includes your name, email address, phone number, billing information, and other details you provide during purchases or inquiries.
                    • Non-Personal Information: Includes browser type, operating system, IP address, and browsing behavior on our website.
                """.trimIndent()
                )

                PrivacySection(
                    title = "2. How We Use Your Information",
                    content = """
                    Your information is used to:
                    • Process transactions and provide the services you request.
                    • Improve our website, products, and customer experience.
                    • Send important updates about your account, transactions, or changes to our policies.
                    • Provide targeted marketing based on your preferences, with your consent.
                """.trimIndent()
                )

                PrivacySection(
                    title = "3. How We Share Your Information",
                    content = """
                    We do not sell or rent your personal information. However, we may share it:
                    • With trusted third-party service providers who assist in delivering our services (e.g., payment processors).
                    • When required by law, such as to comply with legal obligations, prevent fraud, or protect our rights.
                    • With your explicit consent, for other purposes not listed here.
                """.trimIndent()
                )

                PrivacySection(
                    title = "4. Cookies and Tracking Technologies",
                    content = """
                    We use cookies to enhance your browsing experience and gather usage statistics:
                    • Essential Cookies: Necessary for the website to function properly.
                    • Analytics Cookies: Help us analyze how visitors interact with our website.
                    You can disable cookies in your browser settings, but some functionality may be affected.
                """.trimIndent()
                )

                PrivacySection(
                    title = "5. Data Security",
                    content = """
                    We implement industry-standard security measures to protect your data, including encryption, firewalls, and secure access controls. While we strive to safeguard your information, no system is entirely foolproof, and we cannot guarantee absolute security.
                """.trimIndent()
                )

                PrivacySection(
                    title = "6. Your Rights",
                    content = """
                    You have the right to:
                    • Access, update, or delete your personal information.
                    • Opt-out of marketing communications at any time.
                    • Request a copy of the data we hold about you.
                    To exercise these rights, please contact us at support@viraconsultancy.com.
                """.trimIndent()
                )

                PrivacySection(
                    title = "7. Third-Party Links",
                    content = """
                    Our website may contain links to external sites. We are not responsible for the privacy practices or content of these third-party websites. We encourage you to review their privacy policies before sharing any personal information.
                """.trimIndent()
                )

                PrivacySection(
                    title = "8. Children's Privacy",
                    content = """
                    Our services are not directed to children under 13, and we do not knowingly collect information from children. If we become aware that we have inadvertently collected data from a child, we will delete it promptly.
                """.trimIndent()
                )

                PrivacySection(
                    title = "9. Updates to This Privacy Policy",
                    content = """
                    We may update this Privacy Policy periodically to reflect changes in our practices or legal requirements. The updated policy will be posted on this page with a revised "Effective Date."
                """.trimIndent()
                )

                PrivacySection(
                    title = "10. Contact Us",
                    content = """
                    If you have any questions, concerns, or complaints regarding this Privacy Policy, please contact us:              
                    Email: connect.floc@gmail.com
                    
                """.trimIndent()
                )
            }
        }
}

@Composable
fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Justify
        )
    }
}
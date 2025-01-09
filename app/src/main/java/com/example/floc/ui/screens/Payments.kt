package com.example.floc.ui.screens

//import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.floc.Payments.PaymentViewModel
import com.example.floc.R
import com.example.floc.util.Resource
import com.example.floc.util.UiEvent
import com.example.floc.util.formatDate
import com.google.android.play.integrity.internal.i
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController, paymentViewModel: PaymentViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    val planStatus by  paymentViewModel.planStatus.collectAsState()
    val allPlans by paymentViewModel.allPlans.collectAsState()
    val selectedPlan  = paymentViewModel.selectedPlan.collectAsState()
    val paymentHistory by paymentViewModel.paymentHistory.collectAsState()
    val emailError by paymentViewModel.emailError.collectAsState()
    val mobileNumberError by paymentViewModel.mobileNumberError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet by remember{ mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        paymentViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message,withDismissAction = true)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        if (showBottomSheet) {

        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            // Your top bar content
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                title = {

                        Text("Payments",
//                            modifier = Modifier.fillMaxWidth(),
//                            textAlign = TextAlign.Center,

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
                    containerColor = Color(0xFFFFC3A1),
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                    scrolledContainerColor = Color(0xFFFFC3A1)


                ),

            )
        },
        bottomBar = {
            if(planStatus is Resource.Success && planStatus.data!= true && !showBottomSheet ){
                Button(
                    onClick = {
                        showBottomSheet = true
//                        paymentViewModel.startPayment(context,email,contact)
                    },
//                    enabled = emailError == null && mobileNumberError == null && selectedPlan.value != null && email.isNotEmpty() && contact.isNotEmpty(),

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor =  Color.White
                    )
                )
                 {
                    Text("Checkout", color = Color.White,
                        style = MaterialTheme.typography.titleMedium,

                        )
                }
            }

        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        BottomSheetScaffold(sheetShape = RoundedCornerShape(20.dp),//Rounded corners
            sheetTonalElevation = 2.dp,

            sheetPeekHeight = if(showBottomSheet) 300.dp else 0.dp,
            sheetContainerColor = Color.White,
            sheetContentColor = Color.Black,
            sheetSwipeEnabled = false,
            sheetContent = {
                when(planStatus) {
                    is Resource.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding( 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Resource.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding( 16.dp)

                        ){
                            Text("Something Went Wrong, Please Try Again")
                        }


                    }
                    is Resource.Success -> {
                        if ( planStatus.data !=true) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding( 16.dp)

                            ) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        paymentViewModel.validateEmail(it)
                                    },
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = emailError != null,
                                    singleLine = true,
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
                                if (emailError != null) {
                                    Text(text = emailError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = contact,
                                    onValueChange = { contact = it
                                        paymentViewModel.validateContact(  contact)},
                                    label = { Text("Mobile No") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = mobileNumberError != null,
                                    singleLine = true,
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
                                if (mobileNumberError != null) {
                                    Text(text = mobileNumberError!!, color = Color.Red, modifier = Modifier.padding(start = 16.dp))
                                }
                                Button(
                                    onClick = {
//                                        showBottomSheet = true
                        paymentViewModel.startPayment(context,email,contact)
                                    },
//                    enabled = emailError == null && mobileNumberError == null && selectedPlan.value != null && email.isNotEmpty() && contact.isNotEmpty(),

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black,
                                        contentColor =  Color.White
                                    )
                                )
                                {
                                    Text("Pay Now", color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,

                                        )
                                }
                            }
                        }
                    }
                    else -> {

                    }
                }
            }){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
//                .padding(16.dp)
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                // Display the current plan status




                Card(modifier = Modifier
                    .fillMaxWidth().height(200.dp)
                    .padding(horizontal=8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFC3A1)
                    ),
                    shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp)
                )
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,

                        ) {
                        when(planStatus){
                            is Resource.Loading ->{
                                CircularProgressIndicator()
                            }
                            is Resource.Error ->{
                                Text("Something Went Wrong, Please Try Again")
                            }
                            is Resource.Success ->{
                                if (planStatus.data == true) {
                                    Icon(
                                        painter = painterResource(R.drawable.verified),
                                        contentDescription = "",
                                        modifier = Modifier.size(40.dp),
                                        tint = Color.Unspecified

                                    )
                                    Text(
                                        "Your Plan is Active",
                                        modifier = Modifier.padding(start = 4.dp),
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 24.sp,
                                            letterSpacing = 1.sp
                                        ),
                                        color = Color.Black
                                    )
                                } else {
//                                Icon(
//                                    painter = painterResource(R.drawable.baseline_cancel_24),
//                                    contentDescription = "",
//                                    tint = Color(0xFFD50000)
//                                )
                                    Column {


                                        Text(
                                            "Choose your\nfloc plan:",
                                            modifier = Modifier.padding(4.dp),

                                            style = TextStyle(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 24.sp,
                                                letterSpacing = 1.sp
                                            ),
                                            color = Color.Black

                                        )
                                        Row(modifier =
                                        Modifier.padding(4.dp)) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_bolt_24),
                                                contentDescription = "",
                                                tint = Color.Black
                                            )
                                            Text(
                                                " Unlimited Mock Location",
                                                modifier = Modifier.padding(start = 4.dp),

                                                style = TextStyle(
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = Color.Black
                                            )


                                        }
                                        Row(modifier = Modifier.padding(4.dp)) {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_bolt_24),
                                                contentDescription = "",
                                                tint = Color.Black
                                            )
                                            Text(
                                                "Login (One Device at a time)",
                                                modifier = Modifier.padding(start = 4.dp),

                                                style = TextStyle(
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = Color.Black
                                            )


                                        }
                                    }
                                }
                            }
                            else->{

                            }
                        }



                    }
                }

                when(planStatus) {
                    is Resource.Loading -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp).clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                .fillMaxWidth().weight(1f).background(Color(0XFFE6E6E6)),

                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Error -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp).clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                .fillMaxWidth().weight(1f).background(Color(0XFFE6E6E6)),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Text("Something Went Wrong, Please Try Again")
                        }
                    }
                    is Resource.Success -> {

                            LazyColumn(
                                modifier = Modifier.padding(horizontal = 8.dp).clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)).weight(1f).background(Color(0XFFE6E6E6)) // Take available space
                            ) {
//                item {
//                    Text("Available Plans", style = MaterialTheme.typography.titleMedium)
//                }
                                if(planStatus.data !=true){
                                    items(allPlans.data?.size ?: 0) { index ->
                                        val plan = allPlans.data!![index]
                                        val isSelected = selectedPlan.value == plan
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .border(
                                                    BorderStroke(
                                                        2.dp,
                                                        if (isSelected) Color(0xFF687865) else Color.Transparent
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    paymentViewModel.handlePlanClick(plan)
                                                },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) Color.White else Color.White

                                            ),
                                            elevation = CardDefaults.cardElevation(2.dp)
//                        backgroundColor = if (isSelected) Color(0xFFEDE7F6) else Color.White,

                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(plan.name, style = MaterialTheme.typography.titleLarge, color = Color.Black)
//                                Text("Plan ID: ${plan.planId}", style = MaterialTheme.typography.bodyMedium)
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        "Rs. ${plan.price}",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = Color.Black
                                                    )
                                                    Text(
                                                        "${plan.durationInMonths} Months",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = Color(0xFF687865)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                item {

                                    Row {
                                        Text("Payment History",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(8.dp),
                                            color = Color.Black
                                        )
                                    }


                                }
                                item{
                                    if(paymentHistory.data ==null || paymentHistory.data?.isEmpty() == true){
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp).border(
                                                    BorderStroke(
                                                        2.dp,
                                                        Color.Black
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            colors = CardDefaults.cardColors(
                                                containerColor =  Color(0xFFE6E6E6)


                                            ),
//                            elevation = CardDefaults.cardElevation(2.dp)
                                        ){
                                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {

                                                Text("No Payment History",
                                                    style = MaterialTheme.typography.titleMedium, color = Color.Black)
                                            }
                                        }
                                    }
                                }
                                items(paymentHistory.data?.size ?: 0){index->
                                    val item = paymentHistory.data!![index]
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),

                                        colors = CardDefaults.cardColors(
                                            containerColor =  Color(0xFFE6E6E6),
                                            contentColor= Color.Black

                                        ),
//                            elevation = CardDefaults.cardElevation(2.dp)
                                    ){
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top=8.dp,bottom = 2.dp,start = 8.dp,end = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Text(text = "Id: ${item.paymentID}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding( top = 2.dp,bottom = 8.dp,start = 8.dp,end = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Column(
                                                horizontalAlignment = Alignment.Start,
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {

                                                if(item.status=="success"){
                                                    Row(verticalAlignment =    Alignment.CenterVertically){

//                                                        Icon(painter = painterResource(R.drawable.verify), contentDescription = "", tint = Color(0xFF558B2F))
                                                        Icon(
                                                            painter = painterResource(R.drawable.verified),
                                                            contentDescription = "",
                                                            modifier = Modifier.size(20.dp),
                                                            tint = Color.Unspecified


                                                        )
                                                        Spacer(Modifier.width(2.dp))
                                                        Text("Success")

                                                    }
                                                }
                                                else if(item.status=="failed"){
                                                    Row {

                                                        Icon(painter = painterResource(R.drawable.baseline_cancel_24), contentDescription = "", tint = Color(0xFFD50000))
                                                        Text("Failed")
                                                    }
                                                }
                                                else{
                                                    Row {

                                                        Icon(painter = painterResource(R.drawable.baseline_pending_24), contentDescription = "", tint = Color(
                                                            0xFFFF6D00
                                                        )
                                                        )
                                                        Text("Pending")
                                                    }
                                                }
                                            }
                                            Column(
                                                horizontalAlignment = Alignment.End
                                            ){

                                                Text(item.amount.toString(),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.Black
                                                )
                                                Text(
                                                    formatDate(item.paymentDate.toDate()),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color(0xFF687865)
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider()
                                }


                            }
                        }

                    is Resource.Unspecified -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp).clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                .fillMaxWidth().weight(1f).background(Color(0XFFE6E6E6)),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }








            }
        }

    }




}

@Preview
@Composable
private fun card() {
    Card( modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(painter = painterResource( R.drawable.baseline_check_circle_24), contentDescription = "")
            Text("Your Plan is Active", modifier = Modifier.padding(start = 4.dp), style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
            )

        }
    }

}


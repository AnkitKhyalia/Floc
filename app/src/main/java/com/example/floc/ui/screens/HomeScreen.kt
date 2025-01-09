package com.example.floc.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.floc.MainActivity
import com.example.floc.R
import com.example.floc.ViewModel.MockLocationViewModel
import com.example.floc.saveMockLocation
import com.example.floc.ui.LatLng
import com.example.floc.ui.MapScreen
import com.example.floc.util.Resource
import com.example.floc.util.UiEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val items = listOf<NavigationItem>(
    NavigationItem.Home,
    NavigationItem.Payments,
    NavigationItem.Tutorial,
    NavigationItem.AboutUS
)

 class NavigationItem(
     val route: String,
     val icon: Int,
     val title: String
) {
    companion object {
        val Home: NavigationItem =
            NavigationItem("Home_Screen", R.drawable.baseline_home_filled_24, "Home")
        val Payments: NavigationItem = NavigationItem("Payments_Screen", R.drawable.icons8_paypal_100, "Payments")
        val Tutorial: NavigationItem = NavigationItem("Tutorial_Screen", R.drawable.baseline_devices_24, "Tutorial")
        val AboutUS: NavigationItem = NavigationItem("AboutUS_Screen", R.drawable.baseline_info_outline_24, "About Us")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, minimizeApp:()->Unit, viewModel: MockLocationViewModel = hiltViewModel()) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isMockAppEnabled by viewModel.mockAppEnabled.collectAsState()

    var isAlertDialogOpen by remember { mutableStateOf(false) }
    val savedLocations by viewModel.savedLocations.collectAsState()
    val clickCounts by viewModel.clickCount.collectAsState()
    val isActiveUser by viewModel.isActiveUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as MainActivity


    LaunchedEffect(Unit) {
        if (!hasLocationPermissions(activity)) {
           activity.requestLocationPermissions()
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message,withDismissAction = true)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        if (drawerState.isOpen) {
            drawerState.close()
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.9f),drawerContainerColor = Color.White) {
//                Spacer(modifier = Modifier.height(12.dp))
                IconButton(onClick = {
                    scope.launch {
                        drawerState.close()
                    }

                },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ){

                    Icon(ImageVector.vectorResource(id = R.drawable.baseline_arrow_back_ios_new_24), contentDescription = "Back",modifier = Modifier.size(20.dp))
                    }

                }
                items.forEach {item->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        colors= CardDefaults.cardColors(
                            containerColor = Color.White,
                            contentColor = Color.Black

                        ),
                        onClick = {
                            val currentRoute = navController.currentBackStackEntry?.destination?.route
                            if (item.route != currentRoute) {
                                navController.navigate(item.route)
                            }
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    ){

                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp),verticalAlignment = Alignment.CenterVertically){
                        Spacer(Modifier.width(8.dp))
                        Icon(painter= painterResource(id = item.icon), contentDescription = item.title, tint = if(item.title =="Home") Color(0xFF00497d) else Color.Black, modifier = Modifier.size(24.dp))
                        Text(text = item.title,color= if(item.title =="Home") Color(0xFF00497d) else Color.Black, modifier = Modifier.padding(start = 8.dp).weight(1f))
                        if(item.title !="Home"){

                        Icon(ImageVector.vectorResource(id = R.drawable.baseline_arrow_forward_ios_24), contentDescription = item.title,modifier = Modifier.size(16.dp))
                        }
                    }
                    }


                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.logout()
                        navController.navigate("Auth_Graph") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }

                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black

                    )
                ){
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp),verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.baseline_logout_24),
                            contentDescription = "Logout",
                            tint = Color.Black
                        )
                        Text("Log Out", modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.weight(0.1f))



            }

        },

        gesturesEnabled= drawerState.isOpen
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(ImageVector.vectorResource(id = R.drawable.logo), contentDescription = "Location", modifier = Modifier.size(20.dp),tint= Color.Unspecified)

                            Text(
                                text = "FLOC",
//                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,

                                style = TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp,
                                    letterSpacing = 4.sp // Add letter spacing here
                                ),
                                color = Color(0xFF00497d)
                            )
                            Spacer(modifier = Modifier.width(4.dp))

                        }

                         },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                            }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            isAlertDialogOpen=true
                            viewModel.getSavedLocation()
                        }) {
                            Icon(ImageVector.vectorResource(id = R.drawable.baseline_history_24), contentDescription = "History")
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
        ) { intialPadding ->


            BottomSheetScaffold(sheetShape = RoundedCornerShape(20.dp),//Rounded corners
                sheetTonalElevation = 2.dp,

                sheetPeekHeight = 150.dp,
                sheetContainerColor = Color.White,
                sheetContentColor = Color.Black,
//                sheetDragHandle = { Icon(imageVector =ImageVector.vectorResource(id = R.drawable.baseline_double_arrow_24), contentDescription = "Home", modifier = Modifier.width(20.dp).height(15.dp)) },
                sheetContent = {
                    ModernLocationUI(
                        isLoading= isLoading,
                        latitude = latitude,
                        longitude = longitude,
                        nickName = nickName,
                        clickCounts = clickCounts,
                        isActiveUser = isActiveUser,
                        onLatitudeChange = { latitude = it },
                        onLongitudeChange = { longitude = it },
                        onNickNameChange = { nickName = it },
                        onGoClick = {

                            if (viewModel.checkLatLng(latitude,longitude)) {
                                selectedLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
                            }
                        },
                        onSaveClick = {
                            if(viewModel.checkLatLng(latitude,longitude)){
                                viewModel.saveLocation(latitude.toDouble(), longitude.toDouble(),nickName)
                            }

                        },
                        onStartClick = {
                            if (!hasLocationPermissions(activity)) {
                                Log.d("HomeScreen", "Permissions not granted, showing dialog")
//                                showPermissionDialog = true
                                activity.showPermissionSettingsDialog()
                            }
                            else
                            {

                                       if(viewModel.checkLatLng(latitude,longitude)){
                                           saveMockLocation(context, latitude.toDouble(), longitude.toDouble())
                                            viewModel.setMockLocation(context)

                                           if(isMockAppEnabled){
                                               if(!isActiveUser){
                                                       Log.d("screen", "HomeScreen: ${clickCounts}")
                                               viewModel.decrementClickCount()
                                                   if(clickCounts == 0){
                                                       navController.navigate("Payments_Screen")
                                                   }
                                               }
                                               minimizeApp()
                                           }
                                       }
                            }

                                       },
                        onStopClick = {  viewModel.stopMockLocationService(context) }
                    )



                }) {


                Spacer(modifier = Modifier.padding(intialPadding))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
//                    .padding(innerPadding)
                ) {
                    // Spacer to keep map content below the TopAppBar and above BottomSheet
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display the MapScreen
                    MapScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        initialLatLng = selectedLatLng,
                        onMapClick = { latLng ->

                            latitude = latLng.latitude.toString()
                            longitude = latLng.longitude.toString()
                        }

                    )
                }
                if(isAlertDialogOpen){
                    BasicAlertDialog(
                        onDismissRequest = { isAlertDialogOpen = false },
                    ) {
                        when(savedLocations){
                            is Resource.Unspecified-> {}
                            is Resource.Success -> {
                                val locations = savedLocations.data
                                if(locations.isNullOrEmpty()){
                                    Card(
                                        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f).padding(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {

                                        Text("No Saved Locations")
//                                            Text(text = "Something Went Wrong, Please Try Again Later",textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                                else{

                                LazyColumn(modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.8f).clip( RoundedCornerShape(20.dp)).background(Color.White),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    reverseLayout = true,
                                    contentPadding = PaddingValues(4.dp),



                                    ) {

                                    items(locations.orEmpty()) { location ->
                                        Card(
                                            onClick = {
                                                latitude = location.latitude.toString()
                                                longitude = location.longitude.toString()
                                                nickName = location.nickName ?: ""
                                                selectedLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
                                                isAlertDialogOpen= false
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                           colors = CardDefaults.cardColors(
                                               containerColor = Color.White,
                                               contentColor = Color.Black

                                           )
                                        ) {
                                            Column(modifier= Modifier.padding(8.dp)) {
                                                Text("Nickname: ${location.nickName}", fontWeight = FontWeight.Bold)
                                                Text("Latitude: ${location.latitude}")
                                                Text("Longitude: ${location.longitude}")
                                            }

                                        }
                                        HorizontalDivider()
                                    }
                                }
                                }
                            }
                            is Resource.Loading -> {
                                Column(modifier= Modifier
                                    .fillMaxWidth(0.8f)
                                    .fillMaxHeight(0.8f) ) { CircularProgressIndicator(color = Color(0xFF5CBDF8)) }

                            }
                            is Resource.Error -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f).padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {

                                        Text(text = "Something Went Wrong, Please Try Again Later")
                                    }
                                }

                            }

                        }

                    }
                }

            }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernLocationUI(
    isLoading: Boolean,
    latitude: String,
    longitude: String,
    nickName: String,
    clickCounts: Int,
    isActiveUser: Boolean,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onNickNameChange: (String) -> Unit,
    onGoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isLoading){
            CircularProgressIndicator(color = Color(0xFF5CBDF8))
        }
        else{


        if(!isActiveUser){

        Text(
            text = "Free Start Clicks Left: $clickCounts",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = latitude,
                onValueChange = onLatitudeChange,
                label = { Text("Latitude") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(

                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray

                    )
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = onLongitudeChange,
                label = { Text("Longitude") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(

                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray


                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nickName,
            onValueChange = onNickNameChange,
            label = { Text("Nickname") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray


            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onSaveClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
//                border = BorderStroke(2.dp, Color(0xFF5CBDF8)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_add_24),
                    contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Save")
            }
            Spacer(Modifier.width(16.dp))
            OutlinedButton(
                onClick = onGoClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
//                border = BorderStroke(2.dp, Color(0xFF5CBDF8)),
                colors = ButtonDefaults.buttonColors( containerColor = Color.Black ,  contentColor = Color.White),
                 ) {
                Row(

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_forward_24), contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Go")
                }
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onStopClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
//                border = BorderStroke(2.dp, Color(0xFFEB726D)),
                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFEB726D),
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_pause_circle_outline_24),
                    contentDescription = null
                )
                Spacer(Modifier.width(4.dp))
                Text("Stop")
            }
            Spacer(Modifier.width(16.dp))
            OutlinedButton(
                onClick = onStartClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
//                border = BorderStroke(2.dp, Color(0xFF4AB072)),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
//                    containerColor = Color(0xFF4AB072)
                    containerColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_play_circle_outline_24),
                    contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Start")
            }

        }
        }
    }
}
@Preview
@Composable
fun PreviewModernLocationUI() {

    OutlinedButton(
        onClick = {},
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier. padding( horizontal = 8.dp) ,
        border = BorderStroke(2.dp, Color(0xFF5CBDF8)),
        colors = ButtonDefaults.buttonColors( containerColor = Color(0xFF5CBDF8).copy(0.2f) ,  contentColor = Color(0xFF5CBDF8)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), // Add custom padding here
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Go")
        }
    }
}
fun hasLocationPermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.FOREGROUND_SERVICE
            ) == PackageManager.PERMISSION_GRANTED
}


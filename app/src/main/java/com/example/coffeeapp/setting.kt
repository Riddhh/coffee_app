package com.example.coffeeapp

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.provider.Settings
import androidx.compose.ui.res.stringResource

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(navController: NavHostController) {

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var isOn by remember { mutableStateOf(false) }
    var isOnn by remember { mutableStateOf(false) }
    //#FBF3EA screen
    val bg = Color(0xFFFBF3EA)
    val textt = Color(0xFF5A3A26)
    val cardd = Color(0xFFFFF7EE)
    val border_color = Color(0xFFE3CDB8)
    val off = Color(0xFFD8CFC6)

    Scaffold(
        containerColor = bg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.setting), fontWeight = FontWeight.Bold, fontSize = 25.sp, textAlign = TextAlign.Center) },

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bg,              // ✅ top bar background
                    titleContentColor = textt     // ✅ title color
                )
            )

        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)     // ✅ important (prevents overlap with topbar)
                .padding(10.dp)
        ) {


            Text(
                stringResource(R.string.account_detail),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 15.dp, bottom = 10.dp),
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textt)
            )

            Button(
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                onClick = { navController.navigate("personal_info") },
                border = BorderStroke(2.5.dp, border_color ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardd ,          // ✅ button background
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = textt
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.personal_information),
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textt)
                    )
                }
            }

            Button(
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                onClick = { navController.navigate("order_history") },
                border = BorderStroke(2.5.dp, border_color ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardd ,          // ✅ button background
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = textt
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Order History",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textt)
                    )
                }
            }

            Button(
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                onClick = { navController.navigate("transactionHistory") },
                border = BorderStroke(2.5.dp, border_color ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardd ,          // ✅ button background
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = textt
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Transaction History",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textt)
                    )
                }
            }

            Text(
                stringResource(R.string.app_preference),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 15.dp, bottom = 10.dp),
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
            )

            Button(
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp
                ),
                onClick = {
                    navController.navigate("language")
                },
                border = BorderStroke(2.5.dp, border_color ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardd ,          // ✅ button background
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = textt
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Language",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold,color = textt)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .height(60.dp)
                    .border(
                        width = 2.dp,
                        color = border_color,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .shadow(elevation = 2.dp,  shape = RoundedCornerShape(15.dp))
                    .background(
                        color = Color(0xFFFFF7EE),
//                        shape = RoundedCornerShape(15.dp),
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = textt,
                            modifier = Modifier.size(30.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Notification",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textt
                            )
                        )
                    }
                    Switch(
                        checked = isOn,
                        onCheckedChange = { isOn = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = off,
                            uncheckedThumbColor = Color(0xFF7b4a3a),
                            uncheckedTrackColor = Color(0xFFc8a48a) ,
                            checkedTrackColor = textt,
//                            uncheckedBorderColor = Color(0xFF4e2b1e)
                        )
                    )
                }
            }

            Button(
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp

                ),
                onClick = { navController.navigate("aboutus") },
                border = BorderStroke(2.5.dp, border_color ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardd ,          // ✅ button background
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Contacts,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = textt
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "About Us",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textt)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                ),
                onClick = { showDialog = true },
                border = BorderStroke(2.5.dp, border_color ),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardd ,          // ✅ button background
                ),
            ) {
                Text(
                    "Logout",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textt)
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("setting") { inclusive = true }
                            }
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) { Text("No") }
                    }
                )
            }
        }
    }
}

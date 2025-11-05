package com.example.coffeeapp

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingPage(navController: NavHostController){
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var isOn by remember { mutableStateOf(false) }
    var isOnn by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Text(
            "Guest name",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )

        )
        Text(
            "Account Detail",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 15.dp),
            style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        )
        Button(
            onClick = { /* Handle click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp),

            contentPadding = PaddingValues(0.dp) // 👈 remove internal padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp), // optional: add some left spacing
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
//                    painter = painterResource(id = R.drawable.user),
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Personal Information", style = TextStyle(
                        fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Button(
            onClick = { navController.navigate("transactionHistory") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp),

            contentPadding = PaddingValues(0.dp) // 👈 remove internal padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp), // optional: add some left spacing
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Transaction History", style = TextStyle(
                        fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Text(
            "App Preference",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 15.dp),
            style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        )
        Button(
            onClick = { /* Handle click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp),

            contentPadding = PaddingValues(0.dp) // 👈 remove internal padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp), // optional: add some left spacing
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
//                    painter = painterResource(id = R.drawable.user),
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Language", style = TextStyle(
                        fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                )
            }
        }
//        Button(
//            onClick = { /* Handle click */ },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp)
//                .height(60.dp),
//            shape = RoundedCornerShape(10.dp),
//
//            contentPadding = PaddingValues(0.dp) // 👈 remove internal padding
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 16.dp), // optional: add some left spacing
//                horizontalArrangement = Arrangement.Start,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Notifications,
//                    contentDescription = null,
//                    modifier = Modifier.size(30.dp)
//                )
//                Spacer(Modifier.width(10.dp))
//                Text(
//                    "Notification", style = TextStyle(
//                        fontSize = 20.sp, fontWeight = FontWeight.Bold
//                    )
//                )
//
//            }
//        }
        Box(
            modifier = Modifier.padding(10.dp)
                .height(60.dp)
                .background(color = Color(0xFF465e91),
                    shape = RoundedCornerShape(10.dp),
                )) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 12.dp),

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Notification",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
                Switch(
                    checked = isOn,
                    onCheckedChange = { isOn = it },

                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Red,
                        uncheckedThumbColor = Color.DarkGray,
                        checkedTrackColor = Color.Cyan
                    )
                )
            }
        }
        Box(
            modifier = Modifier.padding(10.dp)
                .height(60.dp)
                .background(color = Color(0xFF465e91),
                    shape = RoundedCornerShape(10.dp),
                )) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 12.dp),

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Dark Mode",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
                Switch(
                    checked = isOnn,
                    onCheckedChange = { isOnn = it },

                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Red,
                        uncheckedThumbColor = Color.DarkGray,
                        checkedTrackColor = Color.Cyan
                    )
                )
            }
        }
        Button(
            onClick = { navController.navigate("aboutus") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp),

            contentPadding = PaddingValues(0.dp) // 👈 remove internal padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp), // optional: add some left spacing
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Contacts,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "About Us", style = TextStyle(
                        fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                "Logout", style = TextStyle(
                    fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
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
                        auth.signOut()
                        Toast.makeText(
                            context, "Logged out", Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("login") {
                            popUpTo("setting") { inclusive = true }
                        }
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }


            )
        }

    }
}
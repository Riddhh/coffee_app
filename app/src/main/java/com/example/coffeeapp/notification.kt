package com.example.coffeeapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notification(navController: NavHostController) {
    val bg = Color(0xFFF5ECE4)
    val textBrown = Color(0xFF381D12)
    val subtext = Color(0xFFA68C7E)
    val btn = Color(0xFF5C3321)

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        "Notification",
                        style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bg,              // ✅ top bar background
                    titleContentColor = textBrown     // ✅ title color
                ),
                actions = {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .width(150.dp)
                            .height(35.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = btn ,          // ✅ button background
                        ),

                    ) {
                        Text(
                            "Clear all",
                            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(15.dp)
                .background(bg)
        ) {
            Spacer(Modifier.height(30.dp))

            ListItem(
                colors = ListItemDefaults.colors(containerColor = bg),
                headlineContent = {
                    Text(
                        "Big Promotion",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textBrown)
                    )
                },
                supportingContent = {
                    Text(
                        "Buying 2 cups will get 50% discount. Come to our shop to get 50%",
                        color = subtext,
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = btn,
                        modifier = Modifier.size(80.dp)
                    )
                },
                trailingContent = {
                    Box(modifier = Modifier.padding(top = 15.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = btn,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        }
    }
}

package com.example.coffeeapp

import android.app.Notification
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Notification(navController: NavHostController ){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(15.dp)) {
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                }
                Text(
                    "Notification", style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Button(onClick = {}, modifier = Modifier
                .width(150.dp)
                .height(35.dp), shape = RoundedCornerShape(12.dp)
            ) {
                Text("Clear all", style = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                ))
            }
        }
        Spacer(Modifier.height(30.dp))
        ListItem(
            headlineContent = {
                Text("Big Promotion",style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ))
            },
            supportingContent = {
                Text("Buying 2 cups will get 50% discount. Come to our shop to get 50%",style = TextStyle(
                    fontSize = 16.sp,

                    ))
            },
            leadingContent = {
                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(80.dp))
            },
            trailingContent = {

                Box(
                    modifier = Modifier.padding(top = 15.dp),
//                    contentAlignment = Alignment.Center

                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)

                    )
                }
            }


        )

    }

}
package com.example.coffeeapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Recommend() {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ListItem(
            headlineContent = {
                Text(
                    "Iced Latte",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            supportingContent = {
                Text(
                    "3.21$",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFFA4A9AD)
                )
            },
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.latte),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit

                )
            },
            trailingContent = {
                Button(onClick = {}) {
                    Text("Shop Now")
                }
            }


        )
        ListItem(
            headlineContent = {
                Text(
                    "Iced Latte",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            supportingContent = {
                Text(
                    "3.21$",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFFA4A9AD)
                )
            },
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.latte),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit

                )
            },
            trailingContent = {
                Button(onClick = {}) {
                    Text("View More")
                }
            }


        )
        ListItem(
            headlineContent = {
                Text(
                    "Iced Latte",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            supportingContent = {
                Text(
                    "3.21$",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFFA4A9AD)
                )
            },
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.latte),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit

                )
            },
            trailingContent = {
                Button(onClick = {}) {
                    Text("Shop Now")
                }
            }


        )
    }
}
package com.example.coffeeapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun Login(navController: NavHostController) {
    val context = LocalContext.current
    val vm: com.example.coffeeapp.auth.AuthViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel()
    val state = vm.state.collectAsState().value

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var pwdVisible by rememberSaveable { mutableStateOf(false) }
    val btn = Color(0xFF5C3321)
    val txtbtn = Color(0xFF5C3321)
    val textBrown = Color(0xFF381D12)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cafe),
            contentDescription = null
        )
        Text(
            "Welcome Back! Please Login to Continue",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = textBrown
        )

        Spacer(Modifier.height(24.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A3A26),
                unfocusedBorderColor = Color(0xFFBCA89B),
                cursorColor = Color(0xFF5A3A26),
                focusedLabelColor = Color(0xFF5A3A26)
            ),
        )

        Spacer(Modifier.height(18.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5A3A26),
                unfocusedBorderColor = Color(0xFFBCA89B),
                cursorColor = Color(0xFF5A3A26),
                focusedLabelColor = Color(0xFF5A3A26)
            ),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (pwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (pwdVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { pwdVisible = !pwdVisible }) {
                    Icon(icon, contentDescription = "Toggle password visibility", tint = Color(0xFFa77b5b))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(25.dp))

        // LOGIN BUTTON
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = btn ,          // ✅ button background
            ),
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
                } else {
                    vm.login(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state.loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Login", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ERROR TEXT (if any)
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }

        // REGISTER NAV
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account?",color = txtbtn, fontSize = 18.sp)
            TextButton(

                onClick = { navController.navigate("register") }) {
                Text("Register", color = txtbtn, fontSize = 18.sp)
            }
        }
    }

    // Navigate when authenticated
    if (state.isAuthed) {
        LaunchedEffect(Unit) {
            navController.navigate("home?fromLogin=true") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}

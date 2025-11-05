package com.example.coffeeapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(navController: NavHostController){
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var loading by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(30.dp)) {
        Image(
            painter = painterResource(R.drawable.cafe),
            contentDescription = null,
            modifier = Modifier.width(250.dp).height(250.dp).align(Alignment.CenterHorizontally)

        )
        Text("Welcome Back! Please Login to Continue",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
            )
        Spacer(Modifier.height(15.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {email= it},
            label = { Text("Enter Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(15.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Enter Password") },
            singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { visible = !visible }) {
                    Icon(icon, contentDescription = "Toggle password visibility")
                }
            },

            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        TextButton(onClick = {navController.navigate("forget")}, modifier = Modifier.align(Alignment.End)) {
            Text("Forgot password?")
        }
        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()){
                loading = true
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task->
                        loading= false
                        if (task.isSuccessful){
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                        }
                        else{
                            Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
            else{
                Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }



        },
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).height(50.dp), shape = RoundedCornerShape(14.dp)
            ) {
//            Text("Login", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (loading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            else Text("Login", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(15.dp))
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Don't have account?")
            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text("Register")
            }
        }

    }
}
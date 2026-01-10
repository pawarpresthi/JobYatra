package com.example.jobwibe

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: (Boolean) -> Unit) {
    val context = LocalContext.current

    // UI State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isRecruiter by remember { mutableStateOf(false) } // The Toggle State

    // Toggle State: Login vs Sign Up
    var isSignUpMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF0A66C2), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // UPDATED: "JY" for JobYatra
            Text("JY", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic Header
        Text(
            text = if (isSignUpMode) "Create Account" else "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Inputs
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = null)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hiring Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I am hiring (Recruiter)", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = isRecruiter, onCheckedChange = { isRecruiter = it })
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Primary Button (Login or Sign Up)
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    val savedUsers = StorageHelper.loadUsers(context)

                    if (isSignUpMode) {
                        // --- SIGN UP LOGIC ---
                        if (savedUsers.any { it.email == email }) {
                            Toast.makeText(context, "User already exists!", Toast.LENGTH_SHORT).show()
                        } else {
                            val newUser = User(email, password, isRecruiter)
                            savedUsers.add(newUser)
                            StorageHelper.saveUsers(context, savedUsers)

                            // 1. SAVE SESSION (Important!)
                            CurrentUser.email = email
                            CurrentUser.isRecruiter = isRecruiter

                            Toast.makeText(context, "Account Created! Logging in...", Toast.LENGTH_SHORT).show()
                            onLoginSuccess(isRecruiter)
                        }
                    } else {
                        // --- LOGIN LOGIC ---
                        val foundUser = savedUsers.find { it.email == email && it.password == password }

                        if (foundUser != null) {
                            // Validation: Check if the Toggle matches the Saved Role
                            if (foundUser.isRecruiter != isRecruiter) {
                                val correctRole = if (foundUser.isRecruiter) "Recruiter" else "Job Seeker"
                                Toast.makeText(context, "Login Failed: This account is registered as a $correctRole.", Toast.LENGTH_LONG).show()
                            } else {
                                // 2. SAVE SESSION (Important!)
                                CurrentUser.email = foundUser.email
                                CurrentUser.isRecruiter = foundUser.isRecruiter

                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                onLoginSuccess(foundUser.isRecruiter)
                            }
                        } else {
                            Toast.makeText(context, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A66C2))
        ) {
            Text(if (isSignUpMode) "Sign Up" else "Login", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Mode Text
        Text(
            text = if (isSignUpMode) "Already have an account? Login" else "New here? Create Account",
            color = Color(0xFF0A66C2),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { isSignUpMode = !isSignUpMode }
        )
    }
}
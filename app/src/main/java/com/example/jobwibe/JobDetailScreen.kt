package com.example.jobwibe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JobDetailScreen(
    jobId: String,
    onBackClick: () -> Unit,
    onApplySuccess: () -> Unit = {}
) {
    val job = mockJobs.find { it.id == jobId } ?: mockJobs[0]
    var showDialog by remember { mutableStateOf(false) }

    // Check if I have already applied
    val isApplied = job.applicants.contains(CurrentUser.email)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = job.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(text = "${job.company} • ${job.salary}", style = MaterialTheme.typography.titleLarge, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "AI Match Analysis", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Great match for your profile!")
                Text("• Skills overlap: 94%")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // APPLY BUTTON
        Button(
            onClick = {
                if (!isApplied) {
                    // 1. Add MY EMAIL to the job's applicant list
                    job.applicants.add(CurrentUser.email)

                    // 2. Add to Recruiter Candidates List (UPDATED WITH EMAIL)
                    mockCandidates.add(0, Candidate(
                        id = "user_${System.currentTimeMillis()}",
                        name = "User: ${CurrentUser.email.substringBefore("@")}",
                        email = CurrentUser.email, // <--- NEW FIELD ADDED HERE
                        role = job.title,
                        experience = "Fresher",
                        matchScore = (85..99).random(),
                        skills = listOf("Java", "Kotlin", "Android"),
                        status = "New"
                    ))

                    onApplySuccess()
                    showDialog = true
                }
            },
            enabled = !isApplied,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isApplied) Color.Gray else Color.Black
            )
        ) {
            Text(
                if (isApplied) "Applied Successfully" else "Easy Apply",
                fontSize = 18.sp
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32)) },
            title = { Text("Application Sent!") },
            text = { Text("Your resume has been sent to ${job.company}.") },
            confirmButton = {
                TextButton(onClick = { showDialog = false; onBackClick() }) {
                    Text("Done")
                }
            }
        )
    }
}
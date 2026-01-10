package com.example.jobwibe

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateDetailScreen(
    candidateId: String,
    onBackClick: () -> Unit,
    onHire: () -> Unit,
    onReject: () -> Unit,
    onSchedule: () -> Unit
) {
    val context = LocalContext.current
    // Find candidate by ID
    val candidate = mockCandidates.find { it.id == candidateId } ?: mockCandidates.firstOrNull()

    if (candidate == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Candidate not found") }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applicant Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // ACTION BAR: Hire / Reject / Schedule
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reject
                Button(
                    onClick = {
                        onReject()
                        Toast.makeText(context, "Candidate Rejected", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) { Icon(Icons.Default.Close, null); Spacer(Modifier.width(4.dp)); Text("Reject") }

                // Schedule
                Button(
                    onClick = onSchedule,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF0A66C2)),
                    modifier = Modifier.weight(1f)
                ) { Icon(Icons.Default.VideoCall, null); Spacer(Modifier.width(4.dp)); Text("Meet") }

                // Hire
                Button(
                    onClick = {
                        onHire()
                        Toast.makeText(context, "Hired! Notification sent.", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) { Icon(Icons.Default.Check, null); Spacer(Modifier.width(4.dp)); Text("Hire") }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // HEADER PROFILE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.LightGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = candidate.name.take(1),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(candidate.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(candidate.role, color = Color.Gray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Match Score Badge
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${candidate.matchScore}% Match", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI INSIGHTS
            PaddingBox(title = "AI Analysis") {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("• Strong background in ${candidate.skills.firstOrNull() ?: "Tech"}.", style = MaterialTheme.typography.bodyMedium)
                        Text("• Experience level (${candidate.experience}) fits the senior role requirements.", style = MaterialTheme.typography.bodyMedium)
                        Text("• Consistent employment history.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // SKILLS
            PaddingBox(title = "Top Skills") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    candidate.skills.forEach { skill ->
                        SuggestionChip(onClick = {}, label = { Text(skill) })
                    }
                }
            }

            // CONTACT INFO
            PaddingBox(title = "Contact Information") {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(candidate.email, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ABOUT SECTION (Dummy Data)
            PaddingBox(title = "About Applicant") {
                Text(
                    "Passionate ${candidate.role} with ${candidate.experience} of experience building scalable applications. " +
                            "Looking for a challenging role where I can utilize my skills in ${candidate.skills.joinToString(", ")}.",
                    color = Color.Gray,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
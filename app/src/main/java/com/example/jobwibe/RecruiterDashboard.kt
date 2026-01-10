package com.example.jobwibe

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush // <--- FIXED
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterDashboard(
    onPostJobClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onMeetingsClick: () -> Unit,
    onCandidateClick: (String) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedCandidate by remember { mutableStateOf<Candidate?>(null) }

    // Dialog Inputs
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("zoom.us/j/123456") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recruiter Portal", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    IconButton(onClick = onMeetingsClick) {
                        Icon(Icons.Default.VideoCall, "Interviews", tint = Color(0xFF0A66C2))
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.ExitToApp, "Logout", tint = Color.Red)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPostJobClick,
                containerColor = Color(0xFF0A66C2),
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, "Post Job") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F2F5))
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("Top Candidates", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(mockCandidates) { candidate ->
                CandidateCard(
                    candidate = candidate,
                    onClick = { onCandidateClick(candidate.id) },
                    onHire = {
                        candidate.status = "Hired"
                        val newMsg = ChatMessage("msg_${System.currentTimeMillis()}", "Hiring Team", "Congratulations! You've been hired! 🎉", "Just now", 1)
                        mockChats.add(0, newMsg)
                        StorageHelper.saveCandidates(context, mockCandidates)
                        StorageHelper.saveChats(context, mockChats)
                        Toast.makeText(context, "Hired!", Toast.LENGTH_SHORT).show()
                    },
                    onSchedule = {
                        selectedCandidate = candidate
                        showDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showDialog && selectedCandidate != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Schedule Interview") },
            text = {
                Column {
                    Text("For: ${selectedCandidate!!.name}")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = link, onValueChange = { link = it }, label = { Text("Link") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newMeeting = Meeting(System.currentTimeMillis().toString(), selectedCandidate!!.name, selectedCandidate!!.email, CurrentUser.email, date, time, link)
                    val allMeetings = StorageHelper.loadMeetings(context)
                    allMeetings.add(newMeeting)
                    StorageHelper.saveMeetings(context, allMeetings)
                    Toast.makeText(context, "Interview Scheduled!", Toast.LENGTH_SHORT).show()
                    showDialog = false
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun CandidateCard(candidate: Candidate, onClick: () -> Unit, onHire: () -> Unit, onSchedule: () -> Unit) {
    if (candidate.status == "New") {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White, Color(0xFFF3E5F5))
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = Color(0xFF6200EA)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = candidate.name.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFF00E676), CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(candidate.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A237E))
                    Text(candidate.role, color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "★ ${candidate.matchScore}% Match",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSchedule,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF6200EA)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6200EA))
                ) {
                    Text("Schedule")
                }

                Button(
                    onClick = onHire,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
                ) {
                    Text("Hire")
                }
            }
        }
    }
}
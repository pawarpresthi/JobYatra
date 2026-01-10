package com.example.jobwibe

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var myMeetings by remember { mutableStateOf<List<Meeting>>(emptyList()) }

    // Load meetings relevant to ME
    LaunchedEffect(Unit) {
        val allMeetings = StorageHelper.loadMeetings(context)
        myMeetings = if (CurrentUser.isRecruiter) {
            allMeetings.filter { it.recruiterEmail == CurrentUser.email }
        } else {
            allMeetings.filter { it.candidateEmail == CurrentUser.email }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upcoming Interviews", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (myMeetings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No interviews scheduled yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(myMeetings) { meeting ->
                    MeetingCard(meeting)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MeetingCard(meeting: Meeting) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF0A66C2))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if(CurrentUser.isRecruiter) "Interview with ${meeting.candidateName}" else "Interview with Recruiter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text("${meeting.date} @ ${meeting.time}", color = Color.Gray, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Open the link in browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://${meeting.link}"))
                    try { context.startActivity(intent) } catch (e: Exception) {}
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.VideoCall, null, tint = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Join Video Call", color = Color(0xFF2E7D32))
            }
        }
    }
}
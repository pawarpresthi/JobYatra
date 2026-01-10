package com.example.jobwibe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Serializable

// 1. Make Serializable so we can save it
data class ChatMessage(
    val id: String,
    val sender: String,
    val lastMsg: String,
    val time: String,
    val unread: Int
) : Serializable

// 2. Use 'mutableStateListOf' so it updates live across screens
val mockChats = mutableStateListOf(
    ChatMessage("1", "Google HR", "Your resume has been shortlisted! When can...", "10:30 AM", 2),
    ChatMessage("2", "Spotify Recruiter", "Thanks for applying. We will get back to...", "Yesterday", 0),
    ChatMessage("3", "Netflix Team", "Are you available for a Kotlin technical...", "Mon", 5)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
        ) {
            items(mockChats) { chat ->
                ChatRow(chat)
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun ChatRow(chat: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { /* TODO: Open individual chat */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Initials
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(0xFF0A66C2), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(chat.sender.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Message Content
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(chat.sender, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(chat.time, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chat.lastMsg,
                color = if (chat.unread > 0) Color.Black else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (chat.unread > 0) FontWeight.Bold else FontWeight.Normal
            )
        }

        // Unread Badge
        if (chat.unread > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF2E7D32), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.unread.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
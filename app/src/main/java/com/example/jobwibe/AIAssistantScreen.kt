package com.example.jobwibe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 1. Data Class for Messages
data class AiMessage(val text: String, val isUser: Boolean, val timestamp: Long = System.currentTimeMillis())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Chat State
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<AiMessage>() }
    var isTyping by remember { mutableStateOf(false) }

    // Load initial greeting
    // Load initial greeting
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            val name = CurrentUser.email.substringBefore("@").replaceFirstChar { it.uppercase() }
            // UPDATED: Name changed to JobYatra
            messages.add(AiMessage("Hi $name! I'm your JobYatra AI Assistant. 🤖\n\nI can help you find jobs, improve your resume, or prepare for interviews. What's on your mind?", false))
        }
    }

    // 2. THE BRAIN: Local Logic Engine (Stable & Fast)
    fun generateResponse(query: String) {
        isTyping = true
        scope.launch {
            delay(1500) // Fake "thinking" delay

            val lowerQuery = query.lowercase()
            val response = when {
                lowerQuery.contains("job") || lowerQuery.contains("find") || lowerQuery.contains("search") -> {
                    // Smart Recommendation based on User Skills
                    val userSkills = StorageHelper.loadUsers(context).find { it.email == CurrentUser.email }?.skills ?: emptyList()
                    if (userSkills.isEmpty()) {
                        "I see you haven't listed any skills yet. Go to your Profile and add skills like 'Kotlin' or 'Python' so I can find matches!"
                    } else {
                        "Based on your skills (${userSkills.joinToString(", ")}), I found 3 high-match jobs:\n\n1. Senior ${userSkills.firstOrNull() ?: "Dev"} @ Google (98% Match)\n2. Lead Engineer @ Spotify\n3. Tech Lead @ Netflix\n\nWould you like me to apply to them for you?"
                    }
                }
                lowerQuery.contains("resume") || lowerQuery.contains("cv") -> {
                    val userDocs = StorageHelper.loadUsers(context).find { it.email == CurrentUser.email }?.documents ?: emptyList()
                    if (userDocs.isEmpty()) {
                        "⚠️ CRITICAL: You haven't uploaded a resume yet! Recruiters ignore 90% of profiles without one. Please upload a PDF in your Profile."
                    } else {
                        "✅ You have uploaded '${userDocs.last()}'.\n\nMy Analysis: It looks good, but try adding more metrics (e.g., 'Improved performance by 20%')."
                    }
                }
                lowerQuery.contains("interview") || lowerQuery.contains("prep") -> {
                    "Here is a common question for your role:\n\n'Explain the difference between Val and Var in Kotlin?'\n\n💡 Tip: Focus on mutability and thread safety."
                }
                lowerQuery.contains("apply") || lowerQuery.contains("yes") -> {
                    "On it! 🚀 I have drafted applications for the top 3 jobs. Check your dashboard for status updates."
                }
                else -> "I can help with Jobs, Resumes, or Interview Prep. Try tapping one of the buttons below!"
            }

            messages.add(AiMessage(response, false))
            isTyping = false
        }
    }

    fun sendMessage(text: String) {
        if (text.isNotBlank()) {
            messages.add(AiMessage(text, true))
            messageText = ""
            generateResponse(text)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF6200EA))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("JobYatra AI", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            // Chat List
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    ChatBubble(msg)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isTyping) {
                    item {
                        Text("AI is typing...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }

            // Suggestions
            if (messages.size < 3) {
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    val suggestions = listOf("Find Jobs for me 🔍", "Analyze my Resume 📄", "Interview Prep 🎤")
                    items(suggestions) { label ->
                        SuggestionChip(
                            onClick = { sendMessage(label) },
                            label = { Text(label) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White)
                        )
                    }
                }
            }

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask anything...") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF0F2F5)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF0F2F5),
                        unfocusedContainerColor = Color(0xFFF0F2F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { sendMessage(messageText) })
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { sendMessage(messageText) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(Color(0xFF6200EA), Color(0xFFBB86FC))),
                            shape = CircleShape
                        )
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    }
}

// 3. Helper Composable (Must be in this file)
@Composable
fun ChatBubble(message: AiMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!message.isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFEDE7F6), CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.SmartToy, null, tint = Color(0xFF6200EA))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = if (message.isUser) Color(0xFF0A66C2) else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    color = if (message.isUser) Color.White else Color.Black,
                    fontSize = 15.sp
                )
            }
        }
    }
}
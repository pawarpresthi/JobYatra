package com.example.jobwibe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onJobClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAiClick: () -> Unit,
    onMeetingsClick: () -> Unit // <--- NEW PARAMETER
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredJobs = mockJobs.filter { job ->
        val matchesSearch = job.title.contains(searchQuery, ignoreCase = true) || job.company.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (selectedCategory == "All") true else job.tags.contains(selectedCategory)
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val name = CurrentUser.email.substringBefore("@").replaceFirstChar { it.uppercase() }
                        Text("Hello, $name 👋", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        // UPDATED: Tagline
                        Text("Welcome to JobYatra", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }

                    // Icons Row
                    Row {
                        // 1. AI Assistant
                        IconButton(
                            onClick = onAiClick,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(Color(0xFFE040FB), Color(0xFF6200EA))),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 2. MEETINGS BUTTON (NEW)
                        IconButton(
                            onClick = onMeetingsClick,
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.VideoCall, contentDescription = "Meetings", tint = Color(0xFF2E7D32))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 3. Logout
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.size(48.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 4. Chat
                        IconButton(
                            onClick = onChatClick,
                            modifier = Modifier.size(48.dp).background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Chat", tint = Color(0xFF0A66C2))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 5. Profile
                        IconButton(
                            onClick = onProfileClick,
                            modifier = Modifier.size(48.dp).background(Color(0xFFF0F2F5), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search jobs...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF0F2F5),
                        unfocusedContainerColor = Color(0xFFF0F2F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)).padding(paddingValues).padding(16.dp)
        ) {
            // ... (Keep existing LazyColumn content: Skills & Job List) ...
            item {
                Text("Popular Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val skills = listOf("All", "Kotlin", "Java", "Python", "Design", "AWS")
                    items(skills) { skill ->
                        FilterChip(
                            selected = selectedCategory == skill,
                            onClick = { selectedCategory = skill },
                            label = { Text(skill) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0A66C2),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Jobs for you", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(${filteredJobs.size} found)", color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(filteredJobs) { job ->
                ProfessionalJobItem(job = job, onClick = { onJobClick(job.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ProfessionalJobItem(job: Job, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = job.logoUrl, contentDescription = null,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(job.company, color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    job.tags.take(2).forEach { tag ->
                        Text(tag, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.background(Color(0xFFF0F2F5), RoundedCornerShape(4.dp)).padding(4.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
            IconButton(onClick = { }) { Icon(Icons.Outlined.BookmarkBorder, "Save", tint = Color.Gray) }
        }
    }
}
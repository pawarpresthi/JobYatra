package com.example.jobwibe
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun JobCard(job: Job, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image/Color
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFF222222), Color.Black)))
            ) {
                // Logo Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = job.logoUrl,
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // 2. Info Overlay (Bottom)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                // The AI Match Score Badge
                AssistChip(
                    onClick = { },
                    label = { Text("AI Match: ${job.matchScore}%", fontWeight = FontWeight.Bold) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF4CAF50))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = job.title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text(text = job.company, style = MaterialTheme.typography.titleMedium, color = Color.LightGray)

                Spacer(modifier = Modifier.height(16.dp))

                // Tags
                Row {
                    job.tags.forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(tag, color = Color.White) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
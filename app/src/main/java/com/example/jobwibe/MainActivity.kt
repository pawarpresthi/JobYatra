package com.example.jobwibe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val savedJobs = StorageHelper.loadJobs(context)
                    if (savedJobs.isNotEmpty()) { mockJobs.clear(); mockJobs.addAll(savedJobs) }
                    val savedCandidates = StorageHelper.loadCandidates(context)
                    if (savedCandidates.isNotEmpty()) { mockCandidates.clear(); mockCandidates.addAll(savedCandidates) }
                    val savedChats = StorageHelper.loadChats(context)
                    if (savedChats.isNotEmpty()) { mockChats.clear(); mockChats.addAll(savedChats) }
                }
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = { isRecruiter ->
                val dest = if (isRecruiter) "recruiter_dashboard" else "dashboard"
                navController.navigate(dest) { popUpTo("login") { inclusive = true } }
            })
        }

        composable("dashboard") {
            DashboardScreen(
                onJobClick = { navController.navigate("details/$it") },
                onProfileClick = { navController.navigate("profile") },
                onChatClick = { navController.navigate("chat") },
                onLogoutClick = { navController.navigate("login") { popUpTo(0) } },
                onAiClick = { navController.navigate("ai_assistant") },
                onMeetingsClick = { navController.navigate("meetings") } // <--- FIX
            )
        }

        composable("ai_assistant") { AIAssistantScreen(onBackClick = { navController.popBackStack() }) }

        composable("recruiter_dashboard") {
            RecruiterDashboard(
                onPostJobClick = { navController.navigate("post_job") },
                onLogoutClick = { navController.navigate("login") { popUpTo(0) } },
                onMeetingsClick = { navController.navigate("meetings") }, // <--- FIX
                onCandidateClick = { candidateId ->
                    navController.navigate("candidate_detail/$candidateId")
                }
            )
        }

        composable("candidate_detail/{candidateId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("candidateId") ?: "1"
            CandidateDetailScreen(
                candidateId = id,
                onBackClick = { navController.popBackStack() },
                onHire = {
                    val candidate = mockCandidates.find { it.id == id }
                    candidate?.let {
                        it.status = "Hired"
                        val newMsg = ChatMessage("msg_${System.currentTimeMillis()}", "Hiring Team", "Congratulations! You've been hired! 🎉", "Just now", 1)
                        mockChats.add(0, newMsg)
                        StorageHelper.saveCandidates(context, mockCandidates)
                        StorageHelper.saveChats(context, mockChats)
                    }
                },
                onReject = {
                    val candidate = mockCandidates.find { it.id == id }
                    candidate?.let {
                        it.status = "Rejected"
                        mockCandidates.remove(it)
                        StorageHelper.saveCandidates(context, mockCandidates)
                    }
                },
                onSchedule = {
                    Toast.makeText(context, "Go to Dashboard to Schedule", Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable("post_job") {
            PostJobScreen(
                onJobPosted = { StorageHelper.saveJobs(context, mockJobs); navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("details/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: "1"
            JobDetailScreen(
                jobId = jobId,
                onBackClick = { navController.popBackStack() },
                onApplySuccess = { StorageHelper.saveCandidates(context, mockCandidates); StorageHelper.saveJobs(context, mockJobs) }
            )
        }

        composable("profile") { ProfileScreen({ navController.popBackStack() }) }
        composable("chat") { ChatScreen({ navController.popBackStack() }) }
        composable("meetings") { MeetingsScreen(onBackClick = { navController.popBackStack() }) }
    }
}
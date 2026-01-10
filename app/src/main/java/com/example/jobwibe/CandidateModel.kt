package com.example.jobwibe

import androidx.compose.runtime.mutableStateListOf
import java.io.Serializable

data class Candidate(
    val id: String,
    val name: String,
    val email: String, // The new field we added
    val role: String,
    val experience: String,
    val matchScore: Int,
    val skills: List<String>,
    var status: String = "New"
) : Serializable

// CHANGED: We added dummy data back so the screen isn't empty!
val mockCandidates = mutableStateListOf(
    Candidate(
        id = "1",
        name = "Rahul Sharma",
        email = "rahul@test.com",
        role = "Android Dev",
        experience = "3 Yrs",
        matchScore = 95,
        skills = listOf("Kotlin", "Compose", "Firebase"),
        status = "New"
    ),
    Candidate(
        id = "2",
        name = "Priya Singh",
        email = "priya@test.com",
        role = "UX Designer",
        experience = "5 Yrs",
        matchScore = 88,
        skills = listOf("Figma", "Sketch", "Prototyping"),
        status = "New"
    ),
    Candidate(
        id = "3",
        name = "Amit Verma",
        email = "amit@test.com",
        role = "Backend Eng",
        experience = "4 Yrs",
        matchScore = 72,
        skills = listOf("Node.js", "AWS", "MongoDB"),
        status = "New"
    )
)
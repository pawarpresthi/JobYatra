package com.example.jobwibe

import androidx.compose.runtime.mutableStateListOf
import java.io.Serializable

data class Job(
    val id: String,
    val title: String,
    val company: String,
    val salary: String,
    val logoUrl: String,
    val matchScore: Int,
    val tags: List<String>,
    // CHANGED: Instead of 'isApplied', we store a list of people who applied
    val applicants: MutableList<String> = mutableListOf()
) : Serializable

val mockJobs = mutableStateListOf(
    Job("1", "Android Engineer", "Google", "$140k", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Android_robot.svg/1024px-Android_robot.svg.png", 94, listOf("Kotlin", "Compose")),
    Job("2", "Product Designer", "Spotify", "$120k", "https://upload.wikimedia.org/wikipedia/commons/2/26/Spotify_logo_with_text.svg", 88, listOf("Figma", "UX")),
    Job("3", "Backend Dev", "Netflix", "$160k", "https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg", 75, listOf("Java", "AWS"))
)

fun addJob(title: String, company: String, salary: String) {
    mockJobs.add(0, Job(
        id = (mockJobs.size + 1).toString(),
        title = title,
        company = company,
        salary = salary,
        logoUrl = "https://cdn-icons-png.flaticon.com/512/3003/3003202.png",
        matchScore = (70..99).random(),
        tags = listOf("New", "Hiring"),
        applicants = mutableListOf() // Start with empty list
    ))
}
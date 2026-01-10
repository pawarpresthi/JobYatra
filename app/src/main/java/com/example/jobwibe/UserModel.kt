package com.example.jobwibe

import java.io.Serializable

data class User(
    val email: String,
    val password: String,
    val isRecruiter: Boolean,
    // NEW: Store specific data for this user
    val skills: MutableList<String> = mutableListOf("Communication", "Teamwork"),
    val documents: MutableList<String> = mutableListOf("Resume.pdf")
) : Serializable
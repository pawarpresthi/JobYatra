package com.example.jobwibe

import java.io.Serializable

data class Meeting(
    val id: String,
    val candidateName: String,
    val candidateEmail: String,
    val recruiterEmail: String,
    val date: String,     // e.g. "Oct 24"
    val time: String,     // e.g. "10:00 AM"
    val link: String      // e.g. "zoom.us/..."
) : Serializable
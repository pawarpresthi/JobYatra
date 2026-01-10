package com.example.jobwibe

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object StorageHelper {
    private const val JOBS_FILE = "jobs_data.dat"
    private const val CANDIDATES_FILE = "candidates_data.dat"
    private const val USERS_FILE = "users_data.dat" // <--- NEW FILE

    // ... (Keep saveJobs/loadJobs and saveCandidates/loadCandidates EXACTLY AS BEFORE) ...
    // ... Copy them here if you deleted them, otherwise just ADD the section below ...
    // ... inside StorageHelper object ...

    private const val CHATS_FILE = "chats_data.dat" // <--- Add this constant
    // ... inside StorageHelper object ...
    private const val MEETINGS_FILE = "meetings_data.dat"

    fun saveMeetings(context: Context, meetings: List<Meeting>) {
        context.openFileOutput(MEETINGS_FILE, Context.MODE_PRIVATE).use { fos ->
            ObjectOutputStream(fos).use { it.writeObject(ArrayList(meetings)) }
        }
    }

    fun loadMeetings(context: Context): MutableList<Meeting> {
        val list = ArrayList<Meeting>()
        try {
            context.openFileInput(MEETINGS_FILE).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    val savedList = ois.readObject() as ArrayList<Meeting>
                    list.addAll(savedList)
                }
            }
        } catch (e: Exception) { }
        return list
    }
    // --- CHATS STORAGE ---
    fun saveChats(context: Context, chats: List<ChatMessage>) {
        context.openFileOutput(CHATS_FILE, Context.MODE_PRIVATE).use { fos ->
            ObjectOutputStream(fos).use { it.writeObject(ArrayList(chats)) }
        }
    }

    fun loadChats(context: Context): SnapshotStateList<ChatMessage> {
        val list = mutableStateListOf<ChatMessage>()
        try {
            context.openFileInput(CHATS_FILE).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    val savedList = ois.readObject() as ArrayList<ChatMessage>
                    list.addAll(savedList)
                }
            }
        } catch (e: Exception) {
            // No saved data
        }
        return list
    }
    // --- JOBS STORAGE (Keep existing) ---
    fun saveJobs(context: Context, jobs: List<Job>) {
        context.openFileOutput(JOBS_FILE, Context.MODE_PRIVATE).use { fos ->
            ObjectOutputStream(fos).use { it.writeObject(ArrayList(jobs)) }
        }
    }

    fun loadJobs(context: Context): SnapshotStateList<Job> {
        val list = mutableStateListOf<Job>()
        try {
            context.openFileInput(JOBS_FILE).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    val savedList = ois.readObject() as ArrayList<Job>
                    list.addAll(savedList)
                }
            }
        } catch (e: Exception) {}
        return list
    }

    // --- CANDIDATES STORAGE (Keep existing) ---
    fun saveCandidates(context: Context, candidates: List<Candidate>) {
        context.openFileOutput(CANDIDATES_FILE, Context.MODE_PRIVATE).use { fos ->
            ObjectOutputStream(fos).use { it.writeObject(ArrayList(candidates)) }
        }
    }

    fun loadCandidates(context: Context): SnapshotStateList<Candidate> {
        val list = mutableStateListOf<Candidate>()
        try {
            context.openFileInput(CANDIDATES_FILE).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    val savedList = ois.readObject() as ArrayList<Candidate>
                    list.addAll(savedList)
                }
            }
        } catch (e: Exception) {}
        return list
    }

    // --- NEW: USER AUTH STORAGE ---
    fun saveUsers(context: Context, users: List<User>) {
        context.openFileOutput(USERS_FILE, Context.MODE_PRIVATE).use { fos ->
            ObjectOutputStream(fos).use { it.writeObject(ArrayList(users)) }
        }
    }

    fun loadUsers(context: Context): MutableList<User> {
        val list = ArrayList<User>()
        try {
            context.openFileInput(USERS_FILE).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    val savedList = ois.readObject() as ArrayList<User>
                    list.addAll(savedList)
                }
            }
        } catch (e: Exception) {
            // No users found
        }
        return list
    }
}
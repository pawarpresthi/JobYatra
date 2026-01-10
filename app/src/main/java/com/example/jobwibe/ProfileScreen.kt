package com.example.jobwibe

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(0) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var allUsers by remember { mutableStateOf<MutableList<User>>(mutableListOf()) }

    LaunchedEffect(refreshTrigger) {
        allUsers = StorageHelper.loadUsers(context)
        currentUser = allUsers.find { it.email == CurrentUser.email }
    }

    var showSkillDialog by remember { mutableStateOf(false) }
    var newSkillText by remember { mutableStateOf("") }

    val pdfLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(context, it) ?: "Document.pdf"
            val savedFile = saveFileToInternalStorage(context, it, fileName)
            if (savedFile != null) {
                currentUser?.let { user ->
                    val newDocs = user.documents.toMutableList().apply { add(fileName) }
                    val index = allUsers.indexOfFirst { u -> u.email == user.email }
                    if (index != -1) {
                        val updatedUser = user.copy(documents = newDocs)
                        allUsers[index] = updatedUser
                        StorageHelper.saveUsers(context, allUsers)
                        refreshTrigger++
                        Toast.makeText(context, "Uploaded: $fileName", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun updateUserData(updatedUser: User) {
        val index = allUsers.indexOfFirst { it.email == updatedUser.email }
        if (index != -1) {
            allUsers[index] = updatedUser
            StorageHelper.saveUsers(context, allUsers)
            refreshTrigger++
        }
    }

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        val user = currentUser!!
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { pdfLauncher.launch("*/*") },
                    containerColor = Color(0xFF0A66C2),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.UploadFile, null) },
                    text = { Text("Upload Doc") }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(Color(0xFF0A66C2))) {
                        IconButton(onClick = onBackClick, modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.BottomCenter).size(120.dp).background(Color.White, CircleShape).padding(4.dp)) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(80.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(user.email.substringBefore("@").replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Job Seeker", color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(24.dp))

                PaddingBox(title = "Skills & Expertise") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(user.skills) { skill ->
                            InputChip(
                                selected = false, onClick = {}, label = { Text(skill) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, "Delete", modifier = Modifier.size(16.dp).clickable {
                                        val newSkills = user.skills.toMutableList().apply { remove(skill) }
                                        updateUserData(user.copy(skills = newSkills))
                                    })
                                }
                            )
                        }
                        item { IconButton(onClick = { showSkillDialog = true }) { Icon(Icons.Default.Add, "Add", tint = Color(0xFF0A66C2)) } }
                    }
                }

                PaddingBox(title = "My Documents") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (user.documents.isEmpty()) Text("No documents uploaded yet.", color = Color.Gray, fontSize = 14.sp)
                        else user.documents.forEach { docName ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, null, tint = Color.Red)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(docName, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(onClick = {
                                        val newDocs = user.documents.toMutableList().apply { remove(docName) }
                                        updateUserData(user.copy(documents = newDocs))
                                    }) { Icon(Icons.Default.Delete, "Delete", tint = Color.Gray) }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showSkillDialog) {
        AlertDialog(
            onDismissRequest = { showSkillDialog = false },
            title = { Text("Add New Skill") },
            text = { OutlinedTextField(value = newSkillText, onValueChange = { newSkillText = it }, label = { Text("Skill") }) },
            confirmButton = {
                Button(onClick = {
                    if (newSkillText.isNotEmpty()) {
                        val newSkills = currentUser!!.skills.toMutableList().apply { add(newSkillText) }
                        updateUserData(currentUser!!.copy(skills = newSkills))
                        newSkillText = ""
                        showSkillDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showSkillDialog = false }) { Text("Cancel") } }
        )
    }
}

// --- HELPER FUNCTIONS ---
@Composable
fun PaddingBox(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        content()
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) result = cursor.getString(index)
            }
        } finally { cursor?.close() }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) result = result?.substring(cut + 1)
    }
    return result
}

fun saveFileToInternalStorage(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) { null }
}
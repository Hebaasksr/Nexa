package com.example.nexa.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

// 5. Provisional layout: metadata only. Host opens storageUrl and handles uploads.
@Composable
fun FilesScreen(
    projectName: String, files: List<ProjectFileUi>, onBack: () -> Unit,
    onOpenFile: (String) -> Unit, onUpload: () -> Unit,
    loading: Boolean = false, uploading: Boolean = false, error: String? = null,
    onRetry: () -> Unit = {}
) {
    var query by rememberSaveable { mutableStateOf("") }
    ProjectPage("Files", projectName, onBack) {
        OutlinedTextField(query, { query = it }, label = { Text("Search files") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (loading) CircularProgressIndicator()
        Message(error)
        if (error != null) TextButton(onClick = onRetry) { Text("Try again") }
        val visible = files.filter { it.fileName.contains(query, ignoreCase = true) }
        if (!loading && error == null && visible.isEmpty()) Text(if (files.isEmpty()) "No files uploaded yet." else "No matching files.")
        visible.forEach { file ->
            Box(Modifier.clickable { onOpenFile(file.id) }) {
                Panel { Text(file.fileName, fontWeight = FontWeight.Bold); Text(file.subtitle, style = MaterialTheme.typography.bodySmall) }
            }
        }
        Action(if (uploading) "Uploading…" else "+ Upload file", !uploading, onUpload)
    }
}

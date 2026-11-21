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

// 2. One form used for BOTH adding and editing a project.
// Persist draft in your ViewModel if it must survive navigation away from this screen.
@Composable
fun AddEditProjectScreen(
    project: ProjectUi? = null, teams: List<TeamOptionUi>, initialTeamId: String = "",
    saving: Boolean = false, error: String? = null,
    onSave: (ProjectDraft) -> Unit, onCancel: () -> Unit
) {
    val key = project?.id ?: "new"
    var name by rememberSaveable(key) { mutableStateOf(project?.name.orEmpty()) }
    var description by rememberSaveable(key) { mutableStateOf(project?.description.orEmpty()) }
    var start by rememberSaveable(key) { mutableStateOf(project?.startDate.orEmpty()) }
    var end by rememberSaveable(key) { mutableStateOf(project?.endDate.orEmpty()) }
    var teamId by rememberSaveable(key) { mutableStateOf(initialTeamId) }
    var attempted by rememberSaveable(key) { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val team = teams.find { it.id == teamId }
    val startParsed = runCatching { LocalDate.parse(start) }.getOrNull()
    val endParsed = runCatching { LocalDate.parse(end) }.getOrNull()
    val dateError = when {
        startParsed == null || endParsed == null -> "Enter valid dates in YYYY-MM-DD format."
        endParsed.isBefore(startParsed) -> "End date must be on or after start date."
        else -> null
    }
    ProjectPage(if (project == null) "New project" else "Edit project", "Required fields are marked with *", onCancel) {
        OutlinedTextField(name, { name = it }, label = { Text("Project name *") },
            isError = attempted && name.isBlank(), enabled = !saving, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (attempted && name.isBlank()) Message("Enter a project name.")
        OutlinedTextField(description, { description = it }, label = { Text("Description") },
            enabled = !saving, minLines = 3, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(start, { start = it }, label = { Text("Start date *") },
            placeholder = { Text("YYYY-MM-DD") }, enabled = !saving, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(end, { end = it }, label = { Text("End date *") },
            placeholder = { Text("YYYY-MM-DD") }, enabled = !saving, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (attempted) Message(dateError)
        Box {
            OutlinedButton(onClick = { expanded = true }, enabled = !saving && teams.isNotEmpty(), modifier = Modifier.fillMaxWidth()) {
                Text(team?.name ?: "Select team *")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                teams.forEach { option -> DropdownMenuItem(text = { Text(option.name) }, onClick = {
                    teamId = option.id; expanded = false
                }) }
            }
        }
        if (teams.isEmpty()) Text("No teams available. Team data must be provided by the workspace.")
        if (attempted && team == null) Message("Select a team.")
        team?.let {
            Panel {
                Text("Included members", fontWeight = FontWeight.Bold)
                it.members.forEach { member -> Text("${member.name} • ${member.role}") }
            }
        }
        Message(error)
        Action(if (saving) "Saving…" else if (project == null) "Create project" else "Save changes", !saving) {
            attempted = true
            if (name.isNotBlank() && dateError == null && team != null) {
                onSave(ProjectDraft(name.trim(), description.trim(), start, end, team.id, team.members.map { it.id }))
            }
        }
        OutlinedButton(onClick = onCancel, enabled = !saving, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
    }
}


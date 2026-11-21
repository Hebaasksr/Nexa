package com.nexa.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ProjectFields(
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val team: String,
)

@Composable
fun AddEditProjectScreen(
    project: ProjectChannel?,
    onSave: (ProjectFields) -> Boolean,
    onBack: () -> Unit,
    onTabSelected: (Int) -> Unit = {},
    onNew: () -> Unit = {},
) {
    var name by rememberSaveable(project?.id) { mutableStateOf(project?.name.orEmpty()) }
    var description by rememberSaveable(project?.id) { mutableStateOf(project?.description.orEmpty()) }
    var start by rememberSaveable(project?.id) { mutableStateOf(project?.startDate ?: "Oct 01, 2026") }
    var end by rememberSaveable(project?.id) { mutableStateOf(project?.endDate ?: "Nov 12, 2026") }
    var team by rememberSaveable(project?.id) { mutableStateOf(project?.teamName ?: "Product team · 5 members") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var failed by rememberSaveable { mutableStateOf(false) }
    var teamExpanded by remember { mutableStateOf(false) }
    val editing = project != null

    Column(Modifier.fillMaxSize().background(Sheet)) {
        NexaHeader(
            if (editing) "Edit project" else "New project",
            "Product workspace · ${if (editing) "Update project details" else "Project draft"}",
            onBack = onBack,
            action = { TextButton(onClick = onBack) { Text("Cancel", color = Color.White) } },
        )
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Required fields are marked with *", color = Muted, style = MaterialTheme.typography.bodySmall)
            ProjectTextField("Project name *", name, "Project name", { name = it }, submitted && name.isBlank())
            ProjectTextField("Description", description, "Add a short project description", { description = it }, lines = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProjectTextField("Start date *", start, "Oct 01, 2026", { start = it }, submitted && start.isBlank(), Modifier.weight(1f))
                ProjectTextField("End date *", end, "Nov 12, 2026", { end = it }, submitted && end.isBlank(), Modifier.weight(1f))
            }
            Text("Team *", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
            Box {
                OutlinedTextField(
                    value = team, onValueChange = {}, readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { IconButton(onClick = { teamExpanded = true }) { Icon(Icons.Default.KeyboardArrowDown, null) } },
                    shape = CardShape, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Panel, focusedContainerColor = Panel),
                )
                Spacer(Modifier.matchParentSize().background(Color.Transparent).then(Modifier))
                DropdownMenu(expanded = teamExpanded, onDismissRequest = { teamExpanded = false }) {
                    listOf("Product team · 5 members", "Personal", "Research group").forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { team = option; teamExpanded = false })
                    }
                }
            }
            NexaCard {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Included members", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Text("HE     HA     FA     HQ     KA", color = Purple, fontWeight = FontWeight.Bold)
                    Text("Owner: Heba Askar · Only this team can access", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (failed) Text("Could not save the project. Please try again.", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    submitted = true
                    if (name.isNotBlank() && start.isNotBlank() && end.isNotBlank()) {
                        failed = !onSave(ProjectFields(name, description, start, end, team))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(16.dp),
            ) { Text(if (editing) "Save changes" else "Create project", fontWeight = FontWeight.Bold) }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Cancel", color = Purple, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProjectTextField(
    label: String,
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    error: Boolean = false,
    modifier: Modifier = Modifier,
    lines: Int = 1,
) {
    Column(modifier) {
        Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value, onChange, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) }, minLines = lines, singleLine = lines == 1,
            isError = error, shape = CardShape,
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Panel, focusedContainerColor = Panel),
        )
    }
}

@Preview(name = "New Project", showBackground = true, widthDp = 393, heightDp = 850)
@Composable
private fun NewProjectPreview() {
    NexaTheme { AddEditProjectScreen(null, { true }, {}) }
}

@Preview(name = "Edit Project", showBackground = true, widthDp = 393, heightDp = 850)
@Composable
private fun EditProjectPreview() {
    NexaTheme {
        AddEditProjectScreen(
            ProjectChannel("redesign", "Mobile App Redesign", "Redesign the mobile application."),
            { true }, {},
        )
    }
}




package com.nexa.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProjectsScreen(
    projects: List<ProjectChannel>,
    onOpenProject: (String) -> Unit,
    onAddProject: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onNew: () -> Unit,
    onBack: (() -> Unit)? = null,
    onJoinProject: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableIntStateOf(0) }

    val shown = projects.filter {
        it.name.contains(query, ignoreCase = true) &&
                when (filter) {
                    1 -> it.status == "Active" || it.status == "In Progress"
                    2 -> it.archived
                    else -> true
                }
    }

    NexaWorkspacePage(
        title = "Projects",
        subtitle = "Product workspace · ${projects.size} active projects",
        onBack = onBack,
        selectedTab = 4,
        onTabSelected = onTabSelected,
        onNew = onNew,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                placeholder = { Text("Search projects…") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Muted)
                },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Panel,
                    focusedContainerColor = Panel,
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("All", "Active", "Archived").forEachIndexed { index, label ->
                    FilterChip(
                        selected = filter == index,
                        onClick = { filter = index },
                        label = { Text(label) },
                    )
                }
            }

            OutlinedButton(
                onClick = onJoinProject,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).heightIn(min = 50.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Join project", color = Purple, fontWeight = FontWeight.Bold)
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(shown.size, key = { shown[it].id }) { index ->
                    val project = shown[index]
                    ProjectListCard(project) { onOpenProject(project.id) }
                }

                if (shown.isEmpty()) {
                    item {
                        Text(
                            "No projects match this search.",
                            color = Muted,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }

                item {
                    Button(
                        onClick = onAddProject,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("New project", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectListCard(project: ProjectChannel, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CardShape,
        color = Panel,
        shadowElevation = 2.dp,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Purple)
                Text(project.name, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("${project.progress}%", color = Purple, fontWeight = FontWeight.Bold)
            }

            Text(
                "${project.teamName} · ${project.status}",
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
            )

            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Purple,
                trackColor = Outline,
            )

            Text(
                "${project.startDate} – ${project.endDate}",
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
            )

            if (project.description.isNotBlank()) {
                Text(
                    project.description,
                    color = Aqua,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 850)
@Composable
private fun ProjectsScreenPreview() {
    NexaTheme {
        ProjectsScreen(
            projects = listOf(
                ProjectChannel(
                    "redesign",
                    "Mobile App Redesign",
                    "UI/UX Design · Team Project",
                    progress = 60,
                ),
                ProjectChannel(
                    "website",
                    "Website Development",
                    "Frontend · Personal",
                    status = "In Progress",
                    progress = 35,
                ),
            ),
            onOpenProject = {},
            onAddProject = {},
            onTabSelected = {},
            onNew = {},
            onBack = {},
            onJoinProject = {},
        )
    }
}
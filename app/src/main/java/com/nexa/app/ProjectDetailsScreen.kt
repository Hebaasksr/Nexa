package com.nexa.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProjectDetailsScreen(
    project: ProjectChannel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenTeam: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onNew: () -> Unit,
) {
    var section by rememberSaveable(project.id) { mutableIntStateOf(0) }
    NexaWorkspacePage(
        title = project.name,
        subtitle = "UI/UX Design · Team Project",
        onBack = onBack,
        selectedTab = 0,
        onTabSelected = onTabSelected,
        onNew = onNew,
        action = {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit project", tint = Color.White)
            }
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Overview", "Tasks", "Files", "Team").forEachIndexed { index, label ->
                    TextButton(onClick = {
                        section = index
                        when (index) {
                            1 -> onOpenTasks()
                            2 -> onOpenFiles()
                            3 -> onOpenTeam()
                        }
                    }) {
                        Text(label, color = if (section == index) Purple else Muted, fontWeight = if (section == index) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    NexaCard {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Project Progress", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                Text("${project.progress}%", color = Purple, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(progress = { project.progress / 100f }, modifier = Modifier.fillMaxWidth(), color = Purple, trackColor = Outline)
                            Text("18 of 30 tasks completed · 12 left", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NexaCard(Modifier.weight(1f)) {
                            Column(Modifier.padding(14.dp)) {
                                Text("START DATE", color = Muted, style = MaterialTheme.typography.labelSmall)
                                Text(project.startDate, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Text("END DATE", color = Muted, style = MaterialTheme.typography.labelSmall)
                                Text(project.endDate, color = Color(0xFFB84B00), fontWeight = FontWeight.Bold)
                            }
                        }
                        NexaCard(Modifier.weight(1f)) {
                            Column(Modifier.padding(14.dp)) {
                                Text("TEAM", color = Muted, style = MaterialTheme.typography.labelSmall)
                                Text("5 Members", fontWeight = FontWeight.Bold)
                                Text(project.teamName, color = Muted, style = MaterialTheme.typography.bodySmall)
                                TextButton(onClick = onOpenTeam) { Text("View team") }
                            }
                        }
                    }
                }
                item {
                    NexaCard {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("NEXT MILESTONE", color = Muted, style = MaterialTheme.typography.labelSmall)
                                Text("Prototype Handoff", fontWeight = FontWeight.Bold)
                                Text("Due Sep 28, 2026", color = Muted, style = MaterialTheme.typography.bodySmall)
                            }
                            AssistChip(onClick = {}, label = { Text("On track") })
                        }
                    }
                }
                item {
                    NexaCard {
                        Column(Modifier.padding(16.dp)) {
                            Text("Description", fontWeight = FontWeight.Bold)
                            Text(project.description.ifBlank { "No description added yet." }, Modifier.padding(top = 5.dp), color = Muted)
                        }
                    }
                }
                item {
                    Button(onClick = onOpenTasks, modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)) {
                        Text("View tasks →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 850)
@Composable
private fun ProjectDetailsPreview() {
    NexaTheme {
        ProjectDetailsScreen(
            ProjectChannel("redesign", "Mobile App Redesign", "Redesign the mobile application with a focus on user experience and accessibility."),
            {}, {}, {}, {}, {}, {}, {},
        )
    }
}


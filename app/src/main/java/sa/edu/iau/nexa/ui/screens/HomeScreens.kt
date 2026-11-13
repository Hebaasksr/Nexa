package sa.edu.iau.nexa.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import sa.edu.iau.nexa.model.*
import sa.edu.iau.nexa.ui.components.*
import sa.edu.iau.nexa.ui.theme.*
import sa.edu.iau.nexa.viewmodel.NexaUiState
import sa.edu.iau.nexa.viewmodel.NexaViewModel

@Composable
fun DashboardScreen(state: NexaUiState, go: (String) -> Unit) {
    NexaShell("Dashboard", "One place. Every project.", "Home", go,
        trailingIcon = Icons.Outlined.Notifications, onTrailing = { go("notifications") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CircleAvatar(state.userName.split(' ').mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase())
                Column {
                    Text("Good morning 👋", color = Secondary, fontSize = 12.sp)
                    Text(state.userName, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Ink)
                    Text("Product workspace", color = Secondary, fontSize = 11.sp)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { go("messages") }) { Icon(Icons.Outlined.ChatBubbleOutline, "Messages", tint = Purple) }
            }
            Box(Modifier.clickable { go("search") }) {
                SearchBarNexa("", { go("search") })
                Box(Modifier.matchParentSize().clickable { go("search") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val vals = listOf(
                    Triple("${state.projects.count { !it.archived }}", "Active Projects", Purple),
                    Triple("${state.tasks.size}", "My Tasks", Color(0xFF087782)),
                    Triple("${state.tasks.count { it.status != "Done" && it.dueDate <= java.time.LocalDate.now().plusDays(7).toString() }}", "Due Soon", Amber)
                )
                vals.forEach { (count, label, color) ->
                    NexaCard(Modifier.weight(1f).height(91.dp)) {
                        Text(count, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
                        Text(label, fontSize = 10.sp, color = Secondary, maxLines = 2)
                    }
                }
            }
            SectionTitle("My Projects", "View all ›") { go("projects") }
            state.projects.filterNot { it.archived }.take(4).forEach { project -> ProjectCard(project) { go("project/${project.id}") } }
            NexaCard(onClick = { go("tasks") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Bolt, null, tint = Purple)
                    Spacer(Modifier.width(6.dp))
                    Text("View all tasks and upcoming deadlines", color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.ChevronRight, null, tint = Purple)
                }
            }
        }
    }
}

@Composable
fun ProjectCard(p: NexaProject, onClick: () -> Unit) {
    NexaCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(PurpleLight), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.PhoneAndroid, null, tint = Purple, modifier = Modifier.size(19.dp)) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(p.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(p.category, fontSize = 11.sp, color = Secondary, maxLines = 1)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = Secondary)
        }
        Spacer(Modifier.height(9.dp))
        Text("${p.owner} · ${if (p.archived) "Archived" else "Active"}", fontSize = 11.sp, color = Secondary)
        Spacer(Modifier.height(9.dp))
        ProgressLine(p.progress)
        Spacer(Modifier.height(8.dp))
        Text("Due ${prettyDate(p.dueDate)}", color = Secondary, fontSize = 11.sp)
    }
}

@Composable
fun ProjectsScreen(state: NexaUiState, go: (String) -> Unit, back: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    val filtered = state.projects.filter { p -> p.name.contains(search, true) && when (filter) { "Active" -> !p.archived; "Archived" -> p.archived; else -> true } }
    NexaShell("Projects", "Product workspace · ${state.projects.count { !it.archived }} active projects", "More", go, back,
        trailingIcon = Icons.Outlined.Notifications, onTrailing = { go("notifications") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SearchBarNexa(search, { search = it }, "Search projects...")
            FilterRow(listOf("All", "Active", "Archived"), filter, { filter = it })
            if (filtered.isEmpty()) EmptyState("Start with your first project", "Create a project, invite your team and start tracking progress.", "+ Create your first project") { go("newProject") }
            else filtered.forEach { ProjectCard(it) { go("project/${it.id}") } }
            Spacer(Modifier.height(2.dp))
            PrimaryButton("+ New project", { go("newProject") })
        }
    }
}

@Composable
fun ProjectDetailScreen(state: NexaUiState, id: String, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    val project = state.projects.find { it.id == id }
    var tab by remember { mutableStateOf("Overview") }
    if (project == null) { MissingRecord("Project not found", back); return }
    NexaShell(project.name, project.category, "Home", go, back, trailingIcon = Icons.Outlined.MoreVert, onTrailing = { vm.archiveProject(id) }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            FilterRow(listOf("Overview", "Tasks", "Files", "Team"), tab, { tab = it })
            when (tab) {
                "Overview" -> {
                    NexaCard {
                        SectionTitle("Project Progress")
                        Spacer(Modifier.height(8.dp)); ProgressLine(project.progress)
                        Text("${state.tasks.count { it.projectId == id && it.status == "Done" }} tasks completed", color = Secondary, fontSize = 12.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NexaCard(Modifier.weight(1f)) { Text("DUE DATE", fontSize = 11.sp, color = Secondary); Text(prettyDate(project.dueDate), fontWeight = FontWeight.Bold, color = Ink) }
                        NexaCard(Modifier.weight(1f)) { Text("TEAM", fontSize = 11.sp, color = Secondary); Text("${state.team.size} Members", fontWeight = FontWeight.Bold, color = Ink) }
                    }
                    NexaCard { Text("Next Milestone", fontWeight = FontWeight.Bold, color = Ink); Text("Prototype Handoff", fontSize = 13.sp); Text("Due ${prettyDate(project.dueDate)}", color = Secondary, fontSize = 11.sp) }
                    NexaCard { Text("Description", fontWeight = FontWeight.Bold, color = Ink); Spacer(Modifier.height(5.dp)); Text(project.description.ifBlank { "No description yet." }, color = Secondary, fontSize = 13.sp, lineHeight = 19.sp) }
                    PrimaryButton("View Tasks →", { tab = "Tasks" })
                }
                "Tasks" -> {
                    SectionTitle("Project tasks", "+ New") { go("newTask?projectId=$id") }
                    state.tasks.filter { it.projectId == id }.forEach { TaskCard(it, project.name, { go("task/${it.id}") }, { vm.setTaskStatus(it.id, if (it.status == "Done") "To Do" else "Done") }) }
                    PrimaryButton("+ New task", { go("newTask?projectId=$id") })
                }
                "Files" -> { Text("Project resources", fontWeight = FontWeight.Bold); state.files.take(4).forEach { FileRow(it) { go("preview/${android.net.Uri.encode(it.name)}") } }; SecondaryButton("Open all files", { go("files") }) }
                "Team" -> { state.team.forEach { MemberRow(it) { go("messages") } }; PrimaryButton("Invite member", { go("invite") }) }
            }
        }
    }
}

@Composable
fun TasksScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit) {
    var filter by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    val tasks = state.tasks.filter { (filter == "All" || it.status == filter) && it.title.contains(query, true) }
    NexaShell("My Tasks", "${state.tasks.size} tasks · ${state.tasks.count { it.status == "In Progress" }} in progress", "Tasks", go,
        trailingIcon = Icons.Outlined.Notifications, onTrailing = { go("notifications") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            SearchBarNexa(query, { query = it }, "Find a task...")
            FilterRow(listOf("All", "To Do", "In Progress", "Done"), filter, { filter = it })
            SectionTitle("All Tasks", "+ New") { go("newTask") }
            if (tasks.isEmpty()) EmptyState("No tasks found", "Try a different filter or create a new task.", "New task") { go("newTask") }
            tasks.forEach { t -> TaskCard(t, state.projects.find { it.id == t.projectId }?.name ?: "Project", { go("task/${t.id}") }, { vm.setTaskStatus(t.id, if (t.status == "Done") "To Do" else "Done") }) }
        }
    }
}

@Composable
fun TaskCard(task: NexaTask, projectName: String, onClick: () -> Unit, onToggle: () -> Unit) {
    NexaCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Checkbox(checked = task.status == "Done", onCheckedChange = { onToggle() }, modifier = Modifier.size(38.dp), colors = CheckboxDefaults.colors(checkedColor = Purple))
            Spacer(Modifier.width(5.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(task.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) { TaskStatusChip(task.status); PriorityChip(task.priority) }
                Text("$projectName · ${prettyDate(task.dueDate)}", color = Secondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = Secondary)
        }
    }
}

@Composable
fun TaskDetailScreen(state: NexaUiState, id: String, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    val task = state.tasks.find { it.id == id }
    if (task == null) { MissingRecord("Task not found", back); return }
    val project = state.projects.find { it.id == task.projectId }
    var deleteDialog by remember { mutableStateOf(false) }
    var comment by remember { mutableStateOf("") }
    var statusExpanded by remember { mutableStateOf(false) }
    NexaShell("Task details", "${project?.name ?: "Project"} · ${project?.category ?: "Workspace"}", onNavigate = go, onBack = back,
        trailingText = "Edit", onTrailing = { go("editTask/${task.id}") },
        footer = {
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
                PrimaryButton(if (task.status == "Done") "↩ Reopen task" else "✓ Mark complete", { vm.setTaskStatus(task.id, if (task.status == "Done") "To Do" else "Done") })
            }
        }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 21.sp, color = Ink)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { TaskStatusChip(task.status); PriorityChip(task.priority) }
            NexaCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleAvatar(state.team.find { it.name == task.assignee }?.initials ?: "NA")
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(task.assignee, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text("Assignee · ${state.team.find { it.name == task.assignee }?.role ?: "Member"}", color = Secondary, fontSize = 12.sp)
                    }
                    Icon(Icons.Outlined.ChevronRight, null)
                }
                Text("Due ${prettyDate(task.dueDate)}", color = Secondary, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }
            NexaCard { Text("Description", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink); Spacer(Modifier.height(6.dp)); Text(task.description.ifBlank { "No description." }, fontSize = 13.sp, color = Secondary, lineHeight = 19.sp) }
            NexaCard {
                Text("Acceptance checklist · ${task.completedItems.size} of ${task.checklist.size}", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                task.checklist.forEachIndexed { idx, text ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = idx in task.completedItems, onCheckedChange = { vm.toggleChecklist(task.id, idx) }, modifier = Modifier.size(36.dp), colors = CheckboxDefaults.colors(checkedColor = Purple))
                        Text(text, fontSize = 12.sp, color = Ink)
                    }
                }
                if (task.checklist.isEmpty()) Text("No checklist items yet.", color = Secondary, fontSize = 12.sp)
            }
            SectionTitle("Activity & comments")
            task.comments.forEach { NexaCard { Text(it, color = Secondary, fontSize = 13.sp) } }
            NexaInput(comment, { comment = it }, "Add a comment...", placeholder = "Write a comment")
            SecondaryButton("Post comment", { vm.addComment(task.id, comment); comment = "" })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { SecondaryButton("Change status", { statusExpanded = true }); DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) { listOf("To Do", "In Progress", "Done").forEach { status -> DropdownMenuItem(text = { Text(status) }, onClick = { vm.setTaskStatus(task.id, status); statusExpanded = false }) } } }
                Box(Modifier.weight(1f)) { OutlinedButton(onClick = { deleteDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger)) { Icon(Icons.Outlined.Delete, null); Spacer(Modifier.width(3.dp)); Text("Delete") } }
            }
        }
    }
    if (deleteDialog) AlertDialog(
        onDismissRequest = { deleteDialog = false },
        icon = { Icon(Icons.Outlined.DeleteForever, null, tint = Danger) },
        title = { Text("Delete this task?", fontWeight = FontWeight.Bold) },
        text = { Text("${task.title}\n\nThis removes the task and its comments. This action cannot be undone.") },
        confirmButton = { Button(onClick = { vm.deleteTask(task.id); deleteDialog = false; go("tasks") }, colors = ButtonDefaults.buttonColors(containerColor = Danger)) { Text("Delete task") } },
        dismissButton = { OutlinedButton(onClick = { deleteDialog = false }) { Text("Cancel") } }
    )
}

@Composable
fun MissingRecord(message: String, back: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Background).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        EmptyState(message, "The item may have been deleted.", "Go back", back)
    }
}

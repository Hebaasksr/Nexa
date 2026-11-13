package sa.edu.iau.nexa.ui.screens

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import sa.edu.iau.nexa.model.*
import sa.edu.iau.nexa.ui.components.*
import sa.edu.iau.nexa.ui.theme.*
import sa.edu.iau.nexa.viewmodel.NexaUiState
import sa.edu.iau.nexa.viewmodel.NexaViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(state: NexaUiState, go: (String) -> Unit) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    var chosen by remember { mutableStateOf(LocalDate.now()) }
    val first = month.atDay(1)
    val offset = first.dayOfWeek.value % 7
    val items = state.tasks.filter { it.dueDate == chosen.toString() }
    NexaShell("Calendar", "Organize tasks and events by date", "Calendar", go,
        trailingIcon = Icons.Outlined.Notifications, onTrailing = { go("notifications") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = Ink)
                IconButton(onClick = { month = month.minusMonths(1) }) { Icon(Icons.Outlined.ChevronLeft, "Previous month", tint = Purple) }
                IconButton(onClick = { month = month.plusMonths(1) }) { Icon(Icons.Outlined.ChevronRight, "Next month", tint = Purple) }
            }
            NexaCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { Text(it, fontSize = 10.sp, color = Secondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center) } }
                Spacer(Modifier.height(12.dp))
                val total = offset + month.lengthOfMonth()
                repeat((total + 6) / 7) { week ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        repeat(7) { day ->
                            val num = week * 7 + day - offset + 1
                            val valid = num in 1..month.lengthOfMonth()
                            val date = if (valid) month.atDay(num) else null
                            val selected = chosen == date
                            Column(Modifier.weight(1f).height(47.dp).clip(RoundedCornerShape(22.dp))
                                .background(if (selected) PurpleLight else Color.Transparent)
                                .then(if (date != null) Modifier.clickable { chosen = date } else Modifier),
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(if (valid) "$num" else "", color = if (selected) Purple else Ink, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                                if (date != null && state.tasks.any { it.dueDate == date.toString() }) Box(Modifier.size(5.dp).clip(RoundedCornerShape(3.dp)).background(Cyan))
                            }
                        }
                    }
                }
            }
            SectionTitle("${prettyDate(chosen.toString())} · ${items.size} task(s)")
            if (items.isEmpty()) EmptyState("No tasks on this date", "Select a highlighted day or create a new task.")
            else items.forEach { task -> TaskCard(task, state.projects.find { it.id == task.projectId }?.name ?: "Project", { go("task/${task.id}") }, { go("task/${task.id}") }) }
            PrimaryButton("+ Schedule a task", { go("newTask") })
        }
    }
}

@Composable
fun FilesScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            var display = "Imported file"
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) display = cursor.getString(0) ?: display
            }
            vm.addLocalFile(display)
        }
    }
    NexaShell("Project Files", "Shared documents and design assets", "More", go, back,
        trailingIcon = Icons.Outlined.FileUpload, onTrailing = { picker.launch("*/*") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SearchBarNexa(query, { query = it }, "Search files and folders...")
            FilterRow(listOf("All", "Documents", "Images", "Videos", "Others"), filter, { filter = it })
            SectionTitle("Folders")
            NexaCard(onClick = { filter = "Images" }) { Row { Icon(Icons.Outlined.Folder, null, tint = Purple); Spacer(Modifier.width(10.dp)); Text("Design Assets", fontWeight = FontWeight.SemiBold, color = Ink) } }
            NexaCard(onClick = { filter = "Documents" }) { Row { Icon(Icons.Outlined.Folder, null, tint = Cyan); Spacer(Modifier.width(10.dp)); Text("Documentation", fontWeight = FontWeight.SemiBold, color = Ink) } }
            SectionTitle("Recent Files")
            val visible = state.files.filter { (filter == "All" || it.category == filter) && it.name.contains(query, true) }
            if (visible.isEmpty()) EmptyState("No files found", "Change your filters or add a file.")
            visible.forEach { file -> FileRow(file) { go("preview/${android.net.Uri.encode(file.name)}") } }
            PrimaryButton("Upload file", { picker.launch("*/*") }, icon = Icons.Outlined.FileUpload)
            Text("Selected files are listed locally on this device. This demo does not upload them to a server.", fontSize = 11.sp, color = Secondary)
        }
    }
}

@Composable
fun FileRow(file: NexaFile, onClick: () -> Unit) {
    NexaCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(43.dp).clip(RoundedCornerShape(12.dp)).background(PurpleLight), contentAlignment = Alignment.Center) { Text(file.type.take(3), color = Purple, fontWeight = FontWeight.Bold, fontSize = 10.sp) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) { Text(file.name, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${file.size} · ${file.category}", color = Secondary, fontSize = 11.sp) }
            Icon(Icons.Outlined.ChevronRight, null, tint = Secondary)
        }
    }
}

@Composable
fun FilePreviewScreen(state: NexaUiState, filename: String, go: (String) -> Unit, back: () -> Unit) {
    val file = state.files.find { it.name == filename }
    if (file == null) { MissingRecord("File not found", back); return }
    NexaShell("File preview", "Mobile App Redesign · ${file.category}", onNavigate = go, onBack = back,
        trailingIcon = Icons.Outlined.Share, onTrailing = { go("files") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
            NexaCard { StatusChip(file.type); Spacer(Modifier.height(8.dp)); Text(file.name, fontWeight = FontWeight.Bold, color = Ink); Text(file.size, fontSize = 12.sp, color = Secondary) }
            NexaCard {
                Icon(Icons.Outlined.Description, null, modifier = Modifier.size(48.dp), tint = Purple)
                Spacer(Modifier.height(12.dp))
                Text("${file.name}", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Spacer(Modifier.height(10.dp))
                Text("NEXA project resource", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text("This is an offline file preview in the student prototype. Files are listed for the local demo; cloud hosting, downloading, and collaboration are not connected.", fontSize = 13.sp, color = Secondary, lineHeight = 20.sp)
            }
            SecondaryButton("Back to files", back)
        }
    }
}

@Composable
fun TeamScreen(state: NexaUiState, go: (String) -> Unit, back: () -> Unit) {
    var search by remember { mutableStateOf("") }
    NexaShell("Team", "Work together. Build better.", "More", go, back, trailingIcon = Icons.Outlined.ChatBubbleOutline, onTrailing = { go("messages") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Product team · ${state.team.size} members", fontSize = 12.sp, color = Secondary)
            SearchBarNexa(search, { search = it }, "Search members...")
            state.team.filter { it.name.contains(search, true) }.forEach { member -> MemberRow(member) { go("chat") } }
            PrimaryButton("+ Invite a new team member", { go("invite") })
        }
    }
}

@Composable
fun MemberRow(member: TeamMember, onClick: () -> Unit) {
    NexaCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircleAvatar(member.initials)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink)
                Text(member.role, fontSize = 12.sp, color = Secondary)
                Text(if (member.online) "● Online" else "● Away", fontSize = 11.sp, color = if (member.online) Success else Secondary)
            }
            Icon(Icons.Outlined.ChatBubbleOutline, "Message", tint = Purple)
        }
    }
}

@Composable
fun InviteScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Member") }
    var project by remember { mutableStateOf(state.projects.firstOrNull()?.name.orEmpty()) }
    var error by remember { mutableStateOf(false) }
    NexaShell("Invite a member", "Product team · ${state.team.size} current members", onNavigate = go, onBack = back, trailingText = "Cancel", onTrailing = back,
        footer = { ActionFooter("Send invitation", { if (email.contains('@')) { vm.invite(email); go("team") } else error = true }, onSecondary = back) }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Invite a collaborator by email. They'll join only after accepting the invitation.", color = Secondary, fontSize = 13.sp)
            NexaInput(email, { email = it; error = false }, "Email address *", placeholder = "collaborator@example.com", keyboard = androidx.compose.ui.text.input.KeyboardType.Email, isError = error)
            if (error) Text("Enter a valid email address.", color = Danger, fontSize = 12.sp)
            NexaDropdown("Role *", role, listOf("Member", "Viewer", "Admin"), { role = it })
            NexaCard { Text("Member permissions", fontWeight = FontWeight.Bold, color = Ink); Text("Members can view projects and update their assigned tasks.", color = Secondary, fontSize = 12.sp) }
            NexaDropdown("Project access", project, state.projects.map { it.name }, { project = it })
            Text("This demo stores a pending invitation locally. No email is sent and the joined-member count stays unchanged.", color = Secondary, fontSize = 12.sp)
            if (state.invited.isNotEmpty()) Text("Pending: ${state.invited.joinToString()}", color = Purple, fontSize = 12.sp)
        }
    }
}

@Composable
fun MessagesScreen(state: NexaUiState, go: (String) -> Unit, back: () -> Unit) {
    var search by remember { mutableStateOf("") }
    NexaShell("Messages", "Stay connected with the Product team", "More", go, back,
        trailingIcon = Icons.Outlined.EditNote, onTrailing = { go("chat") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            SearchBarNexa(search, { search = it }, "Search people or channels...")
            SectionTitle("Project channels")
            listOf("Mobile App Redesign", "Research Platform").filter { it.contains(search, true) }.forEach {
                NexaCard(onClick = { go("chat") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleAvatar("#")
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) { Text(it, color = Ink, fontWeight = FontWeight.Bold); Text("Team updates and discussions", color = Secondary, fontSize = 12.sp) }
                        Icon(Icons.Outlined.ChevronRight, null, tint = Secondary)
                    }
                }
            }
            SectionTitle("Direct messages")
            state.team.drop(1).filter { it.name.contains(search, true) }.forEach { MemberRow(it) { go("chat") } }
        }
    }
}

@Composable
fun ChatScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    var draft by remember { mutableStateOf("") }
    NexaShell("Mobile App Redesign", "Project channel · ${state.team.size} members", onNavigate = go, onBack = back,
        trailingText = "Members", onTrailing = { go("team") },
        footer = {
            Row(Modifier.fillMaxWidth().background(Color.White).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(draft, { draft = it }, modifier = Modifier.weight(1f), singleLine = true,
                    placeholder = { Text("Message the team...", fontSize = 13.sp) }, shape = RoundedCornerShape(24.dp))
                IconButton(onClick = { vm.sendMessage(draft); draft = "" }, enabled = draft.isNotBlank()) {
                    Icon(Icons.Outlined.Send, "Send message", tint = Purple)
                }
            }
        }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Product team · Local demo conversation", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 11.sp, color = Secondary)
            state.messages.forEach { m ->
                val mine = m.sender == state.userName
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
                    Column(Modifier.fillMaxWidth(.88f)) {
                        Text("${m.sender} · ${m.time}", fontSize = 10.sp, color = Secondary)
                        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(if (mine) Purple else Color.White).padding(13.dp)) {
                            Text(m.text, color = if (mine) Color.White else Ink, fontSize = 13.sp, lineHeight = 19.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    NexaShell("Notifications", "${state.notifications.count { !it.read }} unread · Product workspace", "More", go, back,
        trailingIcon = Icons.Outlined.Settings, onTrailing = { go("settings") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("Today", "Mark all as read") { vm.markNotificationsRead() }
            state.notifications.forEach { n ->
                NexaCard(onClick = { vm.markNotificationRead(n.id); go(n.destination) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.NotificationsActive, null, tint = Purple)
                        Spacer(Modifier.width(8.dp))
                        Text(n.title, color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 14.sp)
                        if (!n.read) StatusChip("New")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(n.body, color = Secondary, fontSize = 12.sp, lineHeight = 17.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("View details ›", color = Purple, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun GlobalSearchScreen(state: NexaUiState, go: (String) -> Unit, back: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    NexaShell("Search", "Search across your Product workspace", "Home", go, back, trailingText = "Cancel", onTrailing = back) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SearchBarNexa(query, { query = it }, "Search projects, tasks or files")
            FilterRow(listOf("All", "Projects", "Tasks", "Files"), filter, { filter = it })
            if (query.isBlank()) Text("Enter a name or keyword to find matching items.", color = Secondary, fontSize = 13.sp)
            else {
                val projects = state.projects.filter { it.name.contains(query, true) }
                val tasks = state.tasks.filter { it.title.contains(query, true) || it.description.contains(query, true) }
                val files = state.files.filter { it.name.contains(query, true) }
                if ((filter == "Projects" || filter == "All") && projects.isNotEmpty()) { SectionTitle("Projects · ${projects.size}"); projects.forEach { ProjectCard(it) { go("project/${it.id}") } } }
                if ((filter == "Tasks" || filter == "All") && tasks.isNotEmpty()) { SectionTitle("Tasks · ${tasks.size}"); tasks.forEach { TaskCard(it, "Task", { go("task/${it.id}") }, { go("task/${it.id}") }) } }
                if ((filter == "Files" || filter == "All") && files.isNotEmpty()) { SectionTitle("Files · ${files.size}"); files.forEach { FileRow(it) { go("preview/${android.net.Uri.encode(it.name)}") } } }
                if (projects.isEmpty() && tasks.isEmpty() && files.isEmpty()) EmptyState("No matches for \"$query\"", "Try another keyword.")
            }
        }
    }
}

@Composable
fun MoreScreen(state: NexaUiState, go: (String) -> Unit) {
    NexaShell("More", "Your workspace, people and preferences", "More", go,
        trailingIcon = Icons.Outlined.Notifications, onTrailing = { go("notifications") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
            NexaCard(onClick = { go("settings") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleAvatar("HE"); Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) { Text(state.userName, fontWeight = FontWeight.Bold); Text("Product Designer · Workspace", color = Secondary, fontSize = 12.sp) }
                    Icon(Icons.Outlined.ChevronRight, null, tint = Secondary)
                }
                Text("Product workspace · ${state.team.size} members", fontSize = 12.sp, color = Color(0xFF087782), modifier = Modifier.padding(top = 8.dp))
            }
            Text("WORKSPACE", color = Secondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            NexaCard {
                MoreItem("Projects", "${state.projects.count { !it.archived }} active projects", Icons.Outlined.Folder, "projects", go)
                MoreItem("Team", "${state.team.size} members · Manage invitations", Icons.Outlined.Group, "team", go)
                MoreItem("Files", "Design assets and documents", Icons.Outlined.FolderCopy, "files", go)
                MoreItem("Messages", "Channels and direct conversations", Icons.Outlined.ChatBubbleOutline, "messages", go)
                MoreItem("Notifications", "Assignments and deadlines", Icons.Outlined.NotificationsNone, "notifications", go)
            }
            NexaCard {
                MoreItem("Profile & Settings", "Account, preferences and security", Icons.Outlined.Settings, "settings", go)
                MoreItem("Help & support", "Guides and frequently asked questions", Icons.Outlined.HelpOutline, "help", go)
            }
        }
    }
}

@Composable
fun MoreItem(title: String, detail: String, icon: androidx.compose.ui.graphics.vector.ImageVector, dest: String, go: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 65.dp).clickable { go(dest) }, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Purple, modifier = Modifier.size(23.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold, color = Ink, fontSize = 14.sp); Text(detail, fontSize = 11.sp, color = Secondary) }
        Icon(Icons.Outlined.ChevronRight, null, tint = Secondary)
    }
}

@Composable
fun SettingsScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    NexaShell("Profile & Settings", "Personal preferences for your workspace", onNavigate = go, onBack = back,
        trailingIcon = Icons.Outlined.Notifications, onTrailing = { go("notifications") }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NexaCard {
                Row(verticalAlignment = Alignment.CenterVertically) { CircleAvatar("HE"); Spacer(Modifier.width(10.dp)); Column { Text(state.userName, fontWeight = FontWeight.Bold); Text("Product Designer", color = Secondary, fontSize = 12.sp) } }
                Spacer(Modifier.height(7.dp)); StatusChip("Online", Success, SuccessBackground)
            }
            Text("ACCOUNT & SECURITY", color = Secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            NexaCard {
                MoreItem("Personal information", "Name, role and email", Icons.Outlined.PersonOutline, "help", go)
                MoreItem("Password & security", "Password and two-step verification", Icons.Outlined.Security, "reset", go)
                MoreItem("Workspace", "Product team · ${state.team.size} members", Icons.Outlined.Group, "team", go)
            }
            Text("PREFERENCES", color = Secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            NexaCard {
                MoreItem("Appearance & language", "Light · English · Local time", Icons.Outlined.LightMode, "help", go)
                PreferenceSwitch("Task & deadline alerts", state.alerts) { vm.setPrefs(alerts = it) }
                PreferenceSwitch("Mentions & messages", state.mentions) { vm.setPrefs(mentions = it) }
                PreferenceSwitch("Email digest", state.digest) { vm.setPrefs(digest = it) }
            }
            SecondaryButton("Sign out", { vm.signOut(); go("login") })
            Text("NEXA · Version 1.0 · Student offline prototype", fontSize = 11.sp, color = Secondary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PreferenceSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 50.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Ink, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange, colors = SwitchDefaults.colors(checkedTrackColor = Purple))
    }
}

@Composable
fun HelpScreen(go: (String) -> Unit, back: () -> Unit) {
    NexaShell("Help & support", "Answers for common questions", onNavigate = go, onBack = back) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
            listOf(
                "How do I add a task?" to "Use New at the bottom, enter a title and select a project, then tap Create task.",
                "How do I change task status?" to "Open the task, choose Change status, or use the checkbox in My Tasks.",
                "How do I invite a member?" to "Open More → Team → Invite. Invitations are simulated locally in this prototype.",
                "Are my changes saved?" to "Projects, tasks, comments, notifications and messages are saved on this device. Nothing is synced online."
            ).forEach { (q, answer) -> NexaCard { Text(q, fontWeight = FontWeight.Bold, color = Ink); Spacer(Modifier.height(6.dp)); Text(answer, color = Secondary, fontSize = 13.sp) } }
            Text("Contact your project team for support. NEXA is a university HCI prototype, not a production service.", fontSize = 12.sp, color = Secondary)
        }
    }
}

package sa.edu.iau.nexa.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import sa.edu.iau.nexa.model.*
import sa.edu.iau.nexa.ui.components.*
import sa.edu.iau.nexa.ui.theme.*
import sa.edu.iau.nexa.viewmodel.NexaUiState
import sa.edu.iau.nexa.viewmodel.NexaViewModel
import java.time.LocalDate

@Composable
fun DateChoice(value: String, onChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(13.dp)).background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(13.dp)).clickable {
                val date = runCatching { LocalDate.parse(value) }.getOrDefault(LocalDate.now())
                DatePickerDialog(context, { _, year, month, day -> onChange("%04d-%02d-%02d".format(year, month + 1, day)) }, date.year, date.monthValue - 1, date.dayOfMonth).show()
            }.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(prettyDate(value), color = Ink, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
            Icon(Icons.Outlined.CalendarMonth, "Pick date", tint = Secondary, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
fun NewTaskScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit, projectId: String = "", editingId: String? = null) {
    val editing = state.tasks.find { it.id == editingId }
    var project by rememberSaveable(editingId) { mutableStateOf(editing?.projectId ?: projectId.ifBlank { state.projects.firstOrNull()?.id.orEmpty() }) }
    var title by rememberSaveable(editingId) { mutableStateOf(editing?.title ?: "") }
    var description by rememberSaveable(editingId) { mutableStateOf(editing?.description ?: "") }
    var assignee by rememberSaveable(editingId) { mutableStateOf(editing?.assignee ?: state.team.getOrNull(3)?.name.orEmpty()) }
    var due by rememberSaveable(editingId) { mutableStateOf(editing?.dueDate ?: LocalDate.now().plusDays(7).toString()) }
    var priority by rememberSaveable(editingId) { mutableStateOf(editing?.priority ?: "High") }
    var status by rememberSaveable(editingId) { mutableStateOf(editing?.status ?: "To Do") }
    var error by remember { mutableStateOf(false) }
    val editMode = editing != null
    NexaShell(if (editMode) "Edit task" else "New task", if (editMode) "Update the task details" else "Turn the next step into a clear assignment", onNavigate = go,
        onBack = back, trailingText = "Cancel", onTrailing = back,
        footer = {
            ActionFooter(if (editMode) "Save changes" else "Create task", {
                if (title.isBlank() || project.isBlank()) error = true else if (editMode) {
                    vm.updateTask(editingId!!, title, description, assignee, due, priority, status); go("task/${editingId}")
                } else {
                    val id = vm.createTask(project, title, description, assignee, due, priority); go("success/$id")
                }
            }, onSecondary = back)
        }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
            Text("Required fields are marked with *", color = Secondary, fontSize = 12.sp)
            if (error) Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(DangerBackground).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ErrorOutline, null, tint = Danger, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Task not created. Add a title and choose a project.", color = Danger, fontSize = 12.sp)
            }
            NexaDropdown("Project *", state.projects.find { it.id == project }?.name.orEmpty(), state.projects.map { it.name }, { value -> project = state.projects.first { it.name == value }.id })
            NexaInput(title, { title = it; if (it.isNotBlank()) error = false }, "Task title *", placeholder = "Enter a task title", isError = error && title.isBlank())
            if (error && title.isBlank()) Text("⚠ Enter a title to create this task.", color = Danger, fontSize = 12.sp)
            NexaInput(description, { description = it }, "Description", placeholder = "Describe this task", singleLine = false, minLines = 2)
            NexaDropdown("Assignee", assignee, state.team.map { it.name }, { assignee = it })
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DateChoice(due, { due = it }, "Due date *", Modifier.weight(1f))
                NexaDropdown("Priority", priority, listOf("High", "Medium", "Low"), { priority = it }, Modifier.weight(1f))
            }
            if (editMode) NexaDropdown("Status", status, listOf("To Do", "In Progress", "Done"), { status = it })
            Text("${assignee.ifBlank { "The assigned member" }} will be notified in this local demo when the task is created.", color = Secondary, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun NewProjectScreen(state: NexaUiState, vm: NexaViewModel, go: (String) -> Unit, back: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Team Project") }
    var due by rememberSaveable { mutableStateOf(LocalDate.now().plusMonths(1).toString()) }
    var error by remember { mutableStateOf(false) }
    NexaShell("New project", "Product workspace · Project draft", onNavigate = go, onBack = back, trailingText = "Cancel", onTrailing = back,
        footer = { ActionFooter("Create project", {
            if (name.isBlank()) error = true else { val id = vm.createProject(name, description, category, due); go("project/$id") }
        }, onSecondary = back) }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Required fields are marked with *", color = Secondary, fontSize = 12.sp)
            if (error) Text("Please enter a project name.", color = Danger, fontSize = 13.sp)
            NexaInput(name, { name = it; error = false }, "Project name *", placeholder = "Customer Insights Hub", isError = error)
            NexaInput(description, { description = it }, "Description", placeholder = "What is this project about?", singleLine = false, minLines = 3)
            NexaDropdown("Project type", category, listOf("Team Project", "Personal", "UI/UX Design", "Research"), { category = it })
            DateChoice(due, { due = it }, "End date *")
            NexaCard {
                Text("Included members", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { state.team.take(5).forEach { CircleAvatar(it.initials, Modifier.size(36.dp)) } }
                Text("Owner: ${state.userName} · Product team", color = Secondary, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun TaskSuccessScreen(state: NexaUiState, id: String, go: (String) -> Unit, back: () -> Unit) {
    val task = state.tasks.find { it.id == id }
    val projectName = state.projects.find { it.id == task?.projectId }?.name ?: "Project"
    if (task == null) { MissingRecord("Task not found", back); return }
    NexaShell("Task created", "Saved to $projectName", onNavigate = go, onBack = back,
        trailingText = "Done", onTrailing = { go("tasks") },
        footer = { ActionFooter("View task", { go("task/$id") }, "Back to tasks", { go("tasks") }) }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(Modifier.height(28.dp))
            Box(Modifier.size(80.dp).clip(CircleShape).background(SuccessBackground), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Check, null, tint = Success, modifier = Modifier.size(44.dp))
            }
            Text("Ready for the next step", fontWeight = FontWeight.Bold, color = Ink, fontSize = 21.sp)
            Text("Your task is saved and ${task.assignee} has been notified.", color = Secondary, fontSize = 13.sp, textAlign = TextAlign.Center)
            NexaCard {
                Text(task.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { TaskStatusChip(task.status); PriorityChip(task.priority) }
                Spacer(Modifier.height(8.dp))
                Text("$projectName · Due ${prettyDate(task.dueDate)}", fontSize = 12.sp, color = Secondary)
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleAvatar(state.team.find { it.name == task.assignee }?.initials ?: "NA")
                    Spacer(Modifier.width(10.dp))
                    Text(task.assignee, color = Ink, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f)); Icon(Icons.Outlined.ChevronRight, null, tint = Secondary)
                }
            }
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(SuccessBackground).padding(13.dp)) {
                Icon(Icons.Outlined.CheckCircle, null, tint = Success, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text("Included in the project task list.", color = Success, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AuthScreen(vm: NexaViewModel, mode: String, go: (String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val isRegister = mode == "register"; val isReset = mode == "reset"
    val authState by vm.uiState.collectAsState()
    Column(Modifier.fillMaxSize().background(HeaderGradient).verticalScroll(rememberScrollState()).padding(horizontal = 28.dp, vertical = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(82.dp).clip(RoundedCornerShape(24.dp)).background(Purple.copy(alpha = .26f)), contentAlignment = Alignment.Center) { Text("N", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Cyan) }
        Spacer(Modifier.height(16.dp))
        NexaWordmark()
        Spacer(Modifier.height(44.dp))
        Text(if (isReset) "Reset your password" else if (isRegister) "Create your account" else "Welcome back", modifier = Modifier.fillMaxWidth(), fontSize = 27.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(if (isReset) "Enter your email to request a reset." else if (isRegister) "One place for your team and every project." else "Sign in to continue to your projects", modifier = Modifier.fillMaxWidth(), fontSize = 13.sp, color = Color(0xFFB8BDD9))
        Spacer(Modifier.height(28.dp))
        if (isRegister) { AuthField(name, { name = it }, "Full name", "Your full name"); Spacer(Modifier.height(14.dp)) }
        AuthField(email, { email = it }, "Email address", "you@example.com"); Spacer(Modifier.height(14.dp))
        if (!isReset) {
            AuthField(password, { password = it }, "Password", "Enter your password", true)
            if (isRegister) { Spacer(Modifier.height(14.dp)); AuthField(confirm, { confirm = it }, "Confirm password", "Re-enter your password", true) }
        }
        if (!isRegister && !isReset) { TextButton(onClick = { go("reset") }, modifier = Modifier.align(Alignment.End)) { Text("Forgot password?", color = Cyan) } }
        if (isRegister) Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(agreed, { agreed = it }, colors = CheckboxDefaults.colors(checkedColor = Purple))
            Text("I agree to the Terms of Service and Privacy Policy", color = Color.White, fontSize = 12.sp)
        }
        val shownMessage = authState.authError ?: authState.authSuccess ?: message
        if (shownMessage.isNotEmpty()) Text(shownMessage, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), color = if (authState.authError != null || message.isNotEmpty()) Color(0xFFFF9BA7) else Cyan, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        PrimaryButton(if (isReset) "Send reset link" else if (isRegister) "Create account" else "Sign In", {
            when {
                authState.authLoading -> Unit
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> message = "Please enter a valid email address."
                isReset -> { message = ""; vm.resetPassword(email) }
                isRegister && name.isBlank() -> message = "Please enter your name."
                isRegister && (!agreed || password != confirm) -> message = "Please accept the terms and confirm the same password."
                password.length < 8 -> message = "Password must be at least 8 characters."
                isRegister -> { message = ""; vm.register(name, email, password) }
                else -> { message = ""; vm.login(email, password) }
            }
        }, enabled = !authState.authLoading)
        if (authState.authLoading) { Spacer(Modifier.height(8.dp)); CircularProgressIndicator(color = Cyan, modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.height(14.dp))
        SecondaryButton(if (isRegister || isReset) "Back to sign in" else "Create an account", { go(if (isRegister || isReset) "login" else "register") })
        Spacer(Modifier.height(26.dp))
        Text("NEXA · Student HCI prototype · Offline demo", textAlign = TextAlign.Center, fontSize = 11.sp, color = Color(0xFFBBC0D5))
    }
}

@Composable
fun AuthField(value: String, onChange: (String) -> Unit, label: String, placeholder: String, password: Boolean = false) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label.uppercase(), fontSize = 12.sp, color = Color(0xFFB8BDD9), fontWeight = FontWeight.SemiBold)
        OutlinedTextField(value, onChange, placeholder = { Text(placeholder, color = Color(0xFF9299B7), fontSize = 14.sp) },
            singleLine = true, visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Purple, unfocusedBorderColor = Color(0xFF252647), focusedContainerColor = Color(0xFF12142E), unfocusedContainerColor = Color(0xFF12142E)),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
    }
}

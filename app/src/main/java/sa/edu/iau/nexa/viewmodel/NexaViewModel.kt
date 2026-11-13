package sa.edu.iau.nexa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import sa.edu.iau.nexa.data.NexaStore
import sa.edu.iau.nexa.model.*
import java.time.LocalDate
import java.util.UUID

data class NexaUiState(
    val loggedIn: Boolean = false,
    val userName: String = "Heba Askar",
    val projects: List<NexaProject> = emptyList(),
    val tasks: List<NexaTask> = emptyList(),
    val team: List<TeamMember> = emptyList(),
    val files: List<NexaFile> = emptyList(),
    val messages: List<Message> = emptyList(),
    val notifications: List<NexaNotification> = emptyList(),
    val alerts: Boolean = true,
    val mentions: Boolean = true,
    val digest: Boolean = false,
    val invited: List<String> = emptyList(),
    val authLoading: Boolean = false,
    val authError: String? = null,
    val authSuccess: String? = null
)

class NexaViewModel(application: Application): AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val store = NexaStore(application)
    private val _uiState = MutableStateFlow(NexaUiState(
        store.loadLoggedIn(), store.loadName(), store.loadProjects(), store.loadTasks(),
        store.loadTeam(), store.loadFiles(), store.loadMessages(), store.loadNotifications(),
        store.loadAlerts(), store.loadMentions(), store.loadDigest()
    ).copy(loggedIn = auth.currentUser != null, userName = auth.currentUser?.displayName ?: store.loadName()))
    val uiState: StateFlow<NexaUiState> = _uiState
    private fun uuid() = UUID.randomUUID().toString()

    fun login(email: String, password: String) = viewModelScope.launch {
        authenticate { auth.signInWithEmailAndPassword(email.trim(), password).await() }
    }
    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        authenticate {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: error("Account could not be created")
            user.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()).await()
            firestore.collection("users").document(user.uid).set(mapOf("uid" to user.uid, "name" to name.trim(), "email" to user.email, "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp())).await()
        }
    }
    fun resetPassword(email: String) = viewModelScope.launch {
        _uiState.update { it.copy(authLoading = true, authError = null, authSuccess = null) }
        try { auth.sendPasswordResetEmail(email.trim()).await(); _uiState.update { it.copy(authLoading = false, authSuccess = "Password reset email sent.") } }
        catch (e: Exception) { _uiState.update { it.copy(authLoading = false, authError = e.localizedMessage ?: "Unable to send reset email.") } }
    }
    private suspend fun authenticate(action: suspend () -> Unit) {
        _uiState.update { it.copy(authLoading = true, authError = null, authSuccess = null) }
        try { action(); val user = auth.currentUser; val name = user?.displayName ?: user?.email.orEmpty(); _uiState.update { it.copy(authLoading = false, loggedIn = true, userName = name) }; store.saveSession(name, true) }
        catch (e: Exception) { _uiState.update { it.copy(authLoading = false, authError = e.localizedMessage ?: "Authentication failed.") } }
    }
    fun signOut() {
        auth.signOut()
        _uiState.update { it.copy(loggedIn = false) }
        store.saveSession(_uiState.value.userName, false)
    }
    fun createProject(name: String, description: String, category: String, due: String): String {
        val id = uuid()
        val p = NexaProject(id, name.trim(), category.ifBlank { "Team Project" }, _uiState.value.userName, description, due)
        _uiState.update { it.copy(projects = it.projects + p) }
        store.saveProjects(_uiState.value.projects)
        return id
    }
    fun archiveProject(id: String) {
        _uiState.update { s -> s.copy(projects = s.projects.map { if (it.id == id) it.copy(archived = !it.archived) else it }) }
        store.saveProjects(_uiState.value.projects)
    }
    fun createTask(projectId: String, title: String, description: String, assignee: String, due: String, priority: String): String {
        val id = uuid()
        val task = NexaTask(id, projectId, title.trim(), description.trim(), assignee, due, priority)
        _uiState.update { it.copy(tasks = it.tasks + task, notifications = listOf(NexaNotification(uuid(), "Task created", "$title has been assigned to $assignee.", "task/$id")) + it.notifications) }
        store.saveTasks(_uiState.value.tasks); store.saveNotifications(_uiState.value.notifications)
        return id
    }
    fun updateTask(id: String, title: String, description: String, assignee: String, due: String, priority: String, status: String) {
        _uiState.update { s -> s.copy(tasks = s.tasks.map { if (it.id == id) it.copy(title = title, description = description, assignee = assignee, dueDate = due, priority = priority, status = status) else it }) }
        store.saveTasks(_uiState.value.tasks)
    }
    fun setTaskStatus(id: String, status: String) {
        _uiState.update { s -> s.copy(tasks = s.tasks.map { if (it.id == id) it.copy(status = status) else it }) }
        store.saveTasks(_uiState.value.tasks)
    }
    fun toggleChecklist(id: String, index: Int) {
        _uiState.update { s -> s.copy(tasks = s.tasks.map { t ->
            if (t.id != id) t else t.copy(completedItems = if (index in t.completedItems) t.completedItems - index else t.completedItems + index)
        }) }
        store.saveTasks(_uiState.value.tasks)
    }
    fun addComment(id: String, text: String) {
        if (text.isBlank()) return
        _uiState.update { s -> s.copy(tasks = s.tasks.map { if (it.id == id) it.copy(comments = it.comments + text.trim()) else it }) }
        store.saveTasks(_uiState.value.tasks)
    }
    fun deleteTask(id: String) {
        _uiState.update { it.copy(tasks = it.tasks.filterNot { t -> t.id == id }) }
        store.saveTasks(_uiState.value.tasks)
    }
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        _uiState.update { it.copy(messages = it.messages + Message(it.userName, text.trim(), "Just now")) }
        store.saveMessages(_uiState.value.messages)
    }
    fun markNotificationsRead() {
        _uiState.update { it.copy(notifications = it.notifications.map { n -> n.copy(read = true) }) }
        store.saveNotifications(_uiState.value.notifications)
    }
    fun markNotificationRead(id: String) {
        _uiState.update { it.copy(notifications = it.notifications.map { n -> if (n.id == id) n.copy(read = true) else n }) }
        store.saveNotifications(_uiState.value.notifications)
    }
    fun invite(email: String) {
        if (!email.contains('@')) return
        _uiState.update { it.copy(invited = it.invited + email) }
        // Pending invitation is local-only and does not change joined team count.
    }
    fun addLocalFile(filename: String) {
        val name = filename.ifBlank { "Uploaded file" }
        val ext = name.substringAfterLast('.', "").lowercase()
        val category = when (ext) { "png", "jpg", "jpeg", "webp" -> "Images"; "mp4", "mov" -> "Videos"; "pdf", "doc", "docx", "txt" -> "Documents"; else -> "Others" }
        val f = NexaFile(name, ext.uppercase().ifBlank { "FILE" }, "Local", category)
        _uiState.update { it.copy(files = listOf(f) + it.files) }
        store.saveFiles(_uiState.value.files)
    }
    fun setPrefs(alerts: Boolean = _uiState.value.alerts, mentions: Boolean = _uiState.value.mentions, digest: Boolean = _uiState.value.digest) {
        _uiState.update { it.copy(alerts = alerts, mentions = mentions, digest = digest) }
        store.savePreferences(alerts, mentions, digest)
    }
}

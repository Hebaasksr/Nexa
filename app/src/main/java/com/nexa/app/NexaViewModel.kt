package com.nexa.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TeamMember(val name: String, val role: String, val status: String, val accent: Long)

enum class InviteRole(val title: String, val permissions: String) {
    Member("Member", "Can view the project, update assigned tasks, share files, and join project conversations."),
    Admin("Admin", "Can manage members, tasks, files, and project settings."),
    Viewer("Viewer", "Can view project content and read conversations."),
}

data class ProjectChannel(
    val id: String,
    val name: String,
    val description: String = "",
    val startDate: String = "Aug 20, 2026",
    val endDate: String = "Oct 15, 2026",
    val teamName: String = "Product team",
    val status: String = "Active",
    val progress: Int = 60,
    val archived: Boolean = false,
)

data class ChatMessage(
    val author: String,
    val time: String,
    val body: String,
    val isMine: Boolean = false,
    val attachment: String? = null,
)

data class Invitation(val email: String, val role: InviteRole, val projectId: String, val note: String = "")

data class NexaUiState(
    val members: List<TeamMember>,
    val projects: List<ProjectChannel>,
    val messages: Map<String, List<ChatMessage>>,
    val invitations: List<Invitation> = emptyList(),
)

class NexaViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        NexaUiState(
            members = listOf(
                TeamMember("Heba Askar", "Product Designer", "Online", 0xFF5BDBB1),
                TeamMember("Ghaida AlQarni ", "Frontend Developer", "Online", 0xFF5DD5EC),
                TeamMember("Fatimah Alarfaj ", "Backend Developer", "Away", 0xFFFCAB71),
                TeamMember("Jawaher Hazazi", "UI/UX Designer", "Online", 0xFFB79AFF),
                TeamMember("Munirah Albuainain", "Researcher", "Offline", 0xFF93B6FF),
            ),
            projects = listOf(
                ProjectChannel("redesign", "Mobile App Redesign", "Redesign the mobile application with a focus on user experience, accessibility, and modern UI design principles.", "Aug 20, 2026", "Oct 15, 2026", "Product team", "Active", 60),
                ProjectChannel("website", "Website Development", "Build a responsive website for the product team.", "Sep 01, 2026", "Oct 30, 2026", "Personal", "In Progress", 35),
                ProjectChannel("research", "Research Platform", "Plan and deliver the product research platform.", "Aug 10, 2026", "Sep 30, 2026", "Product team", "Near Done", 80),
            ),
            messages = mapOf(
                "redesign" to listOf(
                    ChatMessage("Munirah Albuainain", "9:35 AM", "Prototype handoff is Sep 28. Can we review accessibility before sharing?"),
                    ChatMessage("Heba Askar", "9:37 AM", "Yes. Let’s check contrast, focus order and touch targets.", true, "UI_Mockups.fig"),
                    ChatMessage("Ghaida AlQarni", "9:39 AM", "I’ll check keyboard navigation and make sure errors are announced clearly."),
                    ChatMessage("Fatima Alharbi", "9:40 AM", "I’ll confirm API errors use the same helpful language."),
                ),
            ),
        )
    )
    val state = _state.asStateFlow()

    fun invite(email: String, role: InviteRole, projectId: String, note: String = ""): Boolean {
        val address = email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(address).matches()) return false
        while (true) {
            val current = _state.value
            if (current.projects.none { it.id == projectId } ||
                current.invitations.any { it.projectId == projectId && it.email.equals(address, ignoreCase = true) }
            ) return false
            val updated = current.copy(invitations = current.invitations + Invitation(address, role, projectId, note))
            if (_state.compareAndSet(current, updated)) return true
        }
    }

    fun revoke(invitation: Invitation) {
        _state.update { it.copy(invitations = it.invitations - invitation) }
    }

    fun saveProject(id: String?, name: String, description: String, start: String, end: String, team: String): ProjectChannel? {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return null
        var saved: ProjectChannel? = null
        _state.update { current ->
            val existing = id?.let { key -> current.projects.firstOrNull { it.id == key } }
            val project = (existing ?: ProjectChannel("project-${System.currentTimeMillis()}", cleanName)).copy(
                name = cleanName,
                description = description.trim(),
                startDate = start,
                endDate = end,
                teamName = team,
            )
            saved = project
            val updated = if (existing == null) current.projects + project
            else current.projects.map { if (it.id == id) project else it }
            current.copy(projects = updated)
        }
        return saved
    }

    fun send(projectId: String, body: String, attachment: String?) {
        val message = body.trim()
        if (message.isEmpty() && attachment == null) return
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.ENGLISH))
        _state.update { current ->
            val messages = current.messages[projectId].orEmpty() + ChatMessage("Heba Askar", time, message, true, attachment)
            current.copy(messages = current.messages + (projectId to messages))
        }
    }
}


package sa.edu.iau.nexa.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class NexaProject(
    val id: String,
    val name: String,
    val category: String = "Team Project",
    val owner: String = "Heba Askar",
    val description: String = "",
    val dueDate: String = "2026-10-15",
    val progress: Float = 0f,
    val archived: Boolean = false
)

data class NexaTask(
    val id: String,
    val projectId: String,
    val title: String,
    val description: String,
    val assignee: String,
    val dueDate: String,
    val priority: String = "Medium",
    val status: String = "To Do",
    val checklist: List<String> = emptyList(),
    val completedItems: List<Int> = emptyList(),
    val comments: List<String> = emptyList()
)

data class TeamMember(
    val name: String,
    val role: String,
    val initials: String,
    val online: Boolean = true
)

data class Message(val sender: String, val text: String, val time: String = "Today")

data class NexaNotification(
    val id: String,
    val title: String,
    val body: String,
    val destination: String,
    val read: Boolean = false
)

data class NexaFile(val name: String, val type: String, val size: String, val category: String)

val TEAM = listOf(
    TeamMember("Heba Askar", "Product Designer", "HE"),
    TeamMember("Haneen Alotaibi", "Frontend Developer", "HA"),
    TeamMember("Fatima Alharbi", "Backend Developer", "FA", false),
    TeamMember("Haya Alqahtani", "UI/UX Designer", "HQ"),
    TeamMember("Kayan Almutairi", "Researcher", "KA", false)
)

val DEMO_FILES = listOf(
    NexaFile("Project_Proposal.pdf", "PDF", "2.4 MB", "Documents"),
    NexaFile("UI_Mockups.fig", "FIG", "4.8 MB", "Others"),
    NexaFile("Meeting Notes.docx", "DOC", "1.2 MB", "Documents"),
    NexaFile("Brand_Assets.png", "IMG", "1.8 MB", "Images"),
    NexaFile("Demo_Recording.mp4", "VID", "14 MB", "Videos")
)

fun prettyDate(date: String): String = try {
    LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH))
} catch (_: Exception) { date }

val SAMPLE_PROJECTS = listOf(
    NexaProject("p1", "Mobile App Redesign", "UI/UX Design · Team Project", "Heba Askar", "Redesign the mobile application with a focus on user experience, accessibility, and modern UI design principles.", "2026-10-15", .60f),
    NexaProject("p2", "Website Development", "Frontend · Personal", "Haneen Alotaibi", "Create a responsive website and secure authentication flow.", "2026-10-30", .35f),
    NexaProject("p3", "Research Platform", "Research · Group Project", "Kayan Almutairi", "Present research findings and build a shared research workspace.", "2026-09-30", .80f)
)
val SAMPLE_TASKS = listOf(
    NexaTask("t1", "p1", "Design system components", "Create reusable buttons, cards, and input components.", "Heba Askar", "2026-08-28", "High", "Done"),
    NexaTask("t2", "p2", "Develop login functionality", "Build the website sign-in flow with email validation, secure session handling and helpful error messages.", "Haneen Alotaibi", "2026-09-17", "Medium", "In Progress", listOf("Email and password fields validated", "Session persists after refresh", "Keyboard and error-state testing"), listOf(0), listOf("Validation is ready. I'm checking session expiry before today's review.")),
    NexaTask("t3", "p2", "API integration", "Connect app features to the required API endpoints.", "Fatima Alharbi", "2026-09-17", "Medium", "In Progress"),
    NexaTask("t4", "p3", "User testing", "Collect and summarize usability feedback.", "Kayan Almutairi", "2026-09-22", "Low", "To Do"),
    NexaTask("t5", "p1", "Final presentation", "Prepare the final prototype handoff presentation.", "Haya Alqahtani", "2026-09-28", "High", "To Do")
)

val SAMPLE_NOTIFICATIONS = listOf(
    NexaNotification("n1", "Task assigned", "Heba assigned Develop login functionality to Haneen.", "task/t2"),
    NexaNotification("n2", "Haya mentioned you", "Can we review accessibility before prototype handoff?", "chat"),
    NexaNotification("n3", "Deadline today", "Develop login functionality is due Sep 17.", "task/t2"),
    NexaNotification("n4", "File updated", "Heba updated Design Assets: 12 files, 245 MB.", "files", true)
)

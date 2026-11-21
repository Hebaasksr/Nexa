package com.nexa.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

internal enum class TaskStatus(val title: String) {
    Todo("To Do"), Progress("In Progress"), Done("Completed"),
}

internal data class TaskCheck(val title: String, val checked: Boolean = false)

internal data class NexaTask(
    val id: String,
    val project: String,
    val title: String,
    val description: String,
    val assignee: String,
    val due: LocalDate,
    val priority: String,
    val category: String = "General",
    val status: TaskStatus = TaskStatus.Todo,
    val checklist: List<TaskCheck> = emptyList(),
    val comments: List<String> = emptyList(),
)

internal data class TaskFields(
    val project: String = "",
    val title: String = "",
    val description: String = "",
    val assignee: String = "",
    val due: String = "",
    val priority: String = "",
)

internal class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = application.getSharedPreferences("nexa_tasks", 0)
    private val mutableTasks = MutableStateFlow(load())
    val tasks = mutableTasks.asStateFlow()

    private fun load(): List<NexaTask> {
        val saved = storage.getString("tasks", null) ?: return demoTasks()
        return runCatching {
            val array = JSONArray(saved)
            List(array.length()) { index ->
                val task = array.getJSONObject(index)
                val checks = task.getJSONArray("checklist")
                val comments = task.getJSONArray("comments")
                NexaTask(
                    task.getString("id"), task.getString("project"), task.getString("title"),
                    task.getString("description"), task.getString("assignee"),
                    LocalDate.parse(task.getString("due")), task.getString("priority"),
                    task.getString("category"), TaskStatus.valueOf(task.getString("status")),
                    List(checks.length()) { i ->
                        val check = checks.getJSONObject(i)
                        TaskCheck(check.getString("title"), check.getBoolean("checked"))
                    },
                    List(comments.length()) { i -> comments.getString(i) },
                )
            }
        }.getOrElse { demoTasks() }
    }

    private fun update(tasks: List<NexaTask>) {
        val array = JSONArray()
        tasks.forEach { task ->
            val checks = JSONArray()
            task.checklist.forEach { check ->
                checks.put(JSONObject().put("title", check.title).put("checked", check.checked))
            }
            array.put(
                JSONObject()
                    .put("id", task.id).put("project", task.project).put("title", task.title)
                    .put("description", task.description).put("assignee", task.assignee)
                    .put("due", task.due.toString()).put("priority", task.priority)
                    .put("category", task.category).put("status", task.status.name)
                    .put("checklist", checks).put("comments", JSONArray(task.comments)),
            )
        }
        storage.edit().putString("tasks", array.toString()).apply()
        mutableTasks.value = tasks
    }

    fun save(id: String?, fields: TaskFields): Boolean {
        val due = runCatching { LocalDate.parse(fields.due) }.getOrNull() ?: return false
        if (fields.title.isBlank() || fields.project.isBlank()) return false
        val project = fields.project
        val title = fields.title.trim()
        val description = fields.description.trim()
        if (id == null) {
            val task = NexaTask(UUID.randomUUID().toString(), project, title, description, fields.assignee, due, fields.priority)
            update(listOf(task) + tasks.value)
        } else {
            if (tasks.value.none { it.id == id }) return false
            edit(id) {
                it.copy(project = project, title = title, description = description,
                    assignee = fields.assignee, due = due, priority = fields.priority)
            }
        }
        return true
    }

    fun delete(id: String) = update(tasks.value.filterNot { it.id == id })

    fun toggleDone(id: String) = edit(id) {
        it.copy(status = if (it.status == TaskStatus.Done) TaskStatus.Todo else TaskStatus.Done)
    }

    fun toggleCheck(id: String, index: Int) = edit(id) { task ->
        task.copy(checklist = task.checklist.mapIndexed { i, check ->
            if (i == index) check.copy(checked = !check.checked) else check
        })
    }

    fun comment(id: String, text: String) {
        if (text.isNotBlank()) edit(id) { it.copy(comments = it.comments + text.trim()) }
    }

    private fun edit(id: String, change: (NexaTask) -> NexaTask) {
        update(tasks.value.map { if (it.id == id) change(it) else it })
    }
}

internal fun demoTasks(today: LocalDate = LocalDate.now()): List<NexaTask> = listOf(
    NexaTask("design", "Mobile App Redesign", "Design system components", "Build reusable UI components.",
        "Munirah Albuainain", today.minusDays(20), "High", "UI/UX Design", TaskStatus.Done),
    NexaTask("login", "Website Development", "Develop login functionality",
        "Build the website sign-in flow with email validation, secure session handling and helpful error messages.",
        "Ghaida AlQarni", today, "Medium", "Frontend", TaskStatus.Progress,
        listOf(TaskCheck("Email and password fields validated", true),
            TaskCheck("Session persists after refresh"), TaskCheck("Keyboard and error-state testing")),
        listOf("Validation is ready. I’m checking session expiry before today’s review.")),
    NexaTask("api", "Website Development", "API integration", "Connect the app to the project API.",
        "Heba Askar", today, "Medium", "Backend", TaskStatus.Progress),
    NexaTask("testing", "Research Sprint", "User testing", "Run usability sessions and capture feedback.",
        "Jawaher Hazazi", today.plusDays(5), "Low", "Research"),
    NexaTask("presentation", "Mobile App Redesign", "Final presentation", "Prepare the final presentation.",
        "", today.plusDays(11), "High"),
)

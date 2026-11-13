package sa.edu.iau.nexa.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import sa.edu.iau.nexa.model.*

/** Local-only demo persistence. No password or secret is persisted. */
class NexaStore(context: Context) {
    private val prefs = context.getSharedPreferences("nexa_local_v1", Context.MODE_PRIVATE)

    fun loadProjects(): List<NexaProject> = readList("projects", SAMPLE_PROJECTS) { o ->
        NexaProject(o.getString("id"), o.getString("name"), o.optString("category", "Team Project"), o.optString("owner", "Heba Askar"), o.optString("description"), o.optString("dueDate", "2026-10-15"), o.optDouble("progress", 0.0).toFloat(), o.optBoolean("archived"))
    }
    fun loadTasks(): List<NexaTask> = readList("tasks", SAMPLE_TASKS) { o ->
        NexaTask(o.getString("id"), o.getString("projectId"), o.getString("title"), o.optString("description"), o.optString("assignee"), o.optString("dueDate"), o.optString("priority", "Medium"), o.optString("status", "To Do"), o.optJSONArray("checklist").strings(), o.optJSONArray("completedItems").numbers(), o.optJSONArray("comments").strings())
    }
    fun loadNotifications(): List<NexaNotification> = readList("notifications", SAMPLE_NOTIFICATIONS) { o ->
        NexaNotification(o.getString("id"), o.getString("title"), o.optString("body"), o.optString("destination", "dashboard"), o.optBoolean("read"))
    }
    fun loadMessages(): List<Message> = readList("messages", listOf(
        Message("Haya Alqahtani", "Prototype handoff is Sep 28. Can we review accessibility before sharing?", "9:35 AM"),
        Message("Heba Askar", "Yes. Let's check contrast, focus order and touch targets first.", "9:37 AM"),
        Message("Haneen Alotaibi", "I'll check keyboard navigation and errors are announced clearly.", "9:39 AM"),
        Message("Fatima Alharbi", "I'll confirm API errors use the same helpful language.", "9:40 AM")
    )) { o -> Message(o.getString("sender"), o.getString("text"), o.optString("time", "Today")) }
    fun loadFiles(): List<NexaFile> = readList("files", DEMO_FILES) { o ->
        NexaFile(o.getString("name"), o.optString("type"), o.optString("size"), o.optString("category"))
    }
    fun loadTeam(): List<TeamMember> = readList("team", TEAM) { o ->
        TeamMember(o.getString("name"), o.optString("role"), o.optString("initials"), o.optBoolean("online", false))
    }
    fun loadName() = prefs.getString("name", "Heba Askar") ?: "Heba Askar"
    fun loadLoggedIn() = prefs.getBoolean("logged", false)
    fun loadAlerts() = prefs.getBoolean("alerts", true)
    fun loadMentions() = prefs.getBoolean("mentions", true)
    fun loadDigest() = prefs.getBoolean("digest", false)
    fun saveSession(name: String, loggedIn: Boolean) = prefs.edit().putString("name", name).putBoolean("logged", loggedIn).apply()
    fun savePreferences(alerts: Boolean, mentions: Boolean, digest: Boolean) = prefs.edit().putBoolean("alerts", alerts).putBoolean("mentions", mentions).putBoolean("digest", digest).apply()
    fun saveProjects(items: List<NexaProject>) = writeList("projects", items) { p ->
        JSONObject().put("id", p.id).put("name", p.name).put("category", p.category).put("owner", p.owner).put("description", p.description).put("dueDate", p.dueDate).put("progress", p.progress.toDouble()).put("archived", p.archived)
    }
    fun saveTasks(items: List<NexaTask>) = writeList("tasks", items) { t ->
        JSONObject().put("id", t.id).put("projectId", t.projectId).put("title", t.title).put("description", t.description).put("assignee", t.assignee).put("dueDate", t.dueDate).put("priority", t.priority).put("status", t.status).put("checklist", JSONArray(t.checklist)).put("completedItems", JSONArray(t.completedItems)).put("comments", JSONArray(t.comments))
    }
    fun saveNotifications(items: List<NexaNotification>) = writeList("notifications", items) { n ->
        JSONObject().put("id", n.id).put("title", n.title).put("body", n.body).put("destination", n.destination).put("read", n.read)
    }
    fun saveMessages(items: List<Message>) = writeList("messages", items) { m ->
        JSONObject().put("sender", m.sender).put("text", m.text).put("time", m.time)
    }
    fun saveFiles(items: List<NexaFile>) = writeList("files", items) { f ->
        JSONObject().put("name", f.name).put("type", f.type).put("size", f.size).put("category", f.category)
    }
    fun saveTeam(items: List<TeamMember>) = writeList("team", items) { m ->
        JSONObject().put("name", m.name).put("role", m.role).put("initials", m.initials).put("online", m.online)
    }

    private fun <T> readList(key: String, fallback: List<T>, parse: (JSONObject) -> T): List<T> {
        val raw = prefs.getString(key, null) ?: return fallback
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { idx -> parse(arr.getJSONObject(idx)) }
        } catch (_: Exception) { fallback }
    }
    private fun <T> writeList(key: String, items: List<T>, encode: (T) -> JSONObject) {
        val arr = JSONArray()
        items.forEach { arr.put(encode(it)) }
        prefs.edit().putString(key, arr.toString()).apply()
    }
}

private fun JSONArray?.strings(): List<String> = if (this == null) emptyList() else List(length()) { optString(it) }
private fun JSONArray?.numbers(): List<Int> = if (this == null) emptyList() else List(length()) { optInt(it) }

package com.nexa.app

import android.net.Uri

/** Central route names used by the app's NavHost and navigation actions. */
object AppRoutes {
    const val HOME = "home"
    const val TASKS = "tasks"
    const val TASK_NEW = "task_new"
    const val TASK_EDIT = "task_edit/{taskId}"
    const val TASK_DETAILS = "task/{taskId}"
    const val CALENDAR = "calendar"
    const val SEARCH = "search"
    const val NOTIFICATIONS = "notifications"
    const val PROJECTS = "projects"
    const val PROJECT_NEW = "project_new"
    const val PROJECT_EDIT = "project_edit/{projectId}"
    const val PROJECT_DETAILS = "project_detail/{projectId}"
    const val FILES = "files"
    const val MORE = "more"
    const val PROFILE_SETTINGS = "profile_settings"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val RESET_PASSWORD = "reset_password"
    const val TEAM = "team"
    const val INVITE_MEMBER = "invite_member"
    const val CHATS = "chats"
    const val PROJECT_CHAT = "chat/{projectId}"

    const val TASK_ID_ARGUMENT = "taskId"
    const val PROJECT_ID_ARGUMENT = "projectId"

    fun taskDetails(taskId: String): String = "task/${Uri.encode(taskId)}"
    fun editTask(taskId: String): String = "task_edit/${Uri.encode(taskId)}"
    fun projectDetails(projectId: String): String = "project_detail/${Uri.encode(projectId)}"
    fun editProject(projectId: String): String = "project_edit/${Uri.encode(projectId)}"
    fun projectChat(projectId: String): String = "chat/${Uri.encode(projectId)}"
}

package com.nexa.app

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

private fun NavController.navigateToTopLevel(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        popUpTo(AppRoutes.HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToHomeTab() = navigateToTopLevel(AppRoutes.HOME)
fun NavController.navigateToTaskList() = navigateToTopLevel(AppRoutes.TASKS)
fun NavController.navigateToCalendar() = navigateToTopLevel(AppRoutes.CALENDAR)
fun NavController.navigateToMore() = navigateToTopLevel(AppRoutes.MORE)

fun NavController.navigateToNewTask() = navigate(AppRoutes.TASK_NEW)
fun NavController.navigateToNewTask(builder: NavOptionsBuilder.() -> Unit) = navigate(AppRoutes.TASK_NEW, builder)
fun NavController.navigateToTaskDetails(taskId: String) = navigate(AppRoutes.taskDetails(taskId))
fun NavController.navigateToEditTask(taskId: String) = navigate(AppRoutes.editTask(taskId))

fun NavController.navigateToSearch() = navigate(AppRoutes.SEARCH)
fun NavController.navigateToNotifications() = navigate(AppRoutes.NOTIFICATIONS)
fun NavController.navigateToProjects() = navigate(AppRoutes.PROJECTS)
fun NavController.navigateToNewProject() = navigate(AppRoutes.PROJECT_NEW)
fun NavController.navigateToNewProject(builder: NavOptionsBuilder.() -> Unit) = navigate(AppRoutes.PROJECT_NEW, builder)
fun NavController.navigateToProjectDetails(projectId: String) = navigate(AppRoutes.projectDetails(projectId))
fun NavController.navigateToProjectDetails(
    projectId: String,
    builder: NavOptionsBuilder.() -> Unit,
) = navigate(AppRoutes.projectDetails(projectId), builder)
fun NavController.navigateToEditProject(projectId: String) = navigate(AppRoutes.editProject(projectId))
fun NavController.navigateToFiles() = navigate(AppRoutes.FILES)
fun NavController.navigateToProfileSettings() = navigate(AppRoutes.PROFILE_SETTINGS)
fun NavController.navigateToLogin() = navigate(AppRoutes.LOGIN)
fun NavController.navigateToSignUp() = navigate(AppRoutes.SIGNUP)
fun NavController.navigateToResetPassword() = navigate(AppRoutes.RESET_PASSWORD)
fun NavController.navigateToTeam() = navigate(AppRoutes.TEAM)
fun NavController.navigateToInviteMember() = navigate(AppRoutes.INVITE_MEMBER)
fun NavController.navigateToChats() = navigate(AppRoutes.CHATS)
fun NavController.navigateToProjectChat(projectId: String) = navigate(AppRoutes.projectChat(projectId))

package com.nexa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: NexaViewModel by viewModels()
    private val tasksViewModel: TasksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(0xFF08091A.toInt()),
            navigationBarStyle = SystemBarStyle.light(
                0xFFF5F3FA.toInt(),
                0xFF08091A.toInt(),
            ),
        )

        setContent {
            NexaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NexaApp(viewModel, tasksViewModel)
                }
            }
        }
    }
}

@Composable
private fun NexaApp(
    viewModel: NexaViewModel,
    tasksViewModel: TasksViewModel,
) {
    val nav = rememberNavController()
    val state by viewModel.state.collectAsState()
    val tasks by tasksViewModel.tasks.collectAsState()

    var newItemSheet by rememberSaveable { mutableStateOf(false) }
    var deletingTaskId by rememberSaveable { mutableStateOf<String?>(null) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onNew: () -> Unit = {
        newItemSheet = true
    }

    fun openTab(route: String) {
        if (nav.currentDestination?.route == route) return

        nav.navigate(route) {
            popUpTo("home") {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val onTabSelected: (Int) -> Unit = { tab ->
        when (tab) {
            0 -> openTab("home")
            1 -> openTab("tasks")
            3 -> openTab("calendar")
            4 -> openTab("more")
        }
    }

    val projectNames = state.projects.map { it.name }
    val assignees = state.members.map { it.name }

    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = nav,
            startDestination = "home",
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToProjectDetails = { projectId ->
                        nav.navigate("project_detail/$projectId")
                    },
                    onNavigateToViewAllProjects = {
                        nav.navigate("projects")
                    },
                    onNavigateToCreateTask = onNew,
                    onNavigateToNotifications = {
                        nav.navigate("notifications")
                    },
                    onNavigateToMessages = {
                        nav.navigate("chats")
                    },
                    onTabSelected = onTabSelected,
                    onOpenSearch = {
                        nav.navigate("search")
                    },
                )
            }

            composable("tasks") {
                MyTasksScreen(
                    tasks = tasks,
                    onAdd = {
                        nav.navigate("task_new")
                    },
                    onDetails = { taskId ->
                        nav.navigate("task/$taskId")
                    },
                    onEdit = { taskId ->
                        nav.navigate("task_edit/$taskId")
                    },
                    onDelete = { taskId ->
                        deletingTaskId = taskId
                    },
                    onToggle = tasksViewModel::toggleDone,
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                    onNotifications = {
                        nav.navigate("notifications")
                    },
                    onMessages = {
                        nav.navigate("chats")
                    },
                    onBack = {
                        nav.popBackStack()
                    },
                )
            }

            composable("task_new") {
                AddEditTaskScreen(
                    task = null,
                    projects = projectNames,
                    assignees = assignees,
                    onSave = { fields ->
                        tasksViewModel.save(null, fields).also { saved ->
                            if (saved) {
                                openTab("tasks")
                                scope.launch {
                                    snackbar.showSnackbar("Task created")
                                }
                            }
                        }
                    },
                    onBack = {
                        nav.popBackStack()
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                )
            }

            composable("task_edit/{taskId}") { entry ->
                val taskId = entry.arguments?.getString("taskId")
                val task = tasks.firstOrNull { it.id == taskId }

                if (task != null) {
                    AddEditTaskScreen(
                        task = task,
                        projects = projectNames,
                        assignees = assignees,
                        onSave = { fields ->
                            tasksViewModel.save(task.id, fields).also { saved ->
                                if (saved) {
                                    nav.popBackStack()
                                    scope.launch {
                                        snackbar.showSnackbar("Changes saved")
                                    }
                                }
                            }
                        },
                        onBack = {
                            nav.popBackStack()
                        },
                        onTabSelected = onTabSelected,
                        onNew = onNew,
                    )
                }
            }

            composable("task/{taskId}") { entry ->
                val taskId = entry.arguments?.getString("taskId")
                val task = tasks.firstOrNull { it.id == taskId }

                if (task != null) {
                    TaskDetailsScreen(
                        task = task,
                        onBack = {
                            nav.popBackStack()
                        },
                        onEdit = {
                            nav.navigate("task_edit/${task.id}")
                        },
                        onDelete = {
                            deletingTaskId = task.id
                        },
                        onToggleStatus = {
                            tasksViewModel.toggleDone(task.id)
                        },
                        onToggleCheck = { index ->
                            tasksViewModel.toggleCheck(task.id, index)
                        },
                        onComment = { comment ->
                            tasksViewModel.comment(task.id, comment)
                        },
                        member = state.members.firstOrNull {
                            it.name == task.assignee
                        },
                        onTabSelected = onTabSelected,
                        onNew = onNew,
                    )
                }
            }

            composable("calendar") {
                CalendarScreen(
                    onCreateTaskClick = onNew,
                    onNotificationsClick = {
                        nav.navigate("notifications")
                    },
                    onTabSelected = onTabSelected,
                )
            }

            composable("search") {
                SearchScreen(
                    onCreateTaskClick = onNew,
                    onBackClick = {
                        nav.popBackStack()
                    },
                    onCancelClick = {
                        nav.popBackStack()
                    },
                    onTabSelected = onTabSelected,
                )
            }

            composable("notifications") {
                NotificationsScreen(
                    onCreateTaskClick = onNew,
                    onBackClick = {
                        nav.popBackStack()
                    },
                    onTabSelected = onTabSelected,
                )
            }

            composable("projects") {
                ProjectsScreen(
                    projects = state.projects,
                    onOpenProject = { projectId ->
                        nav.navigate("project_detail/$projectId")
                    },
                    onAddProject = {
                        nav.navigate("project_new")
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                    onBack = {
                        nav.popBackStack()
                    },
                )
            }

            composable("project_new") {
                AddEditProjectScreen(
                    project = null,
                    onSave = { fields ->
                        val saved = viewModel.saveProject(
                            null,
                            fields.name,
                            fields.description,
                            fields.startDate,
                            fields.endDate,
                            fields.team,
                        )

                        if (saved != null) {
                            nav.navigate("project_detail/${saved.id}") {
                                popUpTo("project_new") {
                                    inclusive = true
                                }
                            }
                        }

                        saved != null
                    },
                    onBack = {
                        nav.popBackStack()
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                )
            }

            composable("project_edit/{projectId}") { entry ->
                val projectId = entry.arguments?.getString("projectId")
                val project = state.projects.firstOrNull { it.id == projectId }

                if (project != null) {
                    AddEditProjectScreen(
                        project = project,
                        onSave = { fields ->
                            val saved = viewModel.saveProject(
                                project.id,
                                fields.name,
                                fields.description,
                                fields.startDate,
                                fields.endDate,
                                fields.team,
                            )

                            if (saved != null) {
                                nav.popBackStack()
                            }

                            saved != null
                        },
                        onBack = {
                            nav.popBackStack()
                        },
                        onTabSelected = onTabSelected,
                        onNew = onNew,
                    )
                }
            }

            composable("project_detail/{projectId}") { entry ->
                val projectId = entry.arguments?.getString("projectId")
                val project = state.projects.firstOrNull { it.id == projectId }

                if (project != null) {
                    ProjectDetailsScreen(
                        project = project,
                        onBack = {
                            nav.popBackStack()
                        },
                        onEdit = {
                            nav.navigate("project_edit/${project.id}")
                        },
                        onOpenTasks = {
                            openTab("tasks")
                        },
                        onOpenFiles = {
                            nav.navigate("files")
                        },
                        onOpenTeam = {
                            nav.navigate("team")
                        },
                        onTabSelected = onTabSelected,
                        onNew = onNew,
                    )
                }
            }

            composable("files") {
                FilesScreen(
                    onBack = {
                        nav.popBackStack()
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                )
            }

            composable("more") {
                MoreScreen(
                    onProjects = {
                        nav.navigate("projects")
                    },
                    onTeam = {
                        nav.navigate("team")
                    },
                    onFiles = {
                        nav.navigate("files")
                    },
                    onMessages = {
                        nav.navigate("chats")
                    },
                    onNotifications = {
                        nav.navigate("notifications")
                    },
                    onProfile = {
                        nav.navigate("profile_settings")
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                )
            }

            composable("profile_settings") {
                ProfileSettingsScreen(
                    onBack = {
                        nav.popBackStack()
                    },
                    onResetPassword = {
                        nav.navigate("reset_password")
                    },
                    onSignOut = {
                        openTab("login")
                    },
                    onDeleteAccount = {
                        openTab("login")
                    },
                )
            }

            composable("login") {
                AuthFlowScreen(AuthMode.Login) {
                    openTab("home")
                }
            }

            composable("signup") {
                AuthFlowScreen(AuthMode.SignUp) {
                    openTab("home")
                }
            }

            composable("reset_password") {
                AuthFlowScreen(AuthMode.ResetPassword) {
                    nav.popBackStack()
                }
            }

            composable("team") {
                TeamScreen(
                    members = state.members,
                    invitations = state.invitations,
                    projects = state.projects,
                    onInvite = {
                        nav.navigate("invite_member")
                    },
                    onChats = {
                        nav.navigate("chats")
                    },
                    onProjectChat = { project ->
                        nav.navigate("chat/${project.id}")
                    },
                    onRevoke = viewModel::revoke,
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                    onBack = {
                        nav.popBackStack()
                    },
                )
            }

            composable("invite_member") {
                InviteMemberScreen(
                    memberCount = state.members.size,
                    projects = state.projects,
                    onBack = {
                        nav.popBackStack()
                    },
                    onInvite = viewModel::invite,
                )
            }

            composable("chats") {
                ChatListScreen(
                    projects = state.projects,
                    messages = state.messages,
                    onBack = {
                        nav.popBackStack()
                    },
                    onProjectClick = { project ->
                        nav.navigate("chat/${project.id}")
                    },
                )
            }

            composable("chat/{projectId}") { entry ->
                val projectId = entry.arguments?.getString("projectId")
                val project = state.projects.firstOrNull { it.id == projectId }

                if (project != null) {
                    ProjectChatScreen(
                        project = project,
                        messages = state.messages[project.id].orEmpty(),
                        memberCount = state.members.size,
                        onBack = {
                            nav.popBackStack()
                        },
                        onMembers = {
                            openTab("team")
                        },
                        onSend = { body, attachment ->
                            viewModel.send(project.id, body, attachment)
                        },
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 78.dp),
        )
    }

    if (newItemSheet) {
        NewItemSheet(
            onDismiss = {
                newItemSheet = false
            },
            onTask = {
                newItemSheet = false
                nav.navigate("task_new") {
                    launchSingleTop = true
                }
            },
            onProject = {
                newItemSheet = false
                nav.navigate("project_new") {
                    launchSingleTop = true
                }
            },
        )
    }

    tasks.firstOrNull { it.id == deletingTaskId }?.let { task ->
        DeleteTaskDialog(
            task = task,
            onDismiss = {
                deletingTaskId = null
            },
            onConfirm = {
                deletingTaskId = null

                if (nav.currentDestination?.route == "task/{taskId}") {
                    nav.popBackStack()
                }

                tasksViewModel.delete(task.id)
            },
        )
    }
}
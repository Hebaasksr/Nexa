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

    val onTabSelected: (Int) -> Unit = { tab ->
        when (tab) {
            0 -> nav.navigateToHomeTab()
            1 -> nav.navigateToTaskList()
            3 -> nav.navigateToCalendar()
            4 -> nav.navigateToMore()
        }
    }

    val projectNames = state.projects.map { it.name }
    val assignees = state.members.map { it.name }

    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = nav,
            startDestination = AppRoutes.HOME,
        ) {
            composable(AppRoutes.HOME) {
                HomeScreen(
                    onNavigateToProjectDetails = { projectId ->
                        nav.navigateToProjectDetails(projectId)
                    },
                    onNavigateToViewAllProjects = {
                        nav.navigateToProjects()
                    },
                    onNavigateToCreateTask = onNew,
                    onNavigateToNotifications = {
                        nav.navigateToNotifications()
                    },
                    onNavigateToMessages = {
                        nav.navigateToChats()
                    },
                    onTabSelected = onTabSelected,
                    onOpenSearch = {
                        nav.navigateToSearch()
                    },
                )
            }

            composable(AppRoutes.TASKS) {
                MyTasksScreen(
                    tasks = tasks,
                    onAdd = {
                        nav.navigateToNewTask()
                    },
                    onDetails = { taskId ->
                        nav.navigateToTaskDetails(taskId)
                    },
                    onEdit = { taskId ->
                        nav.navigateToEditTask(taskId)
                    },
                    onDelete = { taskId ->
                        deletingTaskId = taskId
                    },
                    onToggle = tasksViewModel::toggleDone,
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                    onNotifications = {
                        nav.navigateToNotifications()
                    },
                    onMessages = {
                        nav.navigateToChats()
                    },
                    onBack = {
                        nav.popBackStack()
                    },
                )
            }

            composable(AppRoutes.TASK_NEW) {
                AddEditTaskScreen(
                    task = null,
                    projects = projectNames,
                    assignees = assignees,
                    onSave = { fields ->
                        tasksViewModel.save(null, fields).also { saved ->
                            if (saved) {
                                nav.navigateToTaskList()
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

            composable(AppRoutes.TASK_EDIT) { entry ->
                val taskId = entry.arguments?.getString(AppRoutes.TASK_ID_ARGUMENT)
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

            composable(AppRoutes.TASK_DETAILS) { entry ->
                val taskId = entry.arguments?.getString(AppRoutes.TASK_ID_ARGUMENT)
                val task = tasks.firstOrNull { it.id == taskId }

                if (task != null) {
                    TaskDetailsScreen(
                        task = task,
                        onBack = {
                            nav.popBackStack()
                        },
                        onEdit = {
                            nav.navigateToEditTask(task.id)
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

            composable(AppRoutes.CALENDAR) {
                CalendarScreen(
                    onCreateTaskClick = onNew,
                    onNotificationsClick = {
                        nav.navigateToNotifications()
                    },
                    onTabSelected = onTabSelected,
                )
            }

            composable(AppRoutes.SEARCH) {
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

            composable(AppRoutes.NOTIFICATIONS) {
                NotificationsScreen(
                    onCreateTaskClick = onNew,
                    onBackClick = {
                        nav.popBackStack()
                    },
                    onTabSelected = onTabSelected,
                )
            }

            composable(AppRoutes.PROJECTS) {
                ProjectsScreen(
                    projects = state.projects,
                    onOpenProject = { projectId ->
                        nav.navigateToProjectDetails(projectId)
                    },
                    onAddProject = {
                        nav.navigateToNewProject()
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                    onBack = {
                        nav.popBackStack()
                    },
                )
            }

            composable(AppRoutes.PROJECT_NEW) {
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
                            nav.navigateToProjectDetails(saved.id) {
                                popUpTo(AppRoutes.PROJECT_NEW) {
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

            composable(AppRoutes.PROJECT_EDIT) { entry ->
                val projectId = entry.arguments?.getString(AppRoutes.PROJECT_ID_ARGUMENT)
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

            composable(AppRoutes.PROJECT_DETAILS) { entry ->
                val projectId = entry.arguments?.getString(AppRoutes.PROJECT_ID_ARGUMENT)
                val project = state.projects.firstOrNull { it.id == projectId }

                if (project != null) {
                    ProjectDetailsScreen(
                        project = project,
                        onBack = {
                            nav.popBackStack()
                        },
                        onEdit = {
                            nav.navigateToEditProject(project.id)
                        },
                        onOpenTasks = {
                            nav.navigateToTaskList()
                        },
                        onOpenFiles = {
                            nav.navigateToFiles()
                        },
                        onOpenTeam = {
                            nav.navigateToTeam()
                        },
                        onTabSelected = onTabSelected,
                        onNew = onNew,
                    )
                }
            }

            composable(AppRoutes.FILES) {
                FilesScreen(
                    onBack = {
                        nav.popBackStack()
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                )
            }

            composable(AppRoutes.MORE) {
                MoreScreen(
                    onProjects = {
                        nav.navigateToProjects()
                    },
                    onTeam = {
                        nav.navigateToTeam()
                    },
                    onFiles = {
                        nav.navigateToFiles()
                    },
                    onMessages = {
                        nav.navigateToChats()
                    },
                    onNotifications = {
                        nav.navigateToNotifications()
                    },
                    onProfile = {
                        nav.navigateToProfileSettings()
                    },
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                )
            }

            composable(AppRoutes.PROFILE_SETTINGS) {
                ProfileSettingsScreen(
                    onBack = {
                        nav.popBackStack()
                    },
                    onResetPassword = {
                        nav.navigateToResetPassword()
                    },
                    onSignOut = {
                        nav.navigateToLogin()
                    },
                    onDeleteAccount = {
                        nav.navigateToLogin()
                    },
                )
            }

            composable(AppRoutes.LOGIN) {
                AuthFlowScreen(AuthMode.Login) {
                    nav.navigateToHomeTab()
                }
            }

            composable(AppRoutes.SIGNUP) {
                AuthFlowScreen(AuthMode.SignUp) {
                    nav.navigateToHomeTab()
                }
            }

            composable(AppRoutes.RESET_PASSWORD) {
                AuthFlowScreen(AuthMode.ResetPassword) {
                    nav.popBackStack()
                }
            }

            composable(AppRoutes.TEAM) {
                TeamScreen(
                    members = state.members,
                    invitations = state.invitations,
                    projects = state.projects,
                    onInvite = {
                        nav.navigateToInviteMember()
                    },
                    onChats = {
                        nav.navigateToChats()
                    },
                    onProjectChat = { project ->
                        nav.navigateToProjectChat(project.id)
                    },
                    onRevoke = viewModel::revoke,
                    onTabSelected = onTabSelected,
                    onNew = onNew,
                    onBack = {
                        nav.popBackStack()
                    },
                )
            }

            composable(AppRoutes.INVITE_MEMBER) {
                InviteMemberScreen(
                    memberCount = state.members.size,
                    projects = state.projects,
                    onBack = {
                        nav.popBackStack()
                    },
                    onInvite = viewModel::invite,
                )
            }

            composable(AppRoutes.CHATS) {
                ChatListScreen(
                    projects = state.projects,
                    messages = state.messages,
                    onBack = {
                        nav.popBackStack()
                    },
                    onProjectClick = { project ->
                        nav.navigateToProjectChat(project.id)
                    },
                )
            }

            composable(AppRoutes.PROJECT_CHAT) { entry ->
                val projectId = entry.arguments?.getString(AppRoutes.PROJECT_ID_ARGUMENT)
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
                            nav.navigateToTeam()
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
                nav.navigateToNewTask {
                    launchSingleTop = true
                }
            },
            onProject = {
                newItemSheet = false
                nav.navigateToNewProject {
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

                if (nav.currentDestination?.route == AppRoutes.TASK_DETAILS) {
                    nav.popBackStack()
                }

                tasksViewModel.delete(task.id)
            },
        )
    }
}

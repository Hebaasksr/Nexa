package sa.edu.iau.nexa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import sa.edu.iau.nexa.ui.screens.*
import sa.edu.iau.nexa.ui.theme.Navy
import sa.edu.iau.nexa.ui.theme.NexaTheme
import sa.edu.iau.nexa.viewmodel.NexaViewModel

/**
 * NEXA – Kotlin + Jetpack Compose implementation of the university HCI prototype.
 * No Java classes, XML layouts, external backend or account password storage.
 */
class MainActivity : ComponentActivity() {
    private val vm: NexaViewModel by viewModels()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Navy.toArgb()
        window.navigationBarColor = android.graphics.Color.BLACK
        setContent {
            NexaTheme {
                Surface(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                    NexaNavigation(vm)
                }
            }
        }
    }
}

@Composable
private fun NexaNavigation(vm: NexaViewModel) {
    val state by vm.uiState.collectAsState()
    val nav: NavHostController = rememberNavController()
    val initial = remember { if (vm.uiState.value.loggedIn) "dashboard" else "login" }
    LaunchedEffect(state.loggedIn) {
        val target = if (state.loggedIn) "dashboard" else "login"
        if (nav.currentDestination?.route != target) {
            nav.navigate(target) { popUpTo(nav.graph.startDestinationId) { inclusive = true }; launchSingleTop = true }
        }
    }

    fun go(route: String) {
        val previous = nav.currentDestination?.route
        nav.navigate(route) {
            launchSingleTop = true
            if (route == "login" || (route == "dashboard" && previous in listOf("login", "register", "reset"))) {
                popUpTo(nav.graph.startDestinationId) { inclusive = true }
            }
        }
    }
    fun back() { if (!nav.popBackStack()) go("dashboard") }

    NavHost(navController = nav, startDestination = initial, modifier = Modifier.fillMaxSize()) {
        composable("login") { AuthScreen(vm, "login", ::go) }
        composable("register") { AuthScreen(vm, "register", ::go) }
        composable("reset") { AuthScreen(vm, "reset", ::go) }
        composable("dashboard") { DashboardScreen(state, ::go) }
        composable("projects") { ProjectsScreen(state, ::go, ::back) }
        composable("project/{projectId}", arguments = listOf(navArgument("projectId") { type = NavType.StringType })) { entry ->
            ProjectDetailScreen(state, entry.arguments?.getString("projectId").orEmpty(), vm, ::go, ::back)
        }
        composable("tasks") { TasksScreen(state, vm, ::go) }
        composable("task/{taskId}", arguments = listOf(navArgument("taskId") { type = NavType.StringType })) { entry ->
            TaskDetailScreen(state, entry.arguments?.getString("taskId").orEmpty(), vm, ::go, ::back)
        }
        composable("calendar") { CalendarScreen(state, ::go) }
        composable("files") { FilesScreen(state, vm, ::go, ::back) }
        composable("team") { TeamScreen(state, ::go, ::back) }
        composable("newTask?projectId={projectId}", arguments = listOf(navArgument("projectId") { type = NavType.StringType; defaultValue = "" })) { entry ->
            NewTaskScreen(state, vm, ::go, ::back, entry.arguments?.getString("projectId").orEmpty())
        }
        composable("editTask/{taskId}", arguments = listOf(navArgument("taskId") { type = NavType.StringType })) { entry ->
            NewTaskScreen(state, vm, ::go, ::back, editingId = entry.arguments?.getString("taskId"))
        }
        composable("newProject") { NewProjectScreen(state, vm, ::go, ::back) }
        composable("more") { MoreScreen(state, ::go) }
        composable("messages") { MessagesScreen(state, ::go, ::back) }
        composable("chat") { ChatScreen(state, vm, ::go, ::back) }
        composable("notifications") { NotificationsScreen(state, vm, ::go, ::back) }
        composable("settings") { SettingsScreen(state, vm, ::go, ::back) }
        composable("search") { GlobalSearchScreen(state, ::go, ::back) }
        composable("preview/{filename}", arguments = listOf(navArgument("filename") { type = NavType.StringType })) { entry ->
            FilePreviewScreen(state, entry.arguments?.getString("filename").orEmpty(), ::go, ::back)
        }
        composable("invite") { InviteScreen(state, vm, ::go, ::back) }
        composable("success/{taskId}", arguments = listOf(navArgument("taskId") { type = NavType.StringType })) { entry ->
            TaskSuccessScreen(state, entry.arguments?.getString("taskId").orEmpty(), ::go, ::back)
        }
        composable("help") { HelpScreen(::go, ::back) }
    }
}

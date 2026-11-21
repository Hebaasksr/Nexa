package com.nexa.app
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val DarkSurface = Color(0xFF191C3B)
private val LightBodyBg = Color(0xFFF7F8FC)
private val SlateTitle = Color(0xFF1E293B)
private val SlateDark = Color(0xFF0F172A)
private val SlateMuted = Color(0xFF64748B)
private val PlaceholderGray = Color(0xFF8F9BB3)
private val SoftBorder = Color(0xFFE2E8F0)
private val SoftPurpleBg = Color(0xFFF1EBFF)
private val DueSoonOrange = Color(0xFFE07A5F)

data class ProjectItem(
    val id: String,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val statusText: String,
    val statusBgColor: Color,
    val statusTextColor: Color,
    val progress: Float,
    val accentColor: Color
)

data class DashboardUiState(
    val userName: String = "Munirah Hassan",
    val userRole: String = "Team Leader",
    val dateString: String = "Tue, Nov 10, 2026",
    val searchQuery: String = "",
    val activeProjectsCount: Int = 3,
    val myTasksCount: Int = 5,
    val dueSoonCount: Int = 2,
    val projects: List<ProjectItem> = listOf(
        ProjectItem(
            id = "redesign",
            title = "Mobile App Redesign",
            category = "UI/UX Design · Team Project",
            icon = Icons.Outlined.Smartphone,
            statusText = "Active",
            statusBgColor = Color(0xFFE5F9F0),
            statusTextColor = Color(0xFF00B862),
            progress = 0.60f,
            accentColor = Color(0xFF6C47FF)
        ),
        ProjectItem(
            id = "website",
            title = "Website Development",
            category = "Frontend · Personal",
            icon = Icons.Outlined.Laptop,
            statusText = "In Progress",
            statusBgColor = Color(0xFFE5F7FF),
            statusTextColor = Color(0xFF0096C7),
            progress = 0.35f,
            accentColor = Color(0xFF00E5FF)
        ),
        ProjectItem(
            id = "research",
            title = "Research Platform",
            category = "Research · Group Project",
            icon = Icons.Outlined.Science,
            statusText = "Near Done",
            statusBgColor = SoftPurpleBg,
            statusTextColor = Color(0xFF6C47FF),
            progress = 0.80f,
            accentColor = Color(0xFF6C47FF)
        )
    ),
    val dueTodayTasksCount: Int = 2,
    val selectedTab: Int = 0
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToProjectDetails: (String) -> Unit = {},
    onNavigateToViewAllProjects: () -> Unit = {},
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToReviewTasks: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onOpenSearch: (() -> Unit)? = null
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val uiState = DashboardUiState(
        searchQuery = searchQuery,
        selectedTab = selectedTab
    )

    HomeContent(
        uiState = uiState,
        onSearchQueryChange = { searchQuery = it },
        onTabSelected = { tab ->
            if (tab == 0) selectedTab = tab
            onTabSelected(tab)
        },
        onProjectClick = onNavigateToProjectDetails,
        onViewAllProjectsClick = onNavigateToViewAllProjects,
        onCreateTaskClick = onNavigateToCreateTask,
        onNotificationsClick = onNavigateToNotifications,
        onMessagesClick = onNavigateToMessages,
        onReviewTasksClick = onNavigateToReviewTasks,
        modifier = modifier,
        onOpenSearch = onOpenSearch
    )
}

@Composable
fun HomeContent(
    uiState: DashboardUiState,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onProjectClick: (String) -> Unit,
    onViewAllProjectsClick: () -> Unit,
    onCreateTaskClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onReviewTasksClick: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSearch: (() -> Unit)? = null
) {
    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            NexaBottomNavigationBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected,
                onCreateTaskClick = onCreateTaskClick
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DashboardHeader(
                uiState = uiState,
                onNotificationsClick = onNotificationsClick,
                onMessagesClick = onMessagesClick
            )

            Surface(
                color = LightBodyBg,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = {
                                Text(
                                    text = "Search projects, tasks or files...",
                                    color = PlaceholderGray,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                val iconModifier = if (onOpenSearch != null) {
                                    Modifier.size(20.dp).clickable(onClick = onOpenSearch)
                                } else {
                                    Modifier.size(20.dp)
                                }
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = PlaceholderGray,
                                    modifier = iconModifier
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = SoftBorder,
                                unfocusedBorderColor = Color(0xFFF0F2F8),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PrimaryPurple)
                                .clickable { onViewAllProjectsClick() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Grid View",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            count = uiState.activeProjectsCount.toString(),
                            label = "Active Projects",
                            accentColor = PrimaryPurple,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            count = uiState.myTasksCount.toString(),
                            label = "My Tasks",
                            accentColor = AccentCyan,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            count = uiState.dueSoonCount.toString(),
                            label = "Due Soon",
                            accentColor = DueSoonOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Projects",
                            color = SlateTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = onViewAllProjectsClick) {
                            Text(
                                text = "View all ›",
                                color = PrimaryPurple,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.projects.forEach { project ->
                        ProjectCard(
                            project = project,
                            onClick = { onProjectClick(project.id) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        onClick = onReviewTasksClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "⚡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "${uiState.dueTodayTasksCount} tasks due today — tap to review",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Review Tasks",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    uiState: DashboardUiState,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.nexa_logo),
                contentDescription = "NEXA Logo",
                modifier = Modifier.height(42.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(42.dp)
                            .background(DarkSurface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFFF5252), CircleShape)
                            .border(1.5.dp, DarkBackground, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }

                IconButton(
                    onClick = onMessagesClick,
                    modifier = Modifier
                        .size(42.dp)
                        .background(DarkSurface, CircleShape)
                ) {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        val w = size.width
                        val h = size.height
                        drawRoundRect(
                            color = Color.LightGray,
                            topLeft = Offset.Zero,
                            size = Size(w, h * 0.82f),
                            cornerRadius = CornerRadius(w * 0.35f, h * 0.35f),
                            style = Stroke(width = 1.6.dp.toPx())
                        )
                        val dotRadius = 1.2.dp.toPx()
                        listOf(0.30f, 0.50f, 0.70f).forEach { xRatio ->
                            drawCircle(
                                color = Color.LightGray,
                                radius = dotRadius,
                                center = Offset(w * xRatio, h * 0.41f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatarImage()
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Good morning 👋", color = Color.LightGray, fontSize = 13.sp)
                    Text(
                        text = uiState.userName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = uiState.userRole, color = TextMuted, fontSize = 13.sp)
                }
            }

            Box(
                modifier = Modifier
                    .background(DarkSurface, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = uiState.dateString,
                    color = Color(0xFFA8B1D0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MetricCard(
    count: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.height(100.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accentColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = count,
                    color = accentColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    color = SlateMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: ProjectItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(96.dp)
                    .background(project.accentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .background(project.statusBgColor, RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = project.icon,
                                contentDescription = project.title,
                                tint = SlateTitle,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = project.title,
                                color = SlateDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = project.category,
                                color = SlateMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(project.statusBgColor, RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(project.statusTextColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = project.statusText,
                                color = project.statusTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { project.progress },
                        color = project.accentColor,
                        trackColor = SoftBorder,
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${(project.progress * 100).toInt()}%",
                        color = project.accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileAvatarImage(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(54.dp)
            .border(2.dp, AccentCyan, CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
            .background(DarkSurface)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val hairColor = Color(0xFF1C1412)

            drawCircle(color = Color(0xFF262A56))

            drawPath(
                path = Path().apply {
                    moveTo(w * 0.10f, h)
                    lineTo(w * 0.26f, h * 0.64f)
                    lineTo(w * 0.74f, h * 0.64f)
                    lineTo(w * 0.90f, h)
                    close()
                },
                color = Color(0xFF121528)
            )

            drawPath(
                path = Path().apply {
                    moveTo(w * 0.42f, h * 0.64f)
                    lineTo(w * 0.50f, h * 0.82f)
                    lineTo(w * 0.58f, h * 0.64f)
                    close()
                },
                color = Color.White
            )

            drawRect(
                color = Color(0xFFDCA988),
                topLeft = Offset(w * 0.44f, h * 0.48f),
                size = Size(w * 0.12f, h * 0.18f)
            )

            drawCircle(color = hairColor, radius = w * 0.34f, center = Offset(w * 0.50f, h * 0.36f))
            drawOval(
                color = Color(0xFFE8B695),
                topLeft = Offset(w * 0.32f, h * 0.22f),
                size = Size(w * 0.36f, h * 0.38f)
            )

            drawCircle(color = hairColor, radius = w * 0.12f, center = Offset(w * 0.26f, h * 0.30f))
            drawCircle(color = hairColor, radius = w * 0.12f, center = Offset(w * 0.74f, h * 0.30f))
            drawCircle(color = hairColor, radius = w * 0.13f, center = Offset(w * 0.30f, h * 0.42f))
            drawCircle(color = hairColor, radius = w * 0.13f, center = Offset(w * 0.70f, h * 0.42f))
            drawOval(
                color = hairColor,
                topLeft = Offset(w * 0.30f, h * 0.14f),
                size = Size(w * 0.40f, h * 0.16f)
            )

            drawCircle(color = hairColor, radius = w * 0.025f, center = Offset(w * 0.42f, h * 0.38f))
            drawCircle(color = hairColor, radius = w * 0.025f, center = Offset(w * 0.58f, h * 0.38f))

            drawArc(
                color = Color(0xFF8C4C3E),
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(w * 0.43f, h * 0.44f),
                size = Size(w * 0.14f, h * 0.09f),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
fun NexaBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onCreateTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            BottomNavItem(
                icon = Icons.Outlined.Check,
                label = "Tasks",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PrimaryPurple)
                    .clickable(onClick = onCreateTaskClick)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "New",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            BottomNavItem(
                icon = Icons.Outlined.CropSquare,
                label = "Calendar",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
            BottomNavItem(
                icon = Icons.Outlined.Menu,
                label = "More",
                isSelected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SoftPurpleBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) PrimaryPurple else Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isSelected) PrimaryPurple else Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Project_M3Theme {
        HomeScreen()
    }
}

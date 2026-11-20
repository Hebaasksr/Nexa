package com.nexa.app
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkSurface = Color(0xFF191C3B)
private val LightBodyBg = Color(0xFFF7F8FC)
private val SlateDark = Color(0xFF0F172A)
private val SlateBody = Color(0xFF334155)
private val SlateMuted = Color(0xFF64748B)
private val SoftPurpleBg = Color(0xFFF1EBFF)

enum class NotificationIconType {
    TASK_ASSIGNED,
    MENTION,
    DEADLINE,
    FILE_UPDATED
}

data class NotificationItem(
    val id: String,
    val title: String,
    val bodyText: String,
    val metadataText: String,
    val iconType: NotificationIconType,
    val isNew: Boolean = false
)

data class NotificationsUiState(
    val unreadCount: Int = 3,
    val workspaceName: String = "Product workspace",
    val todayNotifications: List<NotificationItem> = listOf(
        NotificationItem(
            id = "1",
            title = "Task assigned",
            bodyText = "Marwah assigned Develop login functionality to Farah.",
            metadataText = "Website Development · 8:30 AM ›",
            iconType = NotificationIconType.TASK_ASSIGNED,
            isNew = true
        ),
        NotificationItem(
            id = "2",
            title = "Sara mentioned you",
            bodyText = "“@Munirah, can we review accessibility before prototype handoff?”",
            metadataText = "Mobile App Redesign · 9:35 AM ›",
            iconType = NotificationIconType.MENTION,
            isNew = true
        ),
        NotificationItem(
            id = "3",
            title = "Deadline today",
            bodyText = "Develop login functionality is due Sep 17. Status: In Progress.",
            metadataText = "Website Development · 8:00 AM ›",
            iconType = NotificationIconType.DEADLINE,
            isNew = true
        )
    ),
    val earlierNotifications: List<NotificationItem> = listOf(
        NotificationItem(
            id = "4",
            title = "File updated",
            bodyText = "Heba updated Design Assets: 12 files, 245 MB.",
            metadataText = "Mobile App Redesign · Sep 14 ›",
            iconType = NotificationIconType.FILE_UPDATED,
            isNew = false
        )
    ),
    val selectedTab: Int = 4
)

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNotificationClick: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onCreateTaskClick: () -> Unit = {}
) {
    val initialState = remember { NotificationsUiState() }
    var todayNotifications by remember { mutableStateOf(initialState.todayNotifications) }
    var earlierNotifications by remember { mutableStateOf(initialState.earlierNotifications) }
    var currentTab by rememberSaveable { mutableIntStateOf(4) }

    val unreadCount = todayNotifications.count { it.isNew } + earlierNotifications.count { it.isNew }

    val uiState = NotificationsUiState(
        unreadCount = unreadCount,
        todayNotifications = todayNotifications,
        earlierNotifications = earlierNotifications,
        selectedTab = currentTab
    )

    NotificationsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onSettingsClick = onSettingsClick,
        onMarkAllAsReadClick = {
            todayNotifications = todayNotifications.map { it.copy(isNew = false) }
            earlierNotifications = earlierNotifications.map { it.copy(isNew = false) }
        },
        onNotificationClick = onNotificationClick,
        onTabSelected = {
            currentTab = it
            onTabSelected(it)
        },
        onCreateTaskClick = onCreateTaskClick,
        modifier = modifier
    )
}

@Composable
fun NotificationsContent(
    uiState: NotificationsUiState,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMarkAllAsReadClick: () -> Unit,
    onNotificationClick: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onCreateTaskClick: () -> Unit,
    modifier: Modifier = Modifier
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
            NotificationsHeader(
                unreadCount = uiState.unreadCount,
                workspaceName = uiState.workspaceName,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today",
                            color = SlateDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = onMarkAllAsReadClick) {
                            Text(
                                text = "Mark all as read",
                                color = PrimaryPurple,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.todayNotifications.forEach { item ->
                        NotificationCard(
                            item = item,
                            onClick = { onNotificationClick(item.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Earlier",
                        color = SlateDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    uiState.earlierNotifications.forEach { item ->
                        NotificationCard(
                            item = item,
                            onClick = { onNotificationClick(item.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun NotificationsHeader(
    unreadCount: Int,
    workspaceName: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
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
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(42.dp)
                    .background(DarkSurface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            TextButton(onClick = onSettingsClick) {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Notifications",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$unreadCount unread · $workspaceName",
            color = TextMuted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (item.iconType) {
        NotificationIconType.TASK_ASSIGNED -> Icons.Default.CheckCircleOutline
        NotificationIconType.MENTION -> Icons.Default.AlternateEmail
        NotificationIconType.DEADLINE -> Icons.Default.AccessTime
        NotificationIconType.FILE_UPDATED -> Icons.AutoMirrored.Filled.InsertDriveFile
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = item.title,
                        color = SlateDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (item.isNew) {
                    Box(
                        modifier = Modifier
                            .background(SoftPurpleBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "New",
                            color = PrimaryPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.bodyText,
                color = SlateBody,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.metadataText,
                color = SlateMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    Project_M3Theme {
        NotificationsScreen()
    }
}
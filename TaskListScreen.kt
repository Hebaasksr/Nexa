package com.nexa.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val taskDate: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)

@Composable
internal fun TaskPage(
    title: String,
    subtitle: String,
    onTabSelected: (Int) -> Unit,
    onNew: () -> Unit,
    onBack: (() -> Unit)? = null,
    action: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Sheet)
            .imePadding(),
    ) {
        NexaHeader(title, subtitle, onBack, action)

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            content = content,
        )

        NexaBottomNavigationBar(1, onTabSelected, onNew)
    }
}

@Composable
internal fun MyTasksScreen(
    tasks: List<NexaTask>,
    onAdd: () -> Unit,
    onDetails: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onToggle: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onNew: () -> Unit,
    onNotifications: () -> Unit = {},
    onMessages: () -> Unit = {},
    onBack: (() -> Unit)? = null,
) {
    var filter by rememberSaveable { mutableIntStateOf(0) }
    var sort by rememberSaveable { mutableStateOf(false) }

    val today = LocalDate.now()
    val inProgress = tasks.count { it.status == TaskStatus.Progress }
    val dueToday = tasks.count {
        it.due == today && it.status != TaskStatus.Done
    }

    val filtered = tasks.filter {
        filter == 0 || it.status.ordinal == filter - 1
    }

    val visible = if (sort) {
        filtered.sortedBy { it.due }
    } else {
        filtered
    }

    TaskPage(
        title = "My Tasks",
        subtitle = "${tasks.size} tasks · $inProgress in progress · $dueToday due today",
        onTabSelected = onTabSelected,
        onNew = onNew,
        onBack = onBack,
        action = {
            TextButton(
                onClick = onNotifications,
                contentPadding = PaddingValues(8.dp),
            ) {
                Text("🔔", color = Color.White)
            }

            TextButton(
                onClick = onMessages,
                contentPadding = PaddingValues(8.dp),
            ) {
                Text("💬", color = Color.White)
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Surface(
                    color = Panel,
                    shape = RoundedCornerShape(28.dp),
                    shadowElevation = 2.dp,
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                    ) {
                        listOf("All", "To Do", "In Progress", "Done")
                            .forEachIndexed { index, label ->
                                Surface(
                                    onClick = { filter = index },
                                    modifier = Modifier.weight(1f),
                                    color = if (filter == index) {
                                        PrimaryPurple
                                    } else {
                                        Panel
                                    },
                                    shape = RoundedCornerShape(24.dp),
                                ) {
                                    Box(
                                        Modifier.heightIn(min = 40.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (filter == index) {
                                                Color.White
                                            } else {
                                                Muted
                                            },
                                            style = MaterialTheme.typography.labelMedium,
                                        )
                                    }
                                }
                            }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (filter == 0) {
                            "All Tasks"
                        } else {
                            listOf("", "To Do", "In Progress", "Done")[filter]
                        },
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                    )

                    Button(
                        onClick = onAdd,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 6.dp,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Add task")
                    }

                    TextButton(onClick = { sort = !sort }) {
                        Text(if (sort) "By due date" else "Sort ↓")
                    }
                }
            }

            if (visible.isEmpty()) {
                item {
                    Text("No tasks in this list", color = Muted)
                }
            }

            items(visible, key = { it.id }) { task ->
                TaskListCard(
                    task = task,
                    today = today,
                    onOpen = { onDetails(task.id) },
                    onEdit = { onEdit(task.id) },
                    onDelete = { onDelete(task.id) },
                    onToggle = { onToggle(task.id) },
                )
            }
        }
    }
}

@Composable
private fun TaskListCard(
    task: NexaTask,
    today: LocalDate,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: () -> Unit,
) {
    val accent = when {
        task.status == TaskStatus.Done -> Color(0xFF9CA6B4)
        task.category in listOf("Frontend", "Research") -> Color(0xFF00B9CF)
        else -> PrimaryPurple
    }

    Surface(
        onClick = onOpen,
        shape = CardShape,
        color = Panel,
        shadowElevation = 2.dp,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accent),
            )

            Checkbox(
                checked = task.status == TaskStatus.Done,
                onCheckedChange = { onToggle() },
            )

            Column(
                Modifier
                    .weight(1f)
                    .padding(top = 12.dp, bottom = 12.dp),
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    color = if (task.status == TaskStatus.Done) Muted else Navy,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 5.dp),
                ) {
                    TaskBadge(
                        label = task.category,
                        background = Color(0xFFF0E9FF),
                        foreground = Color(0xFF582BD2),
                    )

                    if (task.priority.isNotBlank()) {
                        PriorityBadge(task.priority)
                    }
                }

                Text(
                    text = if (task.due == today && task.status != TaskStatus.Done) {
                        "Due today · ${task.due.format(taskDate)}"
                    } else {
                        task.due.format(taskDate)
                    },
                    color = if (task.due == today && task.status != TaskStatus.Done) {
                        Color(0xFF974600)
                    } else {
                        Muted
                    },
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(top = 10.dp, end = 8.dp),
            ) {
                StatusBadge(task.status)

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp, top = 4.dp)
                        .size(18.dp),
                    tint = Muted,
                )

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ${task.title}",
                            modifier = Modifier.size(18.dp),
                            tint = Muted,
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete ${task.title}",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFFC9003D),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun TaskBadge(
    label: String,
    background: Color,
    foreground: Color,
) {
    Surface(
        color = background,
        contentColor = foreground,
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun StatusBadge(status: TaskStatus) {
    when (status) {
        TaskStatus.Todo -> {
            TaskBadge(status.title, Sheet, Muted)
        }

        TaskStatus.Progress -> {
            TaskBadge(
                status.title,
                Color(0xFFDDF8FA),
                Color(0xFF006C79),
            )
        }

        TaskStatus.Done -> {
            TaskBadge(
                status.title,
                Color(0xFFE4FAEF),
                Color(0xFF00693F),
            )
        }
    }
}

@Composable
internal fun PriorityBadge(priority: String) {
    when (priority.substringBefore(" ")) {
        "High" -> {
            TaskBadge(
                priority,
                Color(0xFFFFE8E9),
                Color(0xFF9D2616),
            )
        }

        "Low" -> {
            TaskBadge(
                priority,
                Color(0xFFE4FAEF),
                Color(0xFF00693F),
            )
        }

        else -> {
            TaskBadge(
                priority,
                Color(0xFFFFEDD5),
                Color(0xFF8A4000),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewItemSheet(
    onDismiss: () -> Unit,
    onTask: () -> Unit,
    onProject: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Panel,
    ) {
        Text(
            text = "New",
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 8.dp,
            ),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        ListItem(
            modifier = Modifier.clickable(onClick = onProject),
            headlineContent = {
                Text("New Project", color = Muted)
            },
            supportingContent = {
                Text("Create a shared workspace")
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Muted,
                )
            },
        )

        ListItem(
            modifier = Modifier.clickable(onClick = onTask),
            headlineContent = {
                Text("New Task")
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PrimaryPurple,
                )
            },
        )

        Spacer(Modifier.height(16.dp))
    }
}
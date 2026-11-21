package com.nexa.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
internal fun TaskDetailsScreen(
    task: NexaTask,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: () -> Unit,
    onToggleCheck: (Int) -> Unit,
    onComment: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onNew: () -> Unit,
    member: TeamMember? = null,
) {
    var draft by rememberSaveable(task.id) {
        mutableStateOf("")
    }

    TaskPage(
        title = "Task details",
        subtitle = "${task.project} · ${task.category}",
        onTabSelected = onTabSelected,
        onNew = onNew,
        onBack = onBack,
        action = {
            TextButton(onClick = onEdit) {
                Text("Edit", color = Color.White)
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Delete task",
                    tint = Color.White,
                )
            }
        },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatusBadge(task.status)

                    if (task.priority.isNotBlank()) {
                        PriorityBadge("${task.priority} priority")
                    }

                    if (
                        task.due == LocalDate.now() &&
                        task.status != TaskStatus.Done
                    ) {
                        TaskBadge(
                            "Due today",
                            Color(0xFFFFEDD5),
                            Color(0xFF8A4000),
                        )
                    }
                }
            }

            item {
                NexaCard {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (task.assignee.isNotBlank()) {
                                Avatar(
                                    initialsFor(task.assignee),
                                    Color(0xFFF0E5FF),
                                    Modifier.size(40.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                            }

                            Column {
                                Text(
                                    task.assignee.ifBlank { "Unassigned" },
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    member?.let {
                                        "Assignee · ${it.role} · ${it.status}"
                                    } ?: "Assignee",
                                    color = Muted,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }

                        Text(
                            "Due ${task.due.format(taskDate)}",
                            color = Muted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            item {
                NexaCard {
                    Column(Modifier.padding(12.dp)) {
                        Text("Description", fontWeight = FontWeight.Bold)
                        Text(
                            task.description.ifBlank { "No description" },
                            Modifier.padding(top = 4.dp),
                            color = Muted,
                        )
                    }
                }
            }

            // Checklist box stays visible even when the task has no checklist items.
            item {
                NexaCard {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            "Acceptance checklist · " +
                                    "${task.checklist.count { it.checked }} " +
                                    "of ${task.checklist.size}",
                            fontWeight = FontWeight.Bold,
                        )

                        if (task.checklist.isEmpty()) {
                            Text(
                                "No checklist items yet",
                                color = Muted,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        } else {
                            task.checklist.forEachIndexed { index, check ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onToggleCheck(index)
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Checkbox(
                                        checked = check.checked,
                                        onCheckedChange = null,
                                        modifier = Modifier.padding(end = 8.dp),
                                    )
                                    Text(
                                        check.title,
                                        Modifier.padding(vertical = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Activity & comments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Show an empty comment box when there are no comments yet.
            item {
                if (task.comments.isEmpty()) {
                    NexaCard {
                        Text(
                            "No comments yet",
                            Modifier.padding(12.dp),
                            color = Muted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            items(task.comments) { comment ->
                NexaCard {
                    Text(
                        comment,
                        Modifier.padding(12.dp),
                        color = Muted,
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Add a comment…") },
                    shape = CardShape,
                    colors = taskFieldColors(),
                    trailingIcon = {
                        IconButton(
                            enabled = draft.isNotBlank(),
                            onClick = {
                                onComment(draft)
                                draft = ""
                            },
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send comment",
                            )
                        }
                    },
                )
            }

            item {
                Button(
                    onClick = onToggleStatus,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                    ),
                ) {
                    Text(
                        if (task.status == TaskStatus.Done) {
                            "Reopen task"
                        } else {
                            "✓ Mark complete"
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun DeleteTaskDialog(
    task: NexaTask,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Panel,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    color = Color(0xFFFFEDF2),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = Color(0xFFC9003D),
                    )
                }

                Text(
                    "Delete this task?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    task.title,
                    fontWeight = FontWeight.SemiBold,
                    color = Navy,
                )

                Text(
                    "${task.project} · Due ${task.due.format(taskDate)}",
                    color = Muted,
                )

                Text(
                    "This removes the task, its checklist and comments. " +
                            "This can’t be undone.",
                    color = Muted,
                )

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC9003D),
                    ),
                ) {
                    Text("Delete task")
                }
            }
        }
    }
}
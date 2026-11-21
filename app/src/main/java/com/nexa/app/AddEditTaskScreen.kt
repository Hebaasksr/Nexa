package com.nexa.app


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditTaskScreen(
    task: NexaTask?,
    projects: List<String>,
    assignees: List<String>,
    onSave: (TaskFields) -> Boolean,
    onBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onNew: () -> Unit,
) {
    var project by rememberSaveable(task?.id) {
        mutableStateOf(task?.project.orEmpty())
    }
    var title by rememberSaveable(task?.id) {
        mutableStateOf(task?.title.orEmpty())
    }
    var description by rememberSaveable(task?.id) {
        mutableStateOf(task?.description.orEmpty())
    }
    var assignee by rememberSaveable(task?.id) {
        mutableStateOf(task?.assignee.orEmpty())
    }
    var due by rememberSaveable(task?.id) {
        mutableStateOf(task?.due?.toString().orEmpty())
    }
    var priority by rememberSaveable(task?.id) {
        mutableStateOf(task?.priority.orEmpty())
    }
    var submitted by rememberSaveable {
        mutableStateOf(false)
    }
    var saveFailed by rememberSaveable {
        mutableStateOf(false)
    }
    var choosingDate by rememberSaveable {
        mutableStateOf(false)
    }

    TaskPage(
        title = if (task == null) "New task" else "Edit task",
        subtitle = if (task == null) {
            "Turn the next step into a clear assignment"
        } else {
            "Update the task details"
        },
        onTabSelected = onTabSelected,
        onNew = onNew,
        onBack = onBack,
        action = {
            TextButton(onClick = onBack) {
                Text("Cancel", color = Color.White)
            }
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Required fields are marked with *",
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
            )

            TaskChoice(
                label = "Project *",
                value = project,
                placeholder = "Select project",
                options = projects,
                onSelect = { project = it },
                error = submitted && project.isBlank(),
            )

            TaskTextField(
                label = "Task title *",
                value = title,
                placeholder = "Enter task title",
                onChange = { title = it },
                error = submitted && title.isBlank(),
            )

            TaskTextField(
                label = "Description",
                value = description,
                placeholder = "Add a description",
                onChange = { description = it },
                lines = 2,
            )

            TaskChoice(
                label = "Assignee",
                value = assignee,
                placeholder = "Select assignee",
                options = listOf("") + assignees,
                onSelect = { assignee = it },
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Due date *",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Spacer(Modifier.height(4.dp))

                    TaskPickerButton(
                        label = if (due.isBlank()) {
                            "Select date"
                        } else {
                            LocalDate.parse(due).format(taskDate)
                        },
                        placeholder = due.isBlank(),
                        error = submitted && due.isBlank(),
                        onClick = { choosingDate = true },
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Muted,
                        )
                    }
                }

                TaskChoice(
                    label = "Priority",
                    value = priority,
                    placeholder = "Select priority",
                    options = listOf("", "Low", "Medium", "High"),
                    onSelect = { priority = it },
                    modifier = Modifier.weight(1f),
                )
            }

            if (task == null) {
                Text(
                    "The task will start as To Do.",
                    color = Muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (submitted &&
                (project.isBlank() || title.isBlank() || due.isBlank())
            ) {
                Text(
                    "Select a project, enter a task title and choose a due date.",
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (saveFailed) {
                Text(
                    "This task could not be saved.",
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    submitted = true

                    if (project.isNotBlank() &&
                        title.isNotBlank() &&
                        due.isNotBlank()
                    ) {
                        saveFailed = !onSave(
                            TaskFields(
                                project = project,
                                title = title,
                                description = description,
                                assignee = assignee,
                                due = due,
                                priority = priority,
                            ),
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                ),
            ) {
                Text(
                    if (task == null) "Create task" else "Save changes",
                    fontWeight = FontWeight.Bold,
                )
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) {
                Text("Cancel")
            }
        }
    }

    if (choosingDate) {
        val selection = rememberDatePickerState(
            initialSelectedDateMillis = due
                .takeIf { it.isNotBlank() }
                ?.let {
                    LocalDate.parse(it)
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli()
                },
        )

        DatePickerDialog(
            onDismissRequest = { choosingDate = false },
            confirmButton = {
                TextButton(
                    enabled = selection.selectedDateMillis != null,
                    onClick = {
                        selection.selectedDateMillis?.let { millis ->
                            due = Instant.ofEpochMilli(millis)
                                .atOffset(ZoneOffset.UTC)
                                .toLocalDate()
                                .toString()
                        }
                        choosingDate = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { choosingDate = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(selection, showModeToggle = false)
        }
    }
}

@Composable
private fun TaskTextField(
    label: String,
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    lines: Int = 1,
    error: Boolean = false,
) {
    Text(
        label,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.labelMedium,
    )

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = lines == 1,
        minLines = lines,
        shape = CardShape,
        colors = taskFieldColors(),
        isError = error,
    )
}

@Composable
internal fun taskFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Panel,
    unfocusedContainerColor = Panel,
    errorContainerColor = Panel,
    unfocusedBorderColor = Outline,
)

@Composable
private fun TaskChoice(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: Boolean = false,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(modifier) {
        Text(
            label,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(Modifier.height(4.dp))

        Box {
            TaskPickerButton(
                label = value.ifBlank { placeholder },
                placeholder = value.isBlank(),
                error = error,
                onClick = { expanded = true },
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Muted,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.ifBlank { "None" }) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskPickerButton(
    label: String,
    placeholder: Boolean,
    error: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        color = Panel,
        border = BorderStroke(
            1.dp,
            if (error) MaterialTheme.colorScheme.error else Outline,
        ),
    ) {
        Row(
            Modifier
                .heightIn(min = 48.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                Modifier.weight(1f),
                color = if (placeholder) Muted else Navy,
                style = MaterialTheme.typography.bodyMedium,
            )
            icon()
        }
    }
}

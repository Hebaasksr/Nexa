package com.nexa.app

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class NFile(
    val name: String,
    val meta: String,
    val type: String,
)

@Composable
fun FilesScreen(
    onBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onNew: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("All") }

    val folders = listOf(
        NFile("Design Assets", "12 files · 245 MB", "Design · Figma"),
        NFile("Documentation", "8 files · 120 MB", "Docs · Word"),
        NFile("Presentations", "5 files · 98 MB", "Slides · PPT"),
    )

    val recent = listOf(
        NFile("Project_Proposal.pdf", "2.4 MB · Aug 20, 2026 · Heba A.", "PDF"),
        NFile("UI_Mockups.fig", "4.8 MB · Aug 22, 2026 · Heba A.", "FIG"),
        NFile("Meeting Notes.docx", "1.2 MB · Aug 25, 2026 · Haneen A.", "DOC"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackground)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(42.dp)
                        .background(Color(0xFF191C3B), CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "NEXA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(2.dp)
                            .background(Color(0xFF00D5E8))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Project Files",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Mobile App Redesign · 25 files · 468 MB",
                color = TextMuted,
                fontSize = 14.sp,
            )
        }

        // Main content card
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = Color(0xFFF7F8FC),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search files and folders…") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Muted,
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = Purple,
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Panel,
                        focusedContainerColor = Panel,
                    ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf("All", "Documents", "Images", "Videos", "Others")
                        .forEach { option ->
                            FilterChip(
                                selected = filter == option,
                                onClick = { filter = option },
                                label = { Text(option) },
                            )
                        }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Text("Folders", fontWeight = FontWeight.Bold)
                    }

                    items(
                        folders.filter {
                            it.name.contains(query, ignoreCase = true) || query.isBlank()
                        }
                    ) { file ->
                        FileCard(file, folder = true)
                    }

                    item {
                        Text(
                            "Recent Files",
                            modifier = Modifier.padding(top = 8.dp),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    items(
                        recent.filter {
                            it.name.contains(query, ignoreCase = true) || query.isBlank()
                        }
                    ) { file ->
                        FileCard(file, folder = false)
                    }
                }
            }
        }

        NexaBottomNavigationBar(
            selectedTab = 4,
            onTabSelected = onTabSelected,
            onCreateTaskClick = onNew,
        )
    }
}

@Composable
private fun FileCard(file: NFile, folder: Boolean) {
    NexaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = if (folder) Color(0xFFFFF0E6) else Color(0xFFECE4FF),
                shape = CircleShape,
            ) {
                Text(
                    text = if (folder) "📁" else file.type,
                    modifier = Modifier.padding(10.dp),
                    color = Purple,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, fontWeight = FontWeight.Bold)
                Text(
                    file.meta,
                    color = Muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text("›", color = Muted, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 850)
@Composable
private fun FilesScreenPreview() {
    NexaTheme {
        FilesScreen(
            onBack = {},
            onTabSelected = {},
            onNew = {},
        )
    }
}
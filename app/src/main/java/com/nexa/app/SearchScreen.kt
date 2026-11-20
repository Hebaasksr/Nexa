package com.nexa.app
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkSurface = Color(0xFF191C3B)
private val LightBodyBg = Color(0xFFF7F8FC)
private val SlateTitle = Color(0xFF1E293B)
private val SlateDark = Color(0xFF0F172A)
private val SlateMuted = Color(0xFF64748B)
private val BorderFocused = Color(0xFFCBD5E1)
private val BorderUnfocused = Color(0xFFE2E8F0)

private val SearchCategories = listOf("All", "Projects", "Tasks", "Files")

data class SearchTaskResult(
    val id: String,
    val titlePrefix: String,
    val highlightedWord: String,
    val titleSuffix: String,
    val projectAndAssignee: String,
    val statusText: String,
    val statusBgColor: Color,
    val statusTextColor: Color,
    val dueDateText: String,
    val dueDateBgColor: Color,
    val dueDateTextColor: Color
)

data class SearchFileResult(
    val id: String,
    val fileName: String,
    val metadataAndMatch: String
)

data class SearchUiState(
    val searchQuery: String = "login",
    val selectedCategory: String = "All",
    val resultsCountText: String = "2 results for “login”",
    val tasksCount: Int = 1,
    val taskResults: List<SearchTaskResult> = listOf(
        SearchTaskResult(
            id = "1",
            titlePrefix = "Develop ",
            highlightedWord = "login",
            titleSuffix = " functionality",
            projectAndAssignee = "Website Development · Haneen Alotaibi",
            statusText = "In Progress",
            statusBgColor = Color(0xFFE5F7FF),
            statusTextColor = Color(0xFF0096C7),
            dueDateText = "Due Sep 17",
            dueDateBgColor = Color(0xFFFFF3E0),
            dueDateTextColor = Color(0xFFE65100)
        )
    ),
    val filesCount: Int = 1,
    val fileResults: List<SearchFileResult> = listOf(
        SearchFileResult(
            id = "1",
            fileName = "UI_Mockups.fig",
            metadataAndMatch = "Mobile App Redesign · 4.8 MB · Aug 22 Match in page: “Login / Error states”"
        )
    ),
    val projectsCount: Int = 0,
    val projectsEmptyText: String = "No project names match “login”. Try a project name or choose another category.",
    val selectedTab: Int = 0
)

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onNavigateToTaskDetails: (String) -> Unit = {},
    onNavigateToFileDetails: (String) -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onCreateTaskClick: () -> Unit = {}
) {
    var searchQuery by rememberSaveable { mutableStateOf("login") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    val matchCount = if (searchQuery.equals("login", ignoreCase = true)) 2 else 0
    val resultsSummary = if (searchQuery.isNotBlank()) {
        "$matchCount results for “$searchQuery”"
    } else {
        ""
    }

    val uiState = SearchUiState(
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        resultsCountText = resultsSummary,
        selectedTab = currentTab
    )

    SearchContent(
        uiState = uiState,
        onSearchQueryChange = { searchQuery = it },
        onClearQueryClick = { searchQuery = "" },
        onCategorySelected = { selectedCategory = it },
        onBackClick = onBackClick,
        onCancelClick = onCancelClick,
        onTaskClick = onNavigateToTaskDetails,
        onFileClick = onNavigateToFileDetails,
        onTabSelected = {
            currentTab = it
            onTabSelected(it)
        },
        onCreateTaskClick = onCreateTaskClick,
        modifier = modifier
    )
}

@Composable
fun SearchContent(
    uiState: SearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onClearQueryClick: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onFileClick: (String) -> Unit,
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
            SearchHeader(
                onBackClick = onBackClick,
                onCancelClick = onCancelClick
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
                    Text(
                        text = "Global search",
                        color = SlateTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = onClearQueryClick) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = SlateMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BorderFocused,
                            unfocusedBorderColor = BorderUnfocused,
                            focusedTextColor = SlateDark,
                            unfocusedTextColor = SlateDark
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SearchCategoryTabs(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = onCategorySelected
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (uiState.resultsCountText.isNotEmpty()) {
                        Text(
                            text = uiState.resultsCountText,
                            color = SlateMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    if (uiState.selectedCategory == "All" || uiState.selectedCategory == "Tasks") {
                        SectionHeading(title = "Tasks · ${uiState.tasksCount}")
                        Spacer(modifier = Modifier.height(12.dp))
                        uiState.taskResults.forEach { task ->
                            TaskResultCard(
                                task = task,
                                onClick = { onTaskClick(task.id) }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    if (uiState.selectedCategory == "All" || uiState.selectedCategory == "Files") {
                        SectionHeading(title = "Files · ${uiState.filesCount}")
                        Spacer(modifier = Modifier.height(12.dp))
                        uiState.fileResults.forEach { file ->
                            FileResultCard(
                                file = file,
                                onClick = { onFileClick(file.id) }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    if (uiState.selectedCategory == "All" || uiState.selectedCategory == "Projects") {
                        SectionHeading(title = "Projects · ${uiState.projectsCount}")
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.projectsEmptyText,
                            color = SlateMuted,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit
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

            TextButton(onClick = onCancelClick) {
                Text(
                    text = "Cancel",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Search",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Search across your Product workspace",
            color = TextMuted,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun SearchCategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SearchCategories.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PrimaryPurple else Color.Transparent)
                        .clickable { onCategorySelected(category) }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else SlateMuted,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String) {
    Text(
        text = title,
        color = SlateDark,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TaskResultCard(
    task: SearchTaskResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
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
            val annotatedTitle = buildAnnotatedString {
                append(task.titlePrefix)
                withStyle(SpanStyle(color = PrimaryPurple, fontWeight = FontWeight.Bold)) {
                    append(task.highlightedWord)
                }
                append(task.titleSuffix)
            }

            Text(
                text = annotatedTitle,
                color = SlateDark,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.projectAndAssignee,
                color = SlateMuted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResultBadge(
                    text = task.statusText,
                    bgColor = task.statusBgColor,
                    textColor = task.statusTextColor
                )
                ResultBadge(
                    text = task.dueDateText,
                    bgColor = task.dueDateBgColor,
                    textColor = task.dueDateTextColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "View task details ›",
                color = PrimaryPurple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
    }
}

@Composable
private fun ResultBadge(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FileResultCard(
    file: SearchFileResult,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = file.fileName,
                color = SlateDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = file.metadataAndMatch,
                color = SlateMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    Project_M3Theme {
        SearchScreen()
    }
}
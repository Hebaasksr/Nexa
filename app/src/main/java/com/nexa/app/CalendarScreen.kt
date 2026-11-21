package com.nexa.app

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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkSurface = Color(0xFF191C3B)
private val LightBodyBg = Color(0xFFF7F8FC)
private val SlateDark = Color(0xFF0F172A)
private val SlateTitle = Color(0xFF1E293B)
private val SlateMuted = Color(0xFF64748B)
private val DividerGray = Color(0xFFE2E8F0)
private val InactivePillText = Color(0xFFA8B1D0)

private val CalendarViewModes = listOf("Month", "Week", "Day")
private val DaysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

private val EventDotsByDay = mapOf(
    3 to Color(0xFF00E5FF),
    11 to Color(0xFF6C47FF),
    17 to Color(0xFF00E5FF),
    24 to Color(0xFFFF6B6B)
)

private fun buildSeptemberDays(selectedDay: Int): List<CalendarDayModel?> {
    // Sep 1, 2026 starts on Tuesday (2 leading blanks for Sun & Mon)
    val leadingBlanks = listOf<CalendarDayModel?>(null, null)
    val days = (1..30).map { day ->
        CalendarDayModel(
            dayNumber = day,
            isSelected = day == selectedDay,
            dotColor = EventDotsByDay[day]
        )
    }
    return leadingBlanks + days
}

data class CalendarEventItem(
    val id: String,
    val title: String,
    val timeRange: String,
    val projectTitle: String,
    val projectBgColor: Color,
    val projectTextColor: Color,
    val durationText: String,
    val durationBgColor: Color,
    val durationTextColor: Color,
    val iconEmoji: String,
    val accentColor: Color
)

data class CalendarDayModel(
    val dayNumber: Int,
    val isSelected: Boolean = false,
    val dotColor: Color? = null
)

data class CalendarUiState(
    val monthYearString: String = "September 2026",
    val selectedViewMode: String = "Month",
    val selectedDay: Int = 17,
    val selectedDateHeader: String = "Today, Sep 17, 2026",
    val eventsCountText: String = "2 events",
    val daysGrid: List<CalendarDayModel?> = buildSeptemberDays(17),
    val events: List<CalendarEventItem> = listOf(
        CalendarEventItem(
            id = "1",
            title = "Team Standup",
            timeRange = "10:00 AM – 10:30 AM",
            projectTitle = "Mobile App Redesign",
            projectBgColor = Color(0xFFF1EBFF),
            projectTextColor = Color(0xFF6C47FF),
            durationText = "30 min",
            durationBgColor = Color(0xFFE5F9F0),
            durationTextColor = Color(0xFF00B862),
            iconEmoji = "👥",
            accentColor = Color(0xFF6C47FF)
        ),
        CalendarEventItem(
            id = "2",
            title = "Design Review",
            timeRange = "2:00 PM – 3:00 PM",
            projectTitle = "Website Development",
            projectBgColor = Color(0xFFE5F7FF),
            projectTextColor = Color(0xFF0096C7),
            durationText = "60 min",
            durationBgColor = Color(0xFFE5F7FF),
            durationTextColor = Color(0xFF0096C7),
            iconEmoji = "💻",
            accentColor = Color(0xFF00E5FF)
        )
    ),
    val taskDueTomorrowText: String = "1 task due tomorrow — Research deadline",
    val selectedTab: Int = 3
)

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    onNavigateToEventDetails: (String) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onCreateTaskClick: () -> Unit = {}
) {
    var selectedViewMode by rememberSaveable { mutableStateOf("Month") }
    var selectedDay by rememberSaveable { mutableIntStateOf(17) }
    var currentTab by rememberSaveable { mutableIntStateOf(3) }

    val daysGrid = remember(selectedDay) { buildSeptemberDays(selectedDay) }
    val dateHeader = if (selectedDay == 17) {
        "Today, Sep 17, 2026"
    } else {
        "Sep $selectedDay, 2026"
    }

    val uiState = CalendarUiState(
        selectedViewMode = selectedViewMode,
        selectedDay = selectedDay,
        selectedDateHeader = dateHeader,
        daysGrid = daysGrid,
        selectedTab = currentTab
    )

    CalendarContent(
        uiState = uiState,
        onViewModeSelected = { selectedViewMode = it },
        onDaySelected = { selectedDay = it },
        onEventClick = onNavigateToEventDetails,
        onNotificationsClick = onNotificationsClick,
        onTabSelected = {
            currentTab = it
            onTabSelected(it)
        },
        onCreateTaskClick = onCreateTaskClick,
        modifier = modifier
    )
}

@Composable
fun CalendarContent(
    uiState: CalendarUiState,
    onViewModeSelected: (String) -> Unit,
    onDaySelected: (Int) -> Unit,
    onEventClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
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
            CalendarHeader(
                monthYearString = uiState.monthYearString,
                selectedViewMode = uiState.selectedViewMode,
                onViewModeSelected = onViewModeSelected,
                onNotificationsClick = onNotificationsClick
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
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        DaysOfWeek.forEach { dayName ->
                            Text(
                                text = dayName,
                                color = SlateMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val rowsCount = (uiState.daysGrid.size + 6) / 7
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (rowIndex in 0 until rowsCount) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (colIndex in 0 until 7) {
                                    val dayModel = uiState.daysGrid.getOrNull(rowIndex * 7 + colIndex)
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (dayModel != null) {
                                            CalendarDayCell(
                                                dayModel = dayModel,
                                                onDayClick = { onDaySelected(dayModel.dayNumber) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = DividerGray)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.selectedDateHeader,
                            color = SlateDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.eventsCountText,
                            color = PrimaryPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    uiState.events.forEach { event ->
                        EventCard(
                            event = event,
                            onClick = { onEventClick(event.id) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
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
                                    text = uiState.taskDueTomorrowText,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Review Task",
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
private fun CalendarHeader(
    monthYearString: String,
    selectedViewMode: String,
    onViewModeSelected: (String) -> Unit,
    onNotificationsClick: () -> Unit
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
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Calendar",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = monthYearString,
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSurface, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSurface, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CalendarViewModes.forEach { mode ->
                ViewModePill(
                    label = mode,
                    isSelected = selectedViewMode == mode,
                    onClick = { onViewModeSelected(mode) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ViewModePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryPurple else DarkSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else InactivePillText,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CalendarDayCell(
    dayModel: CalendarDayModel,
    onDayClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onDayClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = if (dayModel.isSelected) PrimaryPurple else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Text(
                text = dayModel.dayNumber.toString(),
                color = if (dayModel.isSelected) Color.White else SlateTitle,
                fontSize = 15.sp,
                fontWeight = if (dayModel.isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }

        if (dayModel.dotColor != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(dayModel.dotColor, CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(7.dp))
        }
    }
}

@Composable
fun EventCard(
    event: CalendarEventItem,
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
                    .height(90.dp)
                    .background(event.accentColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .background(event.projectBgColor, RoundedCornerShape(14.dp))
                    ) {
                        Text(text = event.iconEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = event.title,
                            color = SlateDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = event.timeRange,
                            color = SlateMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .background(event.projectBgColor, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(event.projectTextColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = event.projectTitle,
                                    color = event.projectTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .background(event.durationBgColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = event.durationText,
                        color = event.durationTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    Project_M3Theme {
        CalendarScreen()
    }
}
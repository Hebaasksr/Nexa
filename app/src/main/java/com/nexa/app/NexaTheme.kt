package com.nexa.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val Navy = Color(0xFF08091A)
internal val Purple = Color(0xFF6133E0)
internal val Teal = Color(0xFF11B5C2)
internal val Panel = Color.White
internal val Outline = Color(0xFFE1DDEF)
internal val Muted = Color(0xFF686D88)
internal val Aqua = Color(0xFF087585)
internal val CardShape = RoundedCornerShape(20.dp)
internal val Sheet = Color(0xFFF5F3FA)
internal val HeaderAccent = Color(0xFF7DE5ED)
internal val HeaderMuted = Color(0xFFBDC0DD)

private val NexaColors = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECE4FF),
    onPrimaryContainer = Color(0xFF2B1C55),
    secondary = Teal,
    onSecondary = Navy,
    background = Sheet,
    surface = Panel,
    surfaceVariant = Color(0xFFEAE6F5),
    onBackground = Navy,
    onSurface = Navy,
    onSurfaceVariant = Muted,
    outline = Outline,
)

@Composable
fun NexaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = NexaColors, content = content)
}

/** Compatibility wrapper for older screens. All workspace screens use the shared NEXA palette. */
@Composable
fun Project_M3Theme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = NexaTheme(content)

@Composable
internal fun NexaHeader(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    action: @Composable RowScope.() -> Unit = {},
) {
    Column(
        Modifier.fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Navy, Color(0xFF21104C))))
            .statusBarsPadding(),
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                } else Spacer(Modifier.width(48.dp))
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NEXA", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Spacer(Modifier.width(36.dp).height(2.dp).background(Teal))
                }
                Row(Modifier.widthIn(min = 48.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically, content = action)
            }
            Spacer(Modifier.height(12.dp))
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, Modifier.padding(top = 3.dp, bottom = 5.dp), color = HeaderMuted)
        }
        Spacer(Modifier.fillMaxWidth().height(16.dp)
            .background(Sheet, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)))
    }
}

@Composable
internal fun NexaWorkspacePage(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    selectedTab: Int? = null,
    onTabSelected: (Int) -> Unit = {},
    onNew: () -> Unit = {},
    action: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Sheet)) {
        NexaHeader(title, subtitle, onBack, action)
        Column(Modifier.weight(1f).fillMaxWidth(), content = content)
        if (selectedTab != null) NexaBottomNavigationBar(selectedTab, onTabSelected, onNew)
    }
}

@Composable
internal fun Avatar(initials: String, accent: Color, modifier: Modifier = Modifier.size(44.dp)) {
    Box(modifier.background(accent, CircleShape), contentAlignment = Alignment.Center) {
        Text(initials, color = Navy, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun NexaCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = Panel),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = content,
    )
}

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)
val DarkBackground = Navy
val SurfaceDark = Color(0xFF13162F)
val BorderDark = Color(0xFF24284D)
val PrimaryPurple = Purple
val PrimaryPurpleVariant = Color(0xFF5A31F4)
val AccentCyan = Color(0xFF00BFCB)
val TextMuted = Muted
val TextMutedLabel = Color(0xFF9AA3BF)
val TextPlaceholder = Color(0xFF5F6585)



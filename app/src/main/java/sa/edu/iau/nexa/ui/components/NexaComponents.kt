package sa.edu.iau.nexa.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import sa.edu.iau.nexa.ui.theme.*

val HeaderGradient = Brush.horizontalGradient(listOf(Navy, DarkPurple))

@Composable
fun NexaLogo(modifier: Modifier = Modifier, contentDescription: String = "NEXA logo") {
    Image(painter = painterResource(sa.edu.iau.nexa.R.drawable.nexa_logo), contentDescription = contentDescription,
        modifier = modifier, contentScale = ContentScale.Fit)
}

@Composable
fun NexaWordmark(modifier: Modifier = Modifier) = NexaLogo(modifier.size(width = 110.dp, height = 44.dp))

@Composable
fun NexaHeader(
    title: String,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    trailingText: String? = null,
    onTrailing: (() -> Unit)? = null
) {
    Column(
        Modifier.fillMaxWidth().background(HeaderGradient).height(148.dp).padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Box(Modifier.fillMaxWidth().height(70.dp)) {
            NexaLogo(Modifier.align(Alignment.Center).size(width = 84.dp, height = 36.dp))
            if (onBack != null) {
                Box(Modifier.align(Alignment.CenterStart).size(44.dp).clip(CircleShape).background(Color.White.copy(alpha = .10f)).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Go back", tint = Color.White)
                }
            }
            if (onTrailing != null && (trailingIcon != null || trailingText != null)) {
                if (trailingIcon != null) IconButton(onClick = onTrailing, modifier = Modifier.align(Alignment.CenterEnd).size(48.dp)) {
                    Icon(trailingIcon, trailingText ?: "Action", tint = Color.White)
                }
                else TextButton(onClick = onTrailing, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text(trailingText.orEmpty(), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(title, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (subtitle.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Color(0xFFB8BDD9), fontSize = 12.sp, maxLines = 2, lineHeight = 16.sp, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun NexaShell(
    title: String,
    subtitle: String = "",
    selected: String? = null,
    onNavigate: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    trailingText: String? = null,
    onTrailing: (() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Background)) {
        NexaHeader(title, subtitle, onBack, trailingIcon, trailingText, onTrailing)
        Box(Modifier.weight(1f).fillMaxWidth()) { content() }
        if (footer != null) footer()
        else if (selected != null) NexaBottomNavigation(selected, onNavigate)
    }
}

@Composable
fun NexaBottomNavigation(selected: String, onNavigate: (String) -> Unit) {
    val options = listOf(
        Triple("Home", Icons.Outlined.Home, "dashboard"),
        Triple("Tasks", Icons.Outlined.CheckCircleOutline, "tasks"),
        Triple("New", Icons.Outlined.Add, "newTask"),
        Triple("Calendar", Icons.Outlined.CalendarMonth, "calendar"),
        Triple("More", Icons.Outlined.Menu, "more")
    )
    Row(Modifier.fillMaxWidth().height(88.dp).background(Color.White).padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        options.forEach { (label, icon, route) ->
            val active = selected == label
            val newAction = label == "New"
            Column(
                Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(if (newAction) 18.dp else 24.dp))
                    .background(if (newAction) Purple else if (active) PurpleLight else Color.Transparent)
                    .clickable { onNavigate(route) }.semantics { contentDescription = "$label tab" },
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                Icon(icon, null, tint = if (newAction) Color.White else if (active) Purple else Secondary, modifier = Modifier.size(23.dp))
                Spacer(Modifier.height(3.dp))
                Text(label, fontSize = 10.sp, color = if (newAction) Color.White else if (active) Purple else Secondary, fontWeight = if (newAction || active) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun NexaCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val base = modifier.fillMaxWidth().shadow(5.dp, RoundedCornerShape(18.dp), ambientColor = Ink.copy(alpha = .035f), spotColor = Ink.copy(alpha = .035f))
        .clip(RoundedCornerShape(18.dp)).background(Color.White)
    Column(if (onClick != null) base.clickable { onClick() }.padding(14.dp) else base.padding(14.dp), content = content)
}

@Composable
fun PrimaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, icon: ImageVector? = null) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth().heightIn(min = 50.dp), shape = RoundedCornerShape(17.dp), contentPadding = PaddingValues(horizontal = 18.dp), colors = ButtonDefaults.buttonColors(containerColor = Purple)) {
        if (icon != null) { Icon(icon, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)) }
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun SecondaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.fillMaxWidth().heightIn(min = 50.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Border)) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Purple)
    }
}

@Composable
fun ActionFooter(primary: String, onPrimary: () -> Unit, secondary: String = "Cancel", onSecondary: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Background).padding(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimaryButton(primary, onPrimary)
        SecondaryButton(secondary, onSecondary)
    }
}

@Composable
fun NexaInput(value: String, onChange: (String) -> Unit, label: String, modifier: Modifier = Modifier, placeholder: String = "", singleLine: Boolean = true, minLines: Int = 1, isError: Boolean = false, keyboard: KeyboardType = KeyboardType.Text) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        OutlinedTextField(
            value = value, onValueChange = onChange, placeholder = { Text(placeholder, color = Secondary, fontSize = 14.sp) },
            singleLine = singleLine, minLines = minLines, isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(13.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Ink),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple, unfocusedBorderColor = Border,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                errorBorderColor = Danger, errorContainerColor = Color.White
            )
        )
    }
}

@Composable
fun NexaDropdown(label: String, value: String, options: List<String>, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Ink, fontSize = 12.sp)
        Row(
            Modifier.fillMaxWidth().heightIn(min = 52.dp).clip(RoundedCornerShape(13.dp)).background(Color.White)
                .border(1.dp, Border, RoundedCornerShape(13.dp)).clickable { expanded = true }.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(value.ifBlank { "Select an option" }, Modifier.weight(1f), color = if (value.isBlank()) Secondary else Ink, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(Icons.Outlined.KeyboardArrowDown, null, tint = Secondary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.distinct().forEach { option ->
                DropdownMenuItem(text = { Text(option, fontSize = 14.sp) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

@Composable
fun FilterRow(options: List<String>, selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        options.forEach { o ->
            Box(Modifier.weight(1f).height(36.dp).clip(RoundedCornerShape(20.dp)).background(if (o == selected) Purple else Color.White)
                .clickable { onSelect(o) }, contentAlignment = Alignment.Center) {
                Text(o, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (o == selected) Color.White else Secondary, maxLines = 1)
            }
        }
    }
}

@Composable
fun StatusChip(label: String, color: Color = Purple, background: Color = PurpleLight) {
    Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = color,
        modifier = Modifier.clip(RoundedCornerShape(15.dp)).background(background).padding(horizontal = 9.dp, vertical = 5.dp), maxLines = 1)
}

@Composable
fun TaskStatusChip(status: String) {
    when (status) {
        "Done" -> StatusChip("● Completed", Success, SuccessBackground)
        "In Progress" -> StatusChip("● In Progress", Color(0xFF087782), Color(0xFFE5F8FA))
        else -> StatusChip("● To Do", Secondary, Background)
    }
}

@Composable
fun PriorityChip(priority: String) {
    when (priority) {
        "High" -> StatusChip("High", Danger, DangerBackground)
        "Low" -> StatusChip("Low", Success, SuccessBackground)
        else -> StatusChip("Medium", Amber, Color(0xFFFFF1E2))
    }
}

@Composable
fun ProgressLine(progress: Float, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
            color = Purple,
            trackColor = Border
        )
        Text("${(progress * 100).toInt()}%", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CircleAvatar(initials: String, modifier: Modifier = Modifier) {
    Box(modifier.size(44.dp).clip(CircleShape).background(PurpleLight), contentAlignment = Alignment.Center) {
        Text(initials, color = Purple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun SectionTitle(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Ink)
        if (actionLabel != null && onAction != null) TextButton(onClick = onAction) { Text(actionLabel, fontSize = 12.sp, color = Purple) }
    }
}

@Composable
fun EmptyState(title: String, detail: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth().padding(vertical = 44.dp, horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(Icons.Outlined.Inbox, null, tint = Purple, modifier = Modifier.size(56.dp))
        Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        Text(detail, color = Secondary, fontSize = 13.sp, textAlign = TextAlign.Center)
        if (action != null && onAction != null) PrimaryButton(action, onAction)
    }
}

@Composable
fun SearchBarNexa(query: String, onQuery: (String) -> Unit, placeholder: String = "Search projects, tasks or files...") {
    OutlinedTextField(
        value = query, onValueChange = onQuery,
        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Secondary) },
        placeholder = { Text(placeholder, fontSize = 13.sp, color = Secondary) },
        singleLine = true, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Ink),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
    )
}

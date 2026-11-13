package sa.edu.iau.nexa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF090E29)
val DarkPurple = Color(0xFF1A0A4A)
val Purple = Color(0xFF6133E0)
val PurpleLight = Color(0xFFF0E9FF)
val Cyan = Color(0xFF14C7D6)
val Background = Color(0xFFF6F4FB)
val Ink = Color(0xFF1A1A33)
val Secondary = Color(0xFF62667E)
val Border = Color(0xFFE3E3F0)
val Success = Color(0xFF087755)
val SuccessBackground = Color(0xFFE6F8F0)
val Danger = Color(0xFFB52445)
val DangerBackground = Color(0xFFFFF0F2)
val Amber = Color(0xFF9E4B20)

private val scheme = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Cyan,
    background = Background,
    surface = Color.White,
    onSurface = Ink,
    onBackground = Ink,
    outline = Border,
    error = Danger
)

@Composable
fun NexaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}

package rw.momo.directory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Yellow = Color(0xFFFFCC00)
private val DarkText = Color(0xFF1A1A1A)
private val Deep = Color(0xFF0F0F0F)

private val Light = lightColorScheme(
    primary = Yellow, onPrimary = DarkText,
    secondary = Color(0xFF2C2C2C), onSecondary = Color.White,
    background = Color(0xFFFAFAF7), surface = Color.White,
    onBackground = DarkText, onSurface = DarkText
)
private val Dark = darkColorScheme(
    primary = Yellow, onPrimary = DarkText,
    secondary = Color(0xFFE5E5E5),
    background = Deep, surface = Color(0xFF1C1C1C),
    onBackground = Color.White, onSurface = Color.White
)

@Composable
fun MoMoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        content = content
    )
}
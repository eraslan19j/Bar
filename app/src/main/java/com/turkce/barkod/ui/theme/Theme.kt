package com.turkce.barkod.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val KoyuRenkler = darkColorScheme(
    primary = Color(0xFF4DA3FF),
    onPrimary = Color(0xFF001B33),
    primaryContainer = Color(0xFF0F3B63),
    onPrimaryContainer = Color(0xFFD6E7FF),
    secondary = Color(0xFF7FD1AE),
    onSecondary = Color(0xFF00281A),
    background = Color(0xFF0B0F14),
    onBackground = Color(0xFFE6EAF0),
    surface = Color(0xFF11161D),
    onSurface = Color(0xFFE6EAF0),
    surfaceVariant = Color(0xFF1B222C),
    onSurfaceVariant = Color(0xFFB9C3D0),
    outline = Color(0xFF3A4553),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0000)
)

private val AcikRenkler = lightColorScheme(
    primary = Color(0xFF0B5FBF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E7FF),
    onPrimaryContainer = Color(0xFF001B33),
    secondary = Color(0xFF1F7A57),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF6F8FB),
    onBackground = Color(0xFF10151C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF10151C),
    surfaceVariant = Color(0xFFE7ECF3),
    onSurfaceVariant = Color(0xFF41484F),
    outline = Color(0xFF71787E),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF)
)

private val UygulamaTipografisi = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

@Composable
fun BarkodTheme(
    koyuTema: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (koyuTema) KoyuRenkler else AcikRenkler,
        typography = UygulamaTipografisi,
        content = content
    )
}

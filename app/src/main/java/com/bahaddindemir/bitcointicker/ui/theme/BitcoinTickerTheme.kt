package com.bahaddindemir.bitcointicker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class BitcoinTickerAppColors(
    val brand: Color,
    val background: Color,
    val inputBackground: Color,
    val action: Color,
    val mutedText: Color,
    val onDark: Color,
)

private val AppColors = BitcoinTickerAppColors(
    brand = Color(0xFF273739),
    background = Color(0xFF1B232A),
    inputBackground = Color(0xFF171C21),
    action = Color(0xFF7ED2AB),
    mutedText = Color(0xFF808080),
    onDark = Color.White,
)

private val ColorScheme = darkColorScheme(
    primary = AppColors.action,
    onPrimary = AppColors.brand,
    secondary = AppColors.brand,
    background = AppColors.background,
    onBackground = AppColors.onDark,
    surface = AppColors.background,
    onSurface = AppColors.onDark,
    outline = AppColors.mutedText,
)

private val LocalBitcoinTickerColors = staticCompositionLocalOf { AppColors }

object BitcoinTickerColors {
    val current: BitcoinTickerAppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalBitcoinTickerColors.current
}

@Composable
fun BitcoinTickerTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalBitcoinTickerColors provides AppColors) {
        MaterialTheme(
            colorScheme = ColorScheme,
            content = content
        )
    }
}

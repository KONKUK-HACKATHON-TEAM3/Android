package com.konkuk.hackathon_team3.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

object KONKUKHACKATHONTEAM3Theme {
    val typography: GasTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalGasTypographyProvider.current
}

@Composable
fun ProvideGasTypography(
    typography: GasTypography,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalGasTypographyProvider provides typography,
        content = content
    )
}

@Composable
fun KONKUKHACKATHONTEAM3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    ProvideGasTypography(typography = defaultGasTypography) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }

}
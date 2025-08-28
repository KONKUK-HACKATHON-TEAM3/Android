package com.konkuk.hackathon_team3.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.konkuk.hackathon_team3.R

object LineSeedFont {
    val Bold = FontFamily(Font(R.font.lineseedkr_bd))
    val Thick = FontFamily(Font(R.font.lineseedkr_th))
    val Regular = FontFamily(Font(R.font.lineseedkr_rg))
}

sealed interface TypographyTokens {
    @Immutable
    data class Bold(val bold: TextStyle)

    @Immutable
    data class Thick(val thick: TextStyle)

    @Immutable
    data class Regular(val regular: TextStyle)
}

@Immutable
data class GasTypography(
    val bold: TypographyTokens.Bold,
    val thick: TypographyTokens.Thick,
    val regular: TypographyTokens.Regular
)

val defaultGasTypography = GasTypography(
    bold = TypographyTokens.Bold(
        TextStyle(
            fontFamily = LineSeedFont.Bold,
        )
    ),
    thick = TypographyTokens.Thick(
        TextStyle(
            fontFamily = LineSeedFont.Thick,
        )
    ),
    regular = TypographyTokens.Regular(
        TextStyle(
            fontFamily = LineSeedFont.Regular,
        )
    )
)

val GasTypography.boldStyle get() = bold.bold
val GasTypography.thickStyle get() = thick.thick
val GasTypography.regularStyle get() = regular.regular

val LocalGasTypographyProvider = staticCompositionLocalOf { defaultGasTypography }

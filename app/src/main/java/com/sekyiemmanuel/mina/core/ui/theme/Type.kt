package com.sekyiemmanuel.mina.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.sekyiemmanuel.mina.R

private val InterFontFamily = FontFamily(
    Font(resId = R.font.inter_variable, weight = FontWeight.Normal),
    Font(resId = R.font.inter_variable, weight = FontWeight.Medium),
    Font(resId = R.font.inter_variable, weight = FontWeight.SemiBold),
    Font(resId = R.font.inter_variable, weight = FontWeight.Bold),
)

private fun TextStyle.withInter() = copy(fontFamily = InterFontFamily)

private val BaseTypography = Typography()

val AppTypography = Typography(
    displayLarge = BaseTypography.displayLarge.withInter(),
    displayMedium = BaseTypography.displayMedium.withInter(),
    displaySmall = BaseTypography.displaySmall.withInter(),
    headlineLarge = BaseTypography.headlineLarge.withInter(),
    headlineMedium = BaseTypography.headlineMedium.withInter(),
    headlineSmall = BaseTypography.headlineSmall.withInter(),
    titleLarge = BaseTypography.titleLarge.withInter(),
    titleMedium = BaseTypography.titleMedium.withInter(),
    titleSmall = BaseTypography.titleSmall.withInter(),
    bodyLarge = BaseTypography.bodyLarge.withInter(),
    bodyMedium = BaseTypography.bodyMedium.withInter(),
    bodySmall = BaseTypography.bodySmall.withInter(),
    labelLarge = BaseTypography.labelLarge.withInter(),
    labelMedium = BaseTypography.labelMedium.withInter(),
    labelSmall = BaseTypography.labelSmall.withInter(),
)


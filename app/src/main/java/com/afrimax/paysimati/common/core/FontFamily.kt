package com.afrimax.paysimati.common.core

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.afrimax.paysimati.R


@Composable
fun InterFontFamily() = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

@Composable
fun PaySimatiTypography() = Typography(
    defaultFontFamily = InterFontFamily(), body1 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.Normal, fontSize = 18.sp
    ), body2 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.Normal, fontSize = 16.sp
    ), h6 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp
    ), h5 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.SemiBold, fontSize = 18.sp
    ), h4 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.SemiBold, fontSize = 20.sp
    ), h3 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.SemiBold, fontSize = 22.sp
    ), h2 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.SemiBold, fontSize = 24.sp
    ), h1 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.SemiBold, fontSize = 26.sp
    ), subtitle1 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.Normal, fontSize = 16.sp
    ), subtitle2 = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.Normal, fontSize = 14.sp
    ), caption = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.Normal, fontSize = 12.sp
    ), button = TextStyle(
        fontFamily = InterFontFamily(), fontWeight = FontWeight.SemiBold, fontSize = 16.sp
    )
)
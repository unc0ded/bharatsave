package com.bharatsave.goldapp.model

import androidx.annotation.DrawableRes

data class OnBoardingItem(
    val subText: String,
    val primaryText: String,
    val highlightedText: String,
    @DrawableRes val imgRes: Int
)
package com.bharatsave.goldapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class AlertData(
    val titleText: String?,
    val subTitleText: String?,
    val positiveText: String?,
    val positiveAction: @RawValue () -> Unit = {},
    val negativeText: String?,
    val negativeAction: @RawValue () -> Unit = {}
) : Parcelable
package com.bharatsave.goldapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(
    val imageUrl: String,
    val actionButtonText: String
) : Parcelable
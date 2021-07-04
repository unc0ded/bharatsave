package com.dev.`in`.drogon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(
    val imageUrl: String,
    val actionButtonText: String
) : Parcelable
package com.dev.`in`.drogon.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String,
    val email: String,
    val phoneNumber: String
) : Parcelable
package com.bharatsave.goldapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Entity(tableName = "user_table")
@Parcelize
@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @PrimaryKey
    @Json(name = "phone_number")
    val phoneNumber: String
) : Parcelable
package com.bharatsave.goldapp.model.game

import com.squareup.moshi.Json

enum class Orientation {
    @Json(name = "landscape")
    LANDSCAPE,

    @Json(name = "portrait")
    PORTRAIT,

    @Json(name = "any")
    ANY,
}
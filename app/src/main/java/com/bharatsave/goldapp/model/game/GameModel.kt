package com.bharatsave.goldapp.model.game

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameModel(
    @Json(name = "name")
    val gameName: String,
    @Json(name = "url")
    val gameUrl: String,
    @Json(name = "entry_cost")
    val entryTokenCost: Int,
    @Json(name = "reward_value")
    val rewardTokenValue: Int,
    val orientation: Orientation
)
package com.development.sota.scooter.ui.map.data

import com.google.gson.annotations.SerializedName

data class Rate(
    val id: Long,
    val minute: String,
    val hour: String,
    @SerializedName("free_book_minutes") val freeBookMinutes: Int,
    @SerializedName("pause_rate") val pauseRate: String
)

enum class RateType(val value: String) {
    MINUTE("minute"), HOUR("hour")
}
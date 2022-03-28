package com.development.sota.scooter.ui.drivings.domain.entities

import android.annotation.SuppressLint
import android.util.Log
import com.development.sota.scooter.ui.map.data.Scooter
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

data class Order(
    val id: Long,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("finish_time") val finishTime: String,
    @SerializedName("is_paid") val isPaid: Boolean,
    @SerializedName("activation_time") val activationTime: String = "",
    val status: String,
    val scooter: Long,
    val cost: Double,
    val rate: Long
) {
    companion object {
        @SuppressLint("ConstantLocale")
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val decodeDateFormatter =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSSS'Z'", Locale.getDefault())
    }

    fun parseStartTime(): Long? {
        return try {
            val tz = TimeZone.getDefault()
            val now = Date()

            return Instant.parse(startTime).toEpochMilli() - tz.getOffset(now.time)
        } catch (e: Exception) {
            null
        }
    }

    fun parseEndTime(): Date {
        return decodeDateFormatter.parse(finishTime) ?: Date()
    }

    fun parseActivationTime(): Date {
        return decodeDateFormatter.parse(activationTime) ?: Date()
    }

}

data class OrderWithStatus(val order: Order, val scooter: Scooter, var status: OrderStatus)

data class AddOrderResponse(val id: Long)

enum class OrderStatus(val value: String) {
    CANDIDIATE("CA"), BOOKED("BK"), CHOOSE_RATE("CR"), ACTIVATED("AC"), CLOSED("CD"), CANCELED("CCD")
}
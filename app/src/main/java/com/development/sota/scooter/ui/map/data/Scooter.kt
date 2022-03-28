package com.development.sota.scooter.ui.map.data

import com.development.sota.scooter.R
import com.google.gson.annotations.SerializedName
import com.mapbox.mapboxsdk.geometry.LatLng


data class Scooter(
    val id: Long,
    @SerializedName("scooter_name") val name: String,
    val status: String,
    @SerializedName("alert_status") val alertStatus: String,
    val battery: Double, // Max: 60000.0
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val photo: String?,
    @SerializedName("tracker_id") val trackerId: String,
    @SerializedName("speed_limit") val speedLimit: Double,
    val lamp: Boolean,
    val engine: Boolean,
    @SerializedName("scooter_group") val scooterGroup: List<Int>,
    val rate: Int
) {
    fun getScooterIcon(): Int {
        return when {
            battery <= 20000.0 -> R.drawable.ic_icon_scooter_third
            battery in 20000.0..40000.00 -> R.drawable.ic_icon_scooter_second
            else -> R.drawable.ic_icon_scooter_first
        }
    }

    fun getLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    fun getBatteryPercentage(): String {
        val percents = battery / 60000 * 100

        return "${percents.toInt()} %"
    }

    fun getScooterRideInfo(): String {
        val percents = battery / 60000
        val minutes: Int = (200 * percents).toInt()
        val kms: Int = (45 * percents).toInt()

        return "~${if (minutes / 60 == 0) "" else "${minutes / 60}h"} ${if (minutes % 60 == 0) "" else "${minutes % 60}m"} · ${kms}km"

    }

}

data class ScooterResponse(val data: List<Scooter>)

/**
 *  ('ON', 'Online'),
 *  ('UR', 'Under repair'),
 *  ('RT', 'Rented'),
 *  ('BK', 'Booked')
 * */

enum class ScooterStatus(val value: String) {
    ONLINE("ON"), UNDER_REPAIR("UR"), RENTED("RT"), BOOKED("BK")
}

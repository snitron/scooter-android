package com.development.sota.scooter.ui.map.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.development.sota.scooter.R
import com.development.sota.scooter.base.BasePresenter
import com.development.sota.scooter.ui.drivings.domain.entities.Order
import com.development.sota.scooter.ui.drivings.domain.entities.OrderStatus
import com.development.sota.scooter.ui.map.data.*
import com.development.sota.scooter.ui.map.domain.MapInteractor
import com.development.sota.scooter.ui.map.domain.MapInteractorImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import java.util.*

class MapPresenter(val context: Context) : MvpPresenter<MapView>(), BasePresenter {
    private val interactor: MapInteractor = MapInteractorImpl(this)

    private var scooters = arrayListOf<Scooter>()

    var locationPermissionGranted = false
    var position: LatLng = LatLng(44.894997, 37.316259)
        set(value) {
            if (currentScooter != null) {
                interactor.getRouteFor(destination = value, origin = currentScooter!!.getLatLng())
            }
            field = value
        }

    var rates = arrayListOf<Rate>() //Minute, Hour
    var scootersWithOrders = hashMapOf<Long, Long>() //Minute, Hour
    lateinit var scootersGeoJsonSource: List<Feature>
    var usingScooters = hashSetOf<Long>()

    var currentScooter: Scooter? = null
    set(value) {
        if(value != currentScooter) {
            makeFeaturesFromScootersAndSendToMap()
        }
        field = value
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    interactor.getScootersAndOrders()
                    interactor.getGeoZone()
                } catch (e: Exception) {
                    Log.w("Error calling server", e.localizedMessage)
                }

                delay(30000)
            }
        }

    }

    fun scootersGotFromServer(scooters: ArrayList<Scooter>) {
        this.scooters = scooters

        makeFeaturesFromScootersAndSendToMap()
    }

    fun ordersGotFromServer(orders: List<Order>) {
        viewState.setLoading(false)

        for (order in orders) {
            if (order.status == OrderStatus.CLOSED.value) {
                scootersWithOrders =
                    scootersWithOrders.filterValues { it != order.id } as HashMap<Long, Long>
            }
        }

        initMapPopupView(orders)
    }

    fun scootersAndOrdersGotFormServer(scootersAndOrders: Pair<List<Scooter>, List<Order>>) {
        GlobalScope.launch {
            usingScooters.addAll(scootersAndOrders.second.map { it.scooter })
            scooters =
                scootersAndOrders.first.filter { it.status == ScooterStatus.ONLINE.value || it.id in usingScooters } as ArrayList<Scooter>

            viewState.setLoading(false)

            makeFeaturesFromScootersAndSendToMap()
            initMapPopupView(scootersAndOrders.second)
        }
    }

    fun initMapPopupView(orders: List<Order>) {
        val bookCount = orders.count { it.status == OrderStatus.BOOKED.value }
        val rentCount = orders.count { it.status == OrderStatus.ACTIVATED.value }

        viewState.initPopupMapView(orders, bookCount, rentCount)
    }

    fun ratesGotFromServer(rates: List<Rate>, scooterId: Long) {
        this.rates = rates as ArrayList<Rate>

        val neededRate = scooters.first { scooter: Scooter -> scooter.id == scooterId }.rate
        viewState.setRateForScooterCard(
            rates.first { rate: Rate -> rate.id == neededRate.toLong() },
            scooterId
        )
    }

    fun getScooters(): ArrayList<Scooter> {
        return scooters
    }

    fun clickedOnScooterWith(id: Long) {
        currentScooter = scooters.firstOrNull { it.id == id }

        if (currentScooter != null) {
            viewState.showScooterCard(currentScooter!!, OrderStatus.CANDIDIATE)

            interactor.getRouteFor(destination = position, origin = currentScooter!!.getLatLng())
            interactor.getRate(id)
        }
    }

    @SuppressLint("TimberArgCount")
    fun errorGotFromServer(error: String) {
        Log.w("Error calling server", error)
        viewState.showToast(context.getString(R.string.error_api))
        viewState.setLoading(false)
    }

    fun newOrderGotFromServer(orderId: Long, scooterId: Long, withActivation: Boolean) {
        scootersWithOrders[scooterId] = orderId

        if (withActivation) {
            interactor.activateOrder(orderId)
        }

        interactor.getOrders()
        viewState.setLoading(false)
        viewState.sendToDrivingsList()
    }

    fun updateLocationPermission(permission: Boolean) {
        locationPermissionGranted = permission

        if (locationPermissionGranted) viewState.initLocationRelationships()
    }

    fun gotRouteFromServer(route: DirectionsRoute) {
        viewState.drawRoute(route.geometry() ?: "{}")
    }

    fun getInfoAboutUserFromServer(
        info: Pair<Client, BookingBlockResponse>,
        scooterId: Long,
        withActivation: Boolean
    ) {
        if (info.first.balance.toDouble() > 0 && !info.second.blocked) {
            interactor.addOrder(
                startTime = Order.dateFormatter.format(Date()),
                scooterId = scooterId,
                withActivation = withActivation
            )
        } else if (info.first.balance.toDouble() <= 0) {
            viewState.setLoading(false)
            viewState.setDialogBy(MapDialogType.NO_MONEY_FOR_START)
        } else if (info.second.blocked) {
            viewState.setLoading(false)
            viewState.setDialogBy(MapDialogType.BANNED_FOR_BOOKING)
        }
    }

    fun cancelDialog(type: MapDialogType) {

    }

    fun clickedOnBookButton(scooterId: Long) {
        viewState.setLoading(true)
        interactor.checkNoneNullBalanceAndBookBan(scooterId)
    }


    fun purchaseToBalance() {
        viewState.showToast("Here will be purse")
    }

    fun sendToTheDrivingsList() {
        viewState.sendToDrivingsList()
    }

    fun onStartEmitted() {
        interactor.getScootersAndOrders()

        val id = interactor.getCodeOfScooterAndNull()

        if (id != null) {
            clickedOnScooterWith(id)
        }
    }

    fun scooterUnselected() {
        currentScooter = null
    }

    fun geoZoneGot(geoZoneJson: String) {
        val type = object : TypeToken<List<JsonObject>>() {}.type
        val jsonArray = Gson().fromJson<List<JsonObject>>(geoZoneJson.replace(" ", "").replace("[[", "[[[").replace("]]", "]]]"), type)
        val features = arrayListOf<Feature>()

        for (i in jsonArray) {
            Log.w("GEOSTRING", i.toString())
            features.add(Feature.fromJson(i.toString()))
        }

        viewState.drawGeoZones(features)
    }

    private fun makeFeaturesFromScootersAndSendToMap() {
        GlobalScope.launch {
            scootersGeoJsonSource = scooters.groupBy { it.getScooterIcon() }.mapValues { entry ->
                entry.value.map {
                    Feature.fromJson(
                        """{
                                "type": "Feature",
                                "geometry": {
                                    "type": "Point",
                                    "coordinates": [${it.longitude}, ${it.latitude}]
                                },
                                "properties": {
                                    "id": ${it.id},
                                    "scooter-image": ${
                                        if(currentScooter != null && currentScooter!!.id == it.id) {
                                           "scooter-chosen" 
                                        } else {
                                            when (it.getScooterIcon()) {
                                                R.drawable.ic_icon_scooter_third -> "scooter-third"
                                                R.drawable.ic_icon_scooter_second -> "scooter-second"
                                                R.drawable.ic_icon_scooter_first -> "scooter-first"
                                                else -> "scooter-third"
                                            }
                                        }
                        }
                                }
                            }"""
                    )
                }
            }.values.flatten()

            viewState.updateScooterMarkers(scootersGeoJsonSource)
        }
    }

    fun makeFeatureFromLatLng(latLng: LatLng): Feature {
        return Feature.fromJson(
            """{
                                "type": "Feature",
                                "geometry": {
                                    "type": "Point",
                                    "coordinates": [${latLng.longitude}, ${latLng.latitude}]
                                }
                            }"""
        )
    }

    override fun onDestroyCalled() {
        interactor.disposeRequests()
    }
}
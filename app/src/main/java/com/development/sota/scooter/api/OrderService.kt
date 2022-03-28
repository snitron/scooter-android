package com.development.sota.scooter.api

import com.development.sota.scooter.ui.drivings.domain.entities.AddOrderResponse
import com.development.sota.scooter.ui.drivings.domain.entities.Order
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface OrderService {
    //TODO: Add "active" filter

    @GET("getOrder")
    fun getOrders(@Query("client") clientId: Long): Observable<List<Order>>

    @POST("addOrder/")
    @FormUrlEncoded
    fun addOrder(
        @Field("start_time") startTime: String,
        @Field("scooter") scooterId: Long,
        @Field("client") clientId: Long
    ): Observable<AddOrderResponse>

    @POST("activateRent/")
    @FormUrlEncoded
    fun activateOrder(
        @Field("id") orderId: Long
    ): Completable

    @POST("closeOrder/")
    @FormUrlEncoded
    fun closeOrder(
        @Field("id") orderId: Long
    ): Completable

    @POST("cancelOrder/")
    @FormUrlEncoded
    fun cancelOrder(
        @Field("id") orderId: Long
    ): Completable

    @POST("setTaxType/")
    @FormUrlEncoded
    fun setRateType(
        @Field("order") orderId: Long,
        @Field("type") rateType: String
    ): Completable
}
package com.development.sota.scooter.ui.drivings.domain

import android.util.Log
import com.development.sota.scooter.api.MapRetrofitProvider
import com.development.sota.scooter.api.OrdersRetrofitProvider
import com.development.sota.scooter.base.BaseInteractor
import com.development.sota.scooter.db.SharedPreferencesProvider
import com.development.sota.scooter.ui.drivings.domain.entities.Order
import com.development.sota.scooter.ui.drivings.presentation.fragments.list.DrivingsListPresenter
import com.development.sota.scooter.ui.map.data.RateType
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

interface DrivingsListInteractor : BaseInteractor {
    fun getAllOrdersAndScooters()
    fun addOrder(order: Order)
    fun cancelOrder(orderId: Long)
    fun activateOrder(orderId: Long)
    fun setRateAndActivateOrder(orderId: Long, type: RateType)
    fun closeOrder(orderId: Long)
}

class DrivingsListInteractorImpl(private val presenter: DrivingsListPresenter) :
    DrivingsListInteractor {
    private val compositeDisposable = CompositeDisposable()
    private val sharedPreferences = SharedPreferencesProvider(presenter.context).sharedPreferences

    override fun getAllOrdersAndScooters() {
        compositeDisposable.add(
            Observables.zip(
                OrdersRetrofitProvider.service
                    .getOrders(sharedPreferences.getLong("id", -1L)),
                MapRetrofitProvider.service.getScooters()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = { presenter.gotErrorFromServer(it.localizedMessage ?: "") },
                    onNext = {
                        presenter.gotOrdersAndScootersFromServer(it)
                    }
                )
        )
    }

    override fun addOrder(order: Order) {
        compositeDisposable.add(
            OrdersRetrofitProvider.service
                .addOrder(
                    startTime = order.startTime,
                    scooterId = order.scooter,
                    clientId = sharedPreferences.getLong("id", -1)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { presenter.actionEnded(true) { getAllOrdersAndScooters() } },
                    onError = { presenter.actionEnded(false) }
                )
        )
    }

    override fun cancelOrder(orderId: Long) {
        compositeDisposable.add(
            OrdersRetrofitProvider.service
                .cancelOrder(
                    orderId
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = { presenter.actionEnded(true) { getAllOrdersAndScooters() } },
                    onError = { presenter.actionEnded(false) }
                )
        )
    }

    override fun activateOrder(orderId: Long) {
        TODO("Not yet implemented")
    }

    override fun setRateAndActivateOrder(orderId: Long, type: RateType) {
        compositeDisposable.add(
            OrdersRetrofitProvider.service
                .setRateType(orderId, type.value)
                .doOnComplete {
                    OrdersRetrofitProvider.service.activateOrder(orderId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = { presenter.actionEnded(true) { getAllOrdersAndScooters() } },
                    onError = {
                        presenter.actionEnded(false)
                        Log.w("Drivings server error", it.localizedMessage ?: "")
                    }
                )
        )
    }

    override fun closeOrder(orderId: Long) {
        compositeDisposable.add(
            OrdersRetrofitProvider.service
                .closeOrder(
                    orderId
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = { presenter.actionEnded(true) { getAllOrdersAndScooters() } },
                    onError = { presenter.actionEnded(false) }
                )
        )
    }

    override fun disposeRequests() {
        compositeDisposable.clear()
    }

}
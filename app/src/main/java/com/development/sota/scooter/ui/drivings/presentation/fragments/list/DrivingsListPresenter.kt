package com.development.sota.scooter.ui.drivings.presentation.fragments.list

import android.content.Context
import com.development.sota.scooter.R
import com.development.sota.scooter.base.BasePresenter
import com.development.sota.scooter.ui.drivings.domain.DrivingsListInteractor
import com.development.sota.scooter.ui.drivings.domain.DrivingsListInteractorImpl
import com.development.sota.scooter.ui.drivings.domain.entities.Order
import com.development.sota.scooter.ui.drivings.domain.entities.OrderStatus
import com.development.sota.scooter.ui.drivings.domain.entities.OrderWithStatus
import com.development.sota.scooter.ui.map.data.RateType
import com.development.sota.scooter.ui.map.data.Scooter
import moxy.MvpPresenter

class DrivingsListPresenter(val context: Context) : MvpPresenter<DrivingsListView>(),
    BasePresenter {
    private val interactor: DrivingsListInteractor = DrivingsListInteractorImpl(this)
    private var orders = arrayListOf<Order>()
    private var ordersWithStatuses = arrayListOf<OrderWithStatus>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.setLoading(true)
        interactor.getAllOrdersAndScooters()
    }

    fun gotOrdersAndScootersFromServer(ordersAndScooters: Pair<List<Order>, List<Scooter>>) {
        this.orders = ordersAndScooters.first as ArrayList<Order>

        ordersWithStatuses.clear()
        val finishedOrders = arrayListOf<OrderWithStatus>()

        for (order in orders) {
            when (order.status) {
                OrderStatus.CLOSED.value ->
                    finishedOrders.add(
                        OrderWithStatus(
                            order,
                            ordersAndScooters.second.first { it.id == order.scooter },
                            OrderStatus.CLOSED
                        )
                    )
                OrderStatus.CANCELED.value ->
                    finishedOrders.add(
                        OrderWithStatus(
                            order,
                            ordersAndScooters.second.first { it.id == order.scooter },
                            OrderStatus.CANCELED
                        )
                    )
                OrderStatus.BOOKED.value ->
                    ordersWithStatuses.add(
                        OrderWithStatus(
                            order,
                            ordersAndScooters.second.first { it.id == order.scooter },
                            OrderStatus.BOOKED
                        )
                    )
                OrderStatus.ACTIVATED.value ->
                    ordersWithStatuses.add(
                        OrderWithStatus(
                            order,
                            ordersAndScooters.second.first { it.id == order.scooter },
                            OrderStatus.ACTIVATED
                        )
                    )
                else ->
                    ordersWithStatuses.add(
                        OrderWithStatus(
                            order,
                            ordersAndScooters.second.first { it.id == order.scooter },
                            OrderStatus.CLOSED
                        )
                    )
            }
        }

        viewState.setLoading(false)
        viewState.initViewPager2(Pair(ordersWithStatuses, finishedOrders))
    }

    fun gotErrorFromServer(message: String) {
        viewState.setLoading(false)
        viewState.showToast(message)
    }

    fun cancelOrder(id: Long) {
        viewState.setLoading(true)

        interactor.cancelOrder(id)
    }

    fun activateOrder(id: Long) {
        viewState.setLoading(true)

        interactor.activateOrder(id)
    }

    fun setRateAndActivate(id: Long, type: RateType) {
        viewState.setLoading(true)

        interactor.setRateAndActivateOrder(id, type)
    }

    fun closeOrder(id: Long) {
        viewState.setLoading(true)

        interactor.closeOrder(id)
    }


    fun actionEnded(success: Boolean, actionToPerform: () -> Unit = {}) {
        if (success) {
            actionToPerform()
        } else {
            viewState.showToast(context.getString(R.string.error_api))
        }
    }

    override fun onDestroyCalled() {
        interactor.disposeRequests()
    }

}
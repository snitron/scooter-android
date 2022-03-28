package com.development.sota.scooter.ui.drivings.presentation.fragments.qr

import com.development.sota.scooter.base.BasePresenter
import com.development.sota.scooter.ui.drivings.presentation.fragments.QRView
import moxy.MvpPresenter

class QRPresenter : MvpPresenter<QRView>(), BasePresenter {
    private var errorCount = 0

    override fun onDestroyCalled() {

    }

    fun gotErrorFromScanner() {
        if (++errorCount >= 3) {
            viewState.sendToCodeFragment()
        }
    }

    fun getDataFromScanner(data: String) {
        val parsed = parseQRData(data)?.toLongOrNull()

        if (parsed == null) {
            viewState.setLoading(false)
            gotErrorFromScanner()
        } else {
            viewState.sendFoundCodeToDrivings(parsed)
            viewState.setLoading(true)
        }
    }

    fun gotResponseFromActivity(result: Boolean) {
        if (result) {
            viewState.setLoading(false)
        } else {
            viewState.setLoading(false)
            viewState.sendToCodeFragment()
        }
    }

    // If fails - return null
    // Example: scooter.sota/qr?id=1234
    private fun parseQRData(data: String): String? {
        val index = data.indexOf("id=")

        if (index == -1) {
            return null
        } else {
            return data.substringAfter("id=")
        }
    }
}
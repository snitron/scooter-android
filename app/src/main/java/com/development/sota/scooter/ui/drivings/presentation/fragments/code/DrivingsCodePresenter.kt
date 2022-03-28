package com.development.sota.scooter.ui.drivings.presentation.fragments.code

import com.development.sota.scooter.R
import moxy.MvpPresenter

class DrivingsCodePresenter : MvpPresenter<DrivingsCodeView>() {
    fun onCodeEntered(code: String) {
        val parsedCode = code.toLongOrNull()

        if (parsedCode != null) {
            viewState.sendFoundCodeToDrivings(parsedCode)
            viewState.setLoading(true)
        }
    }

    fun getPinViewColor(state: DrivingsCodePinViewState): Int {
        return when (state) {
            DrivingsCodePinViewState.NONE -> R.color.gray_edit_text
            DrivingsCodePinViewState.GREEN -> R.color.green_edit_text
            DrivingsCodePinViewState.RED -> R.color.red_edit_text
        }
    }

    fun gotResultFromActivity(result: Boolean) {
        viewState.setLoading(false)

        if (result) {
            viewState.setColorOfPinView(DrivingsCodePinViewState.GREEN)
        } else {
            viewState.setColorOfPinView(DrivingsCodePinViewState.RED)
        }
    }
}
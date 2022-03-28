package com.development.sota.scooter.ui.login.presentation.fragments.user_agreement

import com.development.sota.scooter.R
import com.development.sota.scooter.base.BasePresenter
import moxy.MvpPresenter

class LoginUserAgreementPresenter : MvpPresenter<LoginUserAgreement>(), BasePresenter {
    fun onNextClicked(currentIndex: Int) {
        if (currentIndex + 1 <= 3) {
            val index = currentIndex + 1

            if (index == 3) {
                viewState.changeConfirmButtonText(R.string.login_user_agreement_confirm)
            }

            viewState.setViewPagerPage(index)
        } else {
            viewState.closeUserAgreement()
        }
    }

    override fun onDestroyCalled() {}
}
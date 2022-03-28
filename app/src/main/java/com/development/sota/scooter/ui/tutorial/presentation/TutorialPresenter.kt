package com.development.sota.scooter.ui.tutorial.presentation

import android.content.Context
import com.development.sota.scooter.base.BasePresenter
import com.development.sota.scooter.ui.tutorial.domain.TutorialInteractor
import com.development.sota.scooter.ui.tutorial.domain.TutorialInteractorImpl
import moxy.MvpPresenter

class TutorialPresenter(val context: Context) : MvpPresenter<TutorialView>(), BasePresenter {
    private val interactor: TutorialInteractor = TutorialInteractorImpl(this)

    fun onNextButtonClicked(index: Int) {
        if (index == 5) {
            closeTutorial()
        } else {
            viewState.nextPage(index + 1)
        }
    }

    fun onSkipButtonClicked() {
        closeTutorial()
    }

    private fun closeTutorial() {
        interactor.setSuccessfulFlag()
        viewState.finishActivity()
    }

    override fun onDestroyCalled() {}
}
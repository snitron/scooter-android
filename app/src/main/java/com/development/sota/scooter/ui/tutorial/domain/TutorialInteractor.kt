package com.development.sota.scooter.ui.tutorial.domain

import com.development.sota.scooter.base.BaseInteractor
import com.development.sota.scooter.db.SharedPreferencesProvider
import com.development.sota.scooter.ui.tutorial.presentation.TutorialPresenter

interface TutorialInteractor : BaseInteractor {
    fun setSuccessfulFlag()
}

class TutorialInteractorImpl(presenter: TutorialPresenter) : TutorialInteractor {
    private val sharedPreferences =
        SharedPreferencesProvider(presenter.context).sharedPreferences

    override fun setSuccessfulFlag() {
        sharedPreferences.edit().putBoolean("wasTutorial", true).apply()
    }

    override fun disposeRequests() {}
}
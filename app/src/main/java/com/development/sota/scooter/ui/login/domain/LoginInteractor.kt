package com.development.sota.scooter.ui.login.domain

import com.development.sota.scooter.api.LoginRetrofitProvider
import com.development.sota.scooter.base.BaseInteractor
import com.development.sota.scooter.db.SharedPreferencesProvider
import com.development.sota.scooter.ui.login.presentation.LoginPresenter
import com.development.sota.scooter.ui.login.presentation.fragments.code.LoginCodePresenter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers


interface LoginInteractor : BaseInteractor {
    fun sendLoginRequest(phone: String, name: String)
    fun saveCredentials(phone: String, name: String, id: Long)
}

class LoginInteractorImpl(val presenter: LoginPresenter) : LoginInteractor {
    private val compositeDisposable = CompositeDisposable()
    private val sharedPreferences = SharedPreferencesProvider(presenter.context).sharedPreferences

    override fun sendLoginRequest(phone: String, name: String) {
        compositeDisposable.add(
            LoginRetrofitProvider.service
                .clientLogin(phone, name).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = { presenter.gotErrorFromAPI(it.localizedMessage ?: "") },
                    onNext = { presenter.gotCodeAndIDFromAPI(it.code, it.id) })
        )
    }

    override fun saveCredentials(phone: String, name: String, id: Long) {
        sharedPreferences.edit().putString("phone", phone).putString("name", name).putLong("id", id)
            .putBoolean("firstInit", true).apply()
    }

    override fun disposeRequests() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }
}

class LoginCodeInteractorImpl(val presenter: LoginCodePresenter) : LoginInteractor {
    private val compositeDisposable = CompositeDisposable()

    override fun sendLoginRequest(phone: String, name: String) {
        compositeDisposable.add(
            LoginRetrofitProvider.service
                .clientLogin(phone, name).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = { presenter.gotErrorFromAPI() },
                    onNext = { presenter.gotCodeFromAPI(it.code) })
        )
    }

    override fun saveCredentials(phone: String, name: String, id: Long) {}

    override fun disposeRequests() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }
}
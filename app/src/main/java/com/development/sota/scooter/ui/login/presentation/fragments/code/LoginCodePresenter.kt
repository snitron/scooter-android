package com.development.sota.scooter.ui.login.presentation.fragments.code

import android.content.Context
import androidx.core.content.ContextCompat
import com.development.sota.scooter.R
import com.development.sota.scooter.base.BasePresenter
import com.development.sota.scooter.ui.login.domain.LoginCodeInteractorImpl
import com.development.sota.scooter.ui.login.domain.LoginInteractor
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class LoginCodePresenter(
    private val context: Context,
    private var code: Int,
    private val phoneAndName: Pair<String, String>
) : MvpPresenter<LoginCodeView>(), BasePresenter {
    private val interactor: LoginInteractor = LoginCodeInteractorImpl(this)
    private val phoneUtil = PhoneNumberUtil.createInstance(context)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        startTicker()
    }

    fun onPinEntered(pin: String) {
        val pinInt = pin.toIntOrNull()

        if (pinInt == null || pinInt != code) {
            viewState.lightPinView(LoginCodeState.RED)
        } else {
            viewState.lightPinView(LoginCodeState.GREEN)
            viewState.closeFragment(true)
        }
    }

    private fun startTicker() {
        viewState.setButtonRequestAgainVisibility(false)
        viewState.setTickerViewVisibility(true)
        thread {
            var time = 120

            while (time >= 0) {
                val minutes = TimeUnit.SECONDS.toMinutes(time.toLong())
                val seconds = time - minutes * 60

                viewState.setTickerTime(String.format("%02d:%02d", minutes, seconds))

                Thread.sleep(1000)

                time--
            }

            viewState.setTickerViewVisibility(false)
            viewState.setButtonRequestAgainVisibility(true)
        }
    }

    fun getEditTextColor(state: LoginCodeState): Int {
        return when (state) {
            LoginCodeState.NONE -> ContextCompat.getColor(context, R.color.gray_edit_text)
            LoginCodeState.GREEN -> ContextCompat.getColor(context, R.color.green_edit_text)
            LoginCodeState.RED -> ContextCompat.getColor(context, R.color.red_edit_text)
        }
    }

    fun onRequestCodeAgainButtonClicked() {
        viewState.setTickerViewVisibility(false)
        viewState.setProgressBarLoginCodeVisibility(true)
        viewState.setButtonRequestAgainVisibility(false)

        interactor.sendLoginRequest(phoneAndName.first, phoneAndName.second)
    }

    fun gotCodeFromAPI(code: Int) {
        viewState.setProgressBarLoginCodeVisibility(false)

        this.code = code
        startTicker()
    }

    fun gotErrorFromAPI() {
        viewState.setProgressBarLoginCodeVisibility(false)
        viewState.setButtonRequestAgainVisibility(true)
        viewState.setTickerViewVisibility(false)

        code = -1
        viewState.showErrorToast()
    }

    fun getFormattedPhoneString(): String {
        var formattedPhoneNumber = ""

        for (region in phoneUtil.supportedRegions) {
            var isValid: Boolean = phoneUtil.isPossibleNumber(phoneAndName.first, region)
            if (isValid) {
                val number = phoneUtil.parseAndKeepRawInput(phoneAndName.first, region)

                isValid = phoneUtil.isValidNumberForRegion(number, region)

                if (isValid) {
                    formattedPhoneNumber = phoneUtil.formatInOriginalFormat(number, region)
                    break
                }
            }
        }

        return formattedPhoneNumber
    }

    fun closeCodeFragmentByUser() {
        viewState.closeFragment(false)
    }

    override fun onDestroyCalled() {
        interactor.disposeRequests()
    }
}
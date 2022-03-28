package com.development.sota.scooter.ui.login.presentation.fragments.input

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.development.sota.scooter.R
import com.development.sota.scooter.base.BasePresenter
import com.development.sota.scooter.ui.login.presentation.LoginActivityView
import com.jwang123.flagkit.FlagKit
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import moxy.MvpPresenter


class LoginInputPresenter(
    private val context: Context,
    private val loginActivityView: LoginActivityView
) : MvpPresenter<LoginInputView>(), BasePresenter {
    private var countryCode = ""
    private var phoneCandidate = ""
    private var name = ""
    private var agreementConfirmed = false

    private val phoneUtil = PhoneNumberUtil.createInstance(context)
    private val countries = arrayOf(
        "ad", "ae", "af", "ag", "ai", "al", "am", "ao", "ar", "at", "au", "ax", "az", "ba", "bb",
        "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bw", "by",
        "bz", "ca", "caf", "cas", "cd", "ceu", "cf", "cg", "ch", "ch", "ci", "cl", "cm", "cn",
        "cna", "co", "coc", "cr", "csa", "cu", "cv", "cy", "cz", "de", "dj", "dk", "dm", "dz", "ec",
        "ee", "eg", "er", "es", "et", "eu", "fi", "fj", "fm", "fr", "ga", "gb", "gd", "ge", "gh",
        "gm", "gn", "gq", "gr", "gt", "gw", "gy", "hk", "hn", "hr", "ht", "hu", "id", "ie", "il",
        "in", "iq", "ir", "is", "it", "jm", "jo", "jp", "ke", "kg", "kh", "km", "kn", "kp", "kr",
        "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma",
        "mc", "md", "me", "mg", "mk", "ml", "mm", "mn", "mo", "mr", "ms", "mt", "mu", "mv", "mw",
        "mx", "my", "mz", "na", "ne", "ng", "ni", "nl", "no", "np", "nz", "om", "pa", "pe", "pg",
        "ph", "pk", "pl", "pr", "pt", "pw", "py", "qa", "ro", "rs", "ru", "rw", "sa", "sb", "sc",
        "sd", "se", "sg", "si", "sk", "sl", "sm", "sn", "so", "sr", "st", "sv", "sy", "sz", "tc",
        "td", "tg", "th", "tj", "tl", "tm", "tn", "to", "tr", "tt", "tw", "tz", "ua", "ug", "us",
        "uy", "uz", "vc", "ve", "vg", "vn", "ws", "ww", "ye", "za", "zw"
    )


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        onInputsChanged()
    }

    fun onPhoneCodeChanged(currentNumber: String) {
        try {
            var state =
                if (currentNumber.isEmpty() || currentNumber.trim() == "+") LoginInputPhoneState.NONE else LoginInputPhoneState.GREEN
            var anyElement = false

            if (currentNumber.length >= 7) {
                for (region in phoneUtil.supportedRegions) {
                    var isValid: Boolean = phoneUtil.isPossibleNumber(currentNumber, region)
                    if (isValid) {
                        val number = phoneUtil.parse(currentNumber, region)

                        isValid = phoneUtil.isValidNumberForRegion(number, region)

                        if (isValid && countries.contains(region.toLowerCase().trim())) {
                            countryCode = region
                            phoneCandidate = currentNumber

                            viewState.changeFlag(
                                FlagKit.drawableWithFlag(
                                    context,
                                    countryCode.toLowerCase().trim()
                                )
                            )

                            anyElement = true
                            break
                        }
                    } else {
                        countryCode = ""
                        viewState.changeFlag(
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_white_circle,
                                null
                            )!!
                        )
                    }
                }
            }

            if (!anyElement) {
                phoneCandidate = ""
                countryCode = ""
            }

            state = if (anyElement) state else LoginInputPhoneState.RED

            viewState.lightThePhoneView(state)
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: $e")
        }

        onInputsChanged()
    }

    fun onNameChanged(currentName: String) {
        name = currentName

        viewState.lightTheNameView(
            if (currentName.trim()
                    .isEmpty()
            ) LoginInputPhoneState.RED else LoginInputPhoneState.GREEN
        )

        onInputsChanged()
    }

    fun onConfirmAgreementSwitchStateChanged(state: Boolean) {
        agreementConfirmed = state

        onInputsChanged()
    }

    fun onRequestCodeButtonClicked() {
        val isPhoneValid: Boolean = phoneUtil.isPossibleNumber(
            phoneCandidate,
            countryCode
        ) && phoneUtil.isValidNumberForRegion(
            phoneUtil.parse(phoneCandidate, countryCode),
            countryCode
        )
        val isNameValid = name.trim().isNotEmpty()

        if (isPhoneValid && isNameValid && agreementConfirmed) {
            loginActivityView.requestCodeClicked(
                PhoneNumberUtil.normalizeDigitsOnly(phoneCandidate),
                name.trim()
            )
        }
    }

    private fun onInputsChanged() {
        val isPhoneValid: Boolean = phoneUtil.isPossibleNumber(
            phoneCandidate,
            countryCode
        ) && phoneUtil.isValidNumberForRegion(
            phoneUtil.parse(phoneCandidate, countryCode),
            countryCode
        )
        val isNameValid = name.trim().isNotEmpty()

        if (isPhoneValid && isNameValid && agreementConfirmed) {
            viewState.changeButtonRequestCodeState(true, R.string.login_get_code)
        } else {
            if (agreementConfirmed) {
                viewState.changeButtonRequestCodeState(false, R.string.login_get_code)
            } else {
                viewState.changeButtonRequestCodeState(false, R.string.login_agree_and_get_code)
            }
        }
    }

    fun userAgreementNewState(state: Boolean) {
        viewState.setTheUserAgreementSwitchState(state)
    }

    fun userAgreementFragmentRequested() {
        viewState.startUserAgreementFragment()
    }

    fun getEditTextColor(state: LoginInputPhoneState): Int {
        return when (state) {
            LoginInputPhoneState.NONE -> ContextCompat.getColor(context, R.color.gray_edit_text)
            LoginInputPhoneState.GREEN -> ContextCompat.getColor(context, R.color.green_edit_text)
            LoginInputPhoneState.RED -> ContextCompat.getColor(context, R.color.red_edit_text)
        }
    }

    override fun onDestroyCalled() {}
}
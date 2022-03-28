package com.development.sota.scooter.ui.login.presentation.fragments.code

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.development.sota.scooter.R
import com.development.sota.scooter.databinding.FragmentLoginCodeBinding
import com.development.sota.scooter.ui.login.presentation.LoginActivityView
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd


interface LoginCodeView : MvpView {
    @AddToEnd
    fun lightPinView(state: LoginCodeState)

    @AddToEnd
    fun setTickerTime(time: String)

    @AddToEnd
    fun setButtonRequestAgainVisibility(state: Boolean)

    @AddToEnd
    fun setTickerViewVisibility(state: Boolean)

    @AddToEnd
    fun setProgressBarLoginCodeVisibility(state: Boolean)

    @AddToEnd
    fun showErrorToast()

    @AddToEnd
    fun closeFragment(result: Boolean)
}

class LoginCodeFragment(
    private val loginCodeView: LoginActivityView,
    private val code: Int
) : MvpAppCompatFragment(), LoginCodeView {
    private val presenter by moxyPresenter {
        LoginCodePresenter(
            context ?: activity!!.applicationContext,
            code,
            loginCodeView.getPhoneAndName()
        )
    }

    private var _binding: FragmentLoginCodeBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginCodeBinding.inflate(inflater, container, false)

        binding.loginCodePinView.doAfterTextChanged {
            if (it != null && it.length == 4) {
                presenter.onPinEntered(it.toString())
            }
        }

        binding.buttonLoginRequestCodeAgain.setOnClickListener { presenter.onRequestCodeAgainButtonClicked() }
        binding.imageButtonLoginCodeBackButton.setOnClickListener { presenter.closeCodeFragmentByUser() }
        binding.textViewLoginCodeSublabel.text =
            getString(R.string.login_input_code) + " " + presenter.getFormattedPhoneString()

        return binding.root
    }

    override fun lightPinView(state: LoginCodeState) {
        activity?.runOnUiThread {
            binding.loginCodePinView.setLineColor(presenter.getEditTextColor(state))
            binding.loginCodePinView.setTextColor(presenter.getEditTextColor(state))
        }
    }

    override fun setTickerTime(time: String) {
        activity?.runOnUiThread {
            val tickerLabel = getString(com.development.sota.scooter.R.string.login_ticker_text)
            val spannable: Spannable = SpannableString("$tickerLabel $time")

            spannable.setSpan(
                ForegroundColorSpan(Color.GRAY),
                0,
                tickerLabel.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                ForegroundColorSpan(Color.BLACK),
                tickerLabel.length,
                "$tickerLabel $time".length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.textViewLoginCodeTimeTicker.text = spannable
        }
    }

    override fun setButtonRequestAgainVisibility(state: Boolean) {
        activity?.runOnUiThread {
            binding.buttonLoginRequestCodeAgain.visibility =
                if (state) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun setTickerViewVisibility(state: Boolean) {
        activity?.runOnUiThread {
            binding.textViewLoginCodeTimeTicker.visibility =
                if (state) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun setProgressBarLoginCodeVisibility(state: Boolean) {
        activity?.runOnUiThread {
            binding.progressBarLoginCode.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

    override fun showErrorToast() {
        activity?.runOnUiThread {
            Toast.makeText(context!!, R.string.error_api, Toast.LENGTH_SHORT).show()
        }
    }

    override fun closeFragment(result: Boolean) {
        activity?.runOnUiThread {
            loginCodeView.closeCodeFragment(result)
        }
    }

    override fun onDestroy() {
        presenter.onDestroyCalled()
        super.onDestroy()
    }
}

enum class LoginCodeState {
    NONE, GREEN, RED
}
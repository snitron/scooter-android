package com.development.sota.scooter.ui.login.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.development.sota.scooter.MainActivity
import com.development.sota.scooter.R
import com.development.sota.scooter.databinding.ActivityLoginBinding
import com.development.sota.scooter.ui.login.presentation.fragments.code.LoginCodeFragment
import com.development.sota.scooter.ui.login.presentation.fragments.input.LoginInputFragment
import com.development.sota.scooter.ui.login.presentation.fragments.user_agreement.LoginUserAgreementFragment
import moxy.MvpAppCompatActivity
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd

interface LoginView : MvpView {
    @AddToEnd
    fun setFragmentInput()

    @AddToEnd
    fun setFragmentCode(code: Int)

    @AddToEnd
    fun showToastWarning()

    @AddToEnd
    fun hideInputFragment()

    @AddToEnd
    fun setLoginProgressBarVisibility(state: Boolean)

    @AddToEnd
    fun finishActivity()

    @AddToEnd
    fun notifyInputFragmentUserAgreementSuccessState()
}

interface LoginActivityView {
    fun requestCodeClicked(phone: String, name: String)

    fun getPhoneAndName(): Pair<String, String>

    fun closeCodeFragment(result: Boolean)

    fun userAgreementSuccess()

    fun startUserAgreement()
}

class LoginActivity : MvpAppCompatActivity(),
    LoginView,
    LoginActivityView {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private val presenter: LoginPresenter by moxyPresenter { LoginPresenter(this) }
    private var state = LoginState.INPUT

    private var saveInputFragment: MvpAppCompatFragment? = null
    private var saveCodeFragment: MvpAppCompatFragment? = null
    private var saveUserAgreementFragment: MvpAppCompatFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun setFragmentInput() {
        runOnUiThread {
            var first = false

            if (saveInputFragment == null) {
                first = true
                saveInputFragment = LoginInputFragment(this)
            }

            supportFragmentManager.beginTransaction().apply {
                if (saveCodeFragment != null) {
                    detach(saveCodeFragment!!)
                    saveCodeFragment = null
                }

                if (saveUserAgreementFragment != null) {
                    detach(saveUserAgreementFragment!!)
                    saveUserAgreementFragment = null
                }

                if (first) {
                    add(R.id.login_frame, saveInputFragment!!)
                } else {
                    show(saveInputFragment!!)
                }

            }.commitNow()
        }
    }

    override fun setFragmentCode(code: Int) {
        runOnUiThread {
            saveCodeFragment = LoginCodeFragment(this, code)

            supportFragmentManager.beginTransaction().apply {
                add(R.id.login_frame, saveCodeFragment!!)
            }.commitNow()
        }
    }

    override fun showToastWarning() {
        Toast.makeText(this, getString(R.string.error_api), Toast.LENGTH_SHORT).show()
    }

    override fun requestCodeClicked(phone: String, name: String) {
        presenter.onCodeRequested(phone, name)
    }

    override fun hideInputFragment() {
        runOnUiThread {
            supportFragmentManager.beginTransaction()
                .hide(saveInputFragment!!)
                .commitNow()
        }
    }

    override fun setLoginProgressBarVisibility(state: Boolean) {
        runOnUiThread {
            binding.progressBarLogin.visibility = if (state) View.VISIBLE else View.GONE
        }
    }

    override fun getPhoneAndName(): Pair<String, String> {
        return presenter.getPhoneAndName()
    }

    override fun closeCodeFragment(result: Boolean) {
        presenter.onCloseCodeFragment(result)
    }

    override fun finishActivity() {
        runOnUiThread {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun userAgreementSuccess() {
        presenter.userAgreementClosed()
    }

    override fun startUserAgreement() {
        runOnUiThread {
            saveUserAgreementFragment = LoginUserAgreementFragment(this)

            supportFragmentManager.beginTransaction()
                .apply {
                    if (saveInputFragment != null) {
                        hide(saveInputFragment!!)
                    }

                    add(R.id.login_frame, saveUserAgreementFragment!!)
                }.commitNow()
        }
    }

    override fun notifyInputFragmentUserAgreementSuccessState() {
        try {
            (saveInputFragment as LoginInputFragment).gotUpdateForUserAgreement(true)
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onDestroy() {
        presenter.onDestroyCalled()

        super.onDestroy()
    }
}

enum class LoginState {
    INPUT, CODE
}
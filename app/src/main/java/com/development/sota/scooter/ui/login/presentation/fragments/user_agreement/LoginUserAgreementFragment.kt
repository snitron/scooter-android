package com.development.sota.scooter.ui.login.presentation.fragments.user_agreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.development.sota.scooter.databinding.FragmentLoginUserAgreementBinding
import com.development.sota.scooter.ui.login.presentation.LoginActivityView
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd

interface LoginUserAgreement : MvpView {
    @AddToEnd
    fun changeConfirmButtonText(resId: Int)

    @AddToEnd
    fun setViewPagerPage(index: Int)

    @AddToEnd
    fun closeUserAgreement()
}

class LoginUserAgreementFragment(private val loginView: LoginActivityView) : MvpAppCompatFragment(),
    LoginUserAgreement {
    private val presenter by moxyPresenter { LoginUserAgreementPresenter() }

    private var _binding: FragmentLoginUserAgreementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginUserAgreementBinding.inflate(inflater, container, false)

        binding.viewPagerLoginAgreement.isUserInputEnabled = false
        binding.viewPagerLoginAgreement.adapter = LoginUserAgreementAdapter()

        binding.buttonLoginUserAgreementConfirm.setOnClickListener { presenter.onNextClicked(binding.viewPagerLoginAgreement.currentItem) }
        return binding.root
    }

    override fun changeConfirmButtonText(resId: Int) {
        activity?.runOnUiThread {
            binding.buttonLoginUserAgreementConfirm.setText(resId)
        }
    }

    override fun setViewPagerPage(index: Int) {
        activity?.runOnUiThread {
            binding.viewPagerLoginAgreement.currentItem = index
        }
    }

    override fun closeUserAgreement() {
        loginView.userAgreementSuccess()
    }
}
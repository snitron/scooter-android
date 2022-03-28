package com.development.sota.scooter.ui.drivings.presentation.fragments.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.development.sota.scooter.databinding.FragmentDrivingsCodeBinding
import com.development.sota.scooter.ui.drivings.DrivingsActivityView
import com.development.sota.scooter.ui.drivings.DrivingsFragmentView
import com.development.sota.scooter.ui.drivings.DrivingsListFragmentType
import com.development.sota.scooter.ui.map.data.Scooter
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd

interface DrivingsCodeView : MvpView {
    @AddToEnd
    fun sendFoundCodeToDrivings(code: Long)

    @AddToEnd
    fun setLoading(by: Boolean)

    @AddToEnd
    fun setColorOfPinView(state: DrivingsCodePinViewState)
}

class DrivingsCodeFragment(
    val drivingsView: DrivingsActivityView,
    val scooterToAdd: Scooter? = null
) : MvpAppCompatFragment(),
    DrivingsCodeView, DrivingsFragmentView {
    private val presenter by moxyPresenter { DrivingsCodePresenter() }

    private var _binding: FragmentDrivingsCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrivingsCodeBinding.inflate(inflater, container, false)

        binding.drivingsCodePinView.doAfterTextChanged {
            if (it != null && it.length == 4) {
                presenter.onCodeEntered(it.toString())
            }
        }

        binding.imageButtonDrivingsCodeBackButton.setOnClickListener {
            drivingsView.onBackPressedByType(DrivingsListFragmentType.CODE)
        }

        binding.imageButtonDrivingsCodeScannerLantern.setOnClickListener {
            drivingsView.toggleLantern()
        }

        return binding.root
    }

    override fun sendFoundCodeToDrivings(code: Long) {
        activity?.runOnUiThread {
            drivingsView.gotCode(code, this)
        }
    }

    override fun setLoading(by: Boolean) {
        activity?.runOnUiThread {
            if (by) {
                binding.progressBarDrivingsCode.visibility = View.VISIBLE
                binding.drivingsCodePinView.isEnabled = false
            } else {
                binding.progressBarDrivingsCode.visibility = View.GONE
                binding.drivingsCodePinView.isEnabled = true
            }
        }
    }

    override fun setColorOfPinView(state: DrivingsCodePinViewState) {
        activity?.runOnUiThread {
            binding.drivingsCodePinView.setLineColor(
                ContextCompat.getColor(
                    context!!,
                    presenter.getPinViewColor(state)
                )
            )
            binding.drivingsCodePinView.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    presenter.getPinViewColor(state)
                )
            )
        }
    }

    override fun gotResultOfCodeChecking(result: Boolean) {
        activity?.runOnUiThread {
            presenter.gotResultFromActivity(result)
        }
    }
}

enum class DrivingsCodePinViewState {
    NONE, GREEN, RED
}
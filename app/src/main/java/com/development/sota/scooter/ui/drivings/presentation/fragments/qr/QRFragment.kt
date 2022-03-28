package com.development.sota.scooter.ui.drivings.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.development.sota.scooter.databinding.FragmentDrivingsQrBinding
import com.development.sota.scooter.ui.drivings.DrivingsActivityView
import com.development.sota.scooter.ui.drivings.DrivingsFragmentView
import com.development.sota.scooter.ui.drivings.DrivingsListFragmentType
import com.development.sota.scooter.ui.drivings.presentation.fragments.qr.QRPresenter
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd

interface QRView : MvpView {
    @AddToEnd
    fun sendFoundCodeToDrivings(code: Long)

    @AddToEnd
    fun setLoading(by: Boolean)

    @AddToEnd
    fun sendToCodeFragment()
}

class QRFragment(val drivingsView: DrivingsActivityView) : MvpAppCompatFragment(), QRView,
    DrivingsFragmentView {
    private val presenter by moxyPresenter { QRPresenter() }

    private var _binding: FragmentDrivingsQrBinding? = null
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrivingsQrBinding.inflate(inflater, container, false)

        codeScanner = CodeScanner(context ?: activity?.applicationContext!!, binding.qrScanner)

        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS
        codeScanner.isAutoFocusEnabled = true

        codeScanner.setDecodeCallback {
            presenter.getDataFromScanner(it.text)
        }
        codeScanner.setErrorCallback {
            presenter.gotErrorFromScanner()
        }

        codeScanner.startPreview()

        binding.imageButtonQrScannerLantern.setOnClickListener {
            codeScanner.isFlashEnabled = !codeScanner.isFlashEnabled
        }

        binding.imageButtonQrScannerCode.setOnClickListener {
            drivingsView.sendToCodeActivity()
        }

        binding.imageButtonQrScannerBack.setOnClickListener {
            drivingsView.onBackPressedByType(DrivingsListFragmentType.QR)
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
                binding.progressBarDrivingsQr.visibility = View.VISIBLE
                codeScanner.stopPreview()
            } else {
                binding.progressBarDrivingsQr.visibility = View.GONE
                codeScanner.startPreview()
            }
        }
    }

    fun toggleLantern() {
        activity?.runOnUiThread {
            codeScanner.isFlashEnabled = !codeScanner.isFlashEnabled
        }
    }

    override fun sendToCodeFragment() {
        activity?.runOnUiThread {
            codeScanner.stopPreview()

            drivingsView.sendToCodeActivity()
        }
    }

    override fun gotResultOfCodeChecking(result: Boolean) {
        presenter.gotResponseFromActivity(result)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity?.runOnUiThread {
            try {
                codeScanner.startPreview()
            } catch (e: Exception) {
            }
        }
    }

    override fun onDestroy() {
        presenter.onDestroyCalled()
        super.onDestroy()
    }
}
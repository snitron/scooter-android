package com.development.sota.scooter.ui.drivings

import android.os.Bundle
import android.widget.Toast
import com.development.sota.scooter.R
import com.development.sota.scooter.databinding.ActivityDrivingsBinding
import com.development.sota.scooter.ui.drivings.presentation.fragments.QRFragment
import com.development.sota.scooter.ui.drivings.presentation.fragments.code.DrivingsCodeFragment
import com.development.sota.scooter.ui.drivings.presentation.fragments.list.DrivingsListFragment
import moxy.MvpAppCompatActivity
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd

interface DrivingsView : MvpView {
    @AddToEnd
    fun showToast(string: String)

    @AddToEnd
    fun setFragmentByType(type: DrivingsListFragmentType)
}

interface DrivingsActivityView {
    fun gotCode(code: Long, delegate: DrivingsFragmentView)

    fun onBackPressedByType(type: DrivingsListFragmentType)

    fun sendToCodeActivity()

    fun toggleLantern()
}

interface DrivingsFragmentView {
    fun gotResultOfCodeChecking(result: Boolean)
}

class DrivingsActivity : MvpAppCompatActivity(), DrivingsView, DrivingsActivityView {
    private val presenter by moxyPresenter { DrivingsPresenter(this) }

    private var _binding: ActivityDrivingsBinding? = null
    private val binding get() = _binding!!

    private var saveQrFragment: MvpAppCompatFragment? = null
    private var saveCodeFragment: MvpAppCompatFragment? = null
    private var saveListFragment: MvpAppCompatFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDrivingsBinding.inflate(layoutInflater)

        if (intent.getSerializableExtra("aim") == DrivingsStartTarget.QRandCode) {
            presenter.updateFragmentType(DrivingsListFragmentType.QR)
        } else {
            presenter.updateFragmentType(DrivingsListFragmentType.LIST)
        }

        setContentView(binding.root)
    }

    override fun gotCode(code: Long, delegate: DrivingsFragmentView) {
        presenter.testCode(code, delegate)
    }

    override fun showToast(string: String) {
        runOnUiThread {
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
        }
    }

    override fun setFragmentByType(type: DrivingsListFragmentType) {
        runOnUiThread {
            supportFragmentManager.beginTransaction().apply {
                when (type) {
                    DrivingsListFragmentType.QR -> {
                        if (saveListFragment != null) {
                            detach(saveListFragment!!)
                        }

                        if (saveCodeFragment != null) {
                            detach(saveCodeFragment!!)
                        }

                        if (saveQrFragment == null) {
                            saveQrFragment = QRFragment(this@DrivingsActivity)

                            add(R.id.drivings_frame, saveQrFragment!!)
                        } else {
                            show(saveQrFragment!!)
                        }
                    }

                    DrivingsListFragmentType.CODE -> {
                        if (saveListFragment != null) {
                            detach(saveListFragment!!)
                        }

                        hide(saveQrFragment!!)

                        saveCodeFragment = DrivingsCodeFragment(this@DrivingsActivity)

                        add(R.id.drivings_frame, saveCodeFragment!!)
                    }

                    DrivingsListFragmentType.LIST -> {
                        if (saveQrFragment != null) {
                            detach(saveQrFragment!!)
                        }

                        if (saveCodeFragment != null) {
                            detach(saveCodeFragment!!)
                        }

                        if (saveListFragment == null) {
                            saveListFragment = DrivingsListFragment(this@DrivingsActivity)

                            add(R.id.drivings_frame, saveListFragment!!)
                        } else {
                            show(saveQrFragment!!)
                        }
                    }
                }
            }.commitNow()
        }
    }

    override fun onBackPressed() {
        onBackPressedByType(presenter.fragmentType)
    }

    override fun onBackPressedByType(type: DrivingsListFragmentType) {
        runOnUiThread {
            when (type) {
                DrivingsListFragmentType.QR, DrivingsListFragmentType.LIST -> finish()
                DrivingsListFragmentType.CODE -> setFragmentByType(DrivingsListFragmentType.QR)
            }
        }
    }

    override fun sendToCodeActivity() {
        runOnUiThread {
            presenter.returnedFromQRSendToCode()
        }
    }

    override fun toggleLantern() {
        runOnUiThread {
            if (saveQrFragment != null) {
                (saveQrFragment!! as QRFragment).toggleLantern()
            }
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

enum class DrivingsStartTarget(val value: Int) {
    QRandCode(0), DrivingList(1)
}

enum class DrivingsListFragmentType {
    QR, CODE, LIST
}
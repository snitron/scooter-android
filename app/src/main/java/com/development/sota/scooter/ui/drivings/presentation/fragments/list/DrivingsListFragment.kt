package com.development.sota.scooter.ui.drivings.presentation.fragments.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.development.sota.scooter.R
import com.development.sota.scooter.databinding.FragmentDrivingsListBinding
import com.development.sota.scooter.ui.drivings.DrivingsActivityView
import com.development.sota.scooter.ui.drivings.DrivingsListFragmentType
import com.development.sota.scooter.ui.drivings.domain.entities.OrderWithStatus
import com.development.sota.scooter.ui.map.data.RateType
import kotlinx.android.synthetic.main.fragment_drivings_list.*
import moxy.MvpAppCompatFragment
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd
import okhttp3.internal.notifyAll

interface DrivingsListView : MvpView {
    @AddToEnd
    fun showToast(message: String)

    @AddToEnd
    fun setLoading(by: Boolean)

    @AddToEnd
    fun initViewPager2(data: Pair<ArrayList<OrderWithStatus>, ArrayList<OrderWithStatus>>)
}

interface OrderManipulatorDelegate {
    fun cancelOrder(id: Long)

    fun activateOrder(id: Long)

    fun setRateAndActivate(id: Long, type: RateType)
    
    fun closeOrder(id: Long)
}

class DrivingsListFragment(val drivingsView: DrivingsActivityView) : MvpAppCompatFragment(),
    DrivingsListView, OrderManipulatorDelegate {
    private val presenter by moxyPresenter {
        DrivingsListPresenter(
            context ?: activity?.applicationContext!!
        )
    }

    private var _binding: FragmentDrivingsListBinding? = null
    private val binding get() = _binding!!
    private var adapter: DrivingsListViewPager2Adapter? = null

    private var segmentId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrivingsListBinding.inflate(inflater, container, false)

        binding.imageButtonDrivingsListBack.setOnClickListener {
            drivingsView.onBackPressedByType(DrivingsListFragmentType.LIST)
        }

        binding.viewPager2DrivingsList.isUserInputEnabled = false

        binding.buttonDrivingsListActive.setOnClickListener {
            if (segmentId == 1) {
                buttonDrivingsListActive.background =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_white_corner)
                buttonDrivingsListActive.elevation = 4f

                buttonDrivingsListHistory.background =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_gray_segment_corner)
                buttonDrivingsListHistory.elevation = 0f

                segmentId = 0
                binding.viewPager2DrivingsList.currentItem = segmentId
            }
        }

        binding.buttonDrivingsListHistory.setOnClickListener {
            if (segmentId == 0) {
                buttonDrivingsListHistory.background =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_white_corner)
                buttonDrivingsListHistory.elevation = 4f

                buttonDrivingsListActive.background =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_gray_segment_corner)
                buttonDrivingsListActive.elevation = 0f

                segmentId = 1
                binding.viewPager2DrivingsList.currentItem = segmentId
            }
        }

        return binding.root
    }

    override fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun setLoading(by: Boolean) {
        activity?.runOnUiThread {
            if (by) {
                binding.progressBarDrivingsList.visibility = View.VISIBLE
                binding.linnearLayoutSegmentContol.isEnabled = false
                binding.viewPager2DrivingsList.isEnabled = false
            } else {
                binding.progressBarDrivingsList.visibility = View.GONE
                binding.linnearLayoutSegmentContol.isEnabled = true
                binding.viewPager2DrivingsList.isEnabled = true
            }
        }
    }

    override fun initViewPager2(data: Pair<ArrayList<OrderWithStatus>, ArrayList<OrderWithStatus>>) {
        activity?.runOnUiThread {
            adapter = DrivingsListViewPager2Adapter(activity!!, data, this)
            binding.viewPager2DrivingsList.adapter = adapter
            binding.viewPager2DrivingsList.requestTransform()
        }
    }

    override fun cancelOrder(id: Long) {
        presenter.cancelOrder(id)
    }

    override fun activateOrder(id: Long) {
        presenter.activateOrder(id)
    }

    override fun setRateAndActivate(id: Long, type: RateType) {
        presenter.setRateAndActivate(id, type)
    }

    override fun closeOrder(id: Long) {
        presenter.closeOrder(id)
    }

    override fun onDestroy() {
        presenter.onDestroyCalled()
        super.onDestroy()
    }
}

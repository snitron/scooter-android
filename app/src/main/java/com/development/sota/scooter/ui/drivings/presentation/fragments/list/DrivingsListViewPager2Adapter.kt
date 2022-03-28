package com.development.sota.scooter.ui.drivings.presentation.fragments.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import com.development.sota.scooter.R
import com.development.sota.scooter.ui.drivings.domain.entities.OrderWithStatus
import kotlinx.android.synthetic.main.item_drivings_list.view.*


class DrivingsListViewPager2Adapter(
    private val context: Context,
    private val data: Pair<ArrayList<OrderWithStatus>, ArrayList<OrderWithStatus>>,
    private val manipulatorDelegate: OrderManipulatorDelegate
) : RecyclerView.Adapter<DrivingsListViewPager2Adapter.DrivingsListViewPager2ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DrivingsListViewPager2ViewHolder =
        DrivingsListViewPager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_drivings_list, parent, false)
        )

    override fun onBindViewHolder(holder: DrivingsListViewPager2ViewHolder, position: Int) =
        holder.itemView.run {
            recyclerViewItemDrivingsList.layoutManager = LinearLayoutManager(context)
            recyclerViewItemDrivingsList.adapter = if (position == 0) OrdersAdapter(
                data.first,
                context,
                manipulatorDelegate
            ) else FinishedOrderAdapter(data.second, context)
        }

    override fun getItemCount(): Int = 2

    inner class DrivingsListViewPager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

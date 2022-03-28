package com.development.sota.scooter.ui.drivings.presentation.fragments.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.development.sota.scooter.R
import com.development.sota.scooter.ui.drivings.domain.entities.OrderStatus
import com.development.sota.scooter.ui.drivings.domain.entities.OrderWithStatus
import kotlinx.android.synthetic.main.item_finished_order.view.*
import java.text.SimpleDateFormat
import java.util.*

class FinishedOrderAdapter(val data: List<OrderWithStatus>, val context: Context) :
    RecyclerView.Adapter<FinishedOrderAdapter.FinishedOrderViewHolder>() {
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FinishedOrderAdapter.FinishedOrderViewHolder = FinishedOrderViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_finished_order, parent, false)
    )

    override fun onBindViewHolder(
        holder: FinishedOrderAdapter.FinishedOrderViewHolder,
        position: Int
    ) =
        holder.itemView.run {
            textViewItemFinishedOrderDate.text =
                dateFormatter.format(data[position].order.parseStartTime())
            textViewItemFinishedOrderAmount.text =
                String.format("%.2f", data[position].order.cost).plus(" ₽")

            if (data[position].order.status == OrderStatus.CANCELED.value) {
                textViewItemFinishedDelta.text = context.getString(R.string.drivings_cancelled)
            }
        }

    override fun getItemCount(): Int = data.size

    inner class FinishedOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
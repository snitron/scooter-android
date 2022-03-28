package com.development.sota.scooter.ui.login.presentation.fragments.user_agreement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.development.sota.scooter.R
import kotlinx.android.synthetic.main.item_agreement.view.*

class LoginUserAgreementAdapter :
    RecyclerView.Adapter<LoginUserAgreementAdapter.LoginUserAgreementViewHolder>() {
    private val data = arrayListOf(
        Pair(R.string.privacy_policy_label, R.string.privacy_policy_data),
        Pair(R.string.user_agreement_label, R.string.user_agreement_data),
        Pair(R.string.accession_agreement_label, R.string.accession_agreement_data),
        Pair(R.string.refund_conditions_label, R.string.refund_conditions_data)
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoginUserAgreementViewHolder = LoginUserAgreementViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_agreement, parent, false)
    )

    override fun onBindViewHolder(holder: LoginUserAgreementViewHolder, position: Int) =
        holder.run {
            holder.itemView.textViewLoginAgreementLabel.setText(data[position].first)
            holder.itemView.textViewLoginAgreementData.setText(data[position].second)
        }

    override fun getItemCount(): Int = data.size


    inner class LoginUserAgreementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
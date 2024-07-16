package com.penguinstudios.tradeguardian.ui.trades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.data.model.getFormattedDateCreated
import com.penguinstudios.tradeguardian.data.model.getFormattedItemPrice
import com.penguinstudios.tradeguardian.data.model.ContractStatus
import com.penguinstudios.tradeguardian.data.model.Trade

class TradesAdapter(
    private val list: List<Trade>,
    private val callback: Callback,
) : RecyclerView.Adapter<TradesAdapter.ViewHolder>() {

    private var greenColor: Int = 0
    private var yellowColor: Int = 0
    private var redColor: Int = 0

    interface Callback {
        fun onTradeClick(adapterPosition: Int)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        greenColor = ContextCompat.getColor(recyclerView.context, R.color.green_400)
        yellowColor = ContextCompat.getColor(recyclerView.context, R.color.yellow_400)
        redColor = ContextCompat.getColor(recyclerView.context, R.color.red_400)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_trade, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.tvStatus.text = ContractStatus.getStatusById(list[i].contractStatusId).statusName
        holder.tvContractAddress.text = list[i].contractAddress
        holder.tvDateCreated.text = list[i].getFormattedDateCreated()
        holder.tvItemPrice.text = list[i].getFormattedItemPrice()

        val statusColor = when (ContractStatus.getStatusById(list[i].contractStatusId)) {
            ContractStatus.AWAITING_DEPOSIT, ContractStatus.AWAITING_DELIVERY, ContractStatus.ITEM_SENT -> yellowColor
            ContractStatus.ITEM_RECEIVED, ContractStatus.SETTLED -> greenColor
            ContractStatus.ITEM_INCORRECT, ContractStatus.TRADE_CANCELED -> redColor
        }
        holder.tvStatus.setTextColor(statusColor)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tvContractAddress: TextView = itemView.findViewById(R.id.tv_contract_address)
        val tvDateCreated: TextView = itemView.findViewById(R.id.tv_date_created)
        val tvItemPrice: TextView = itemView.findViewById(R.id.tv_item_price)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            callback.onTradeClick(adapterPosition)
        }
    }
}
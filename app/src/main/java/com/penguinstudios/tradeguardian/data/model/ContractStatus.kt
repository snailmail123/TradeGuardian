package com.penguinstudios.tradeguardian.data.model

import com.penguinstudios.tradeguardian.R

enum class ContractStatus(val id: Int, val statusName: String, val color: Int) {
    AWAITING_DEPOSIT(0, "Awaiting Deposit", R.color.yellow_400),
    AWAITING_DELIVERY(1, "Awaiting Delivery", R.color.yellow_400),
    ITEM_SENT(2, "Item Sent", R.color.yellow_400),
    ITEM_INCORRECT(3, "Item Incorrect", R.color.red_400),
    ITEM_RECEIVED(4, "Item Received", R.color.green_400),
    SETTLED(5, "Settled", R.color.green_400),
    TRADE_CANCELED(6, "Canceled", R.color.red_400);

    companion object {
        fun getStatusById(id: Int): ContractStatus {
            return ContractStatus.values().firstOrNull { it.id == id }
                ?: throw IllegalStateException("Invalid id: $id")
        }
    }
}

package com.penguinstudios.tradeguardian.ui.trades

sealed class TradesUIState {
    object SuccessGetTrades : TradesUIState()
    object NoTrades : TradesUIState()
    data class DeleteTrade(val adapterPosition: Int) : TradesUIState()
    data class UpdateTrade(val adapterPosition: Int) : TradesUIState()
    data class Error(val message: String) : TradesUIState()
}
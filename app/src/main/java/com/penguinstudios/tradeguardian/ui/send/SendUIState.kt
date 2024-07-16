package com.penguinstudios.tradeguardian.ui.send

sealed class SendUIState {
    data class SuccessfulSend(
        val userWalletAddress: String,
        val txHash: String,
        val sentToAddress: String,
        val formattedAmountSent: String,
        val formattedGasUsed: String
    ) : SendUIState()

    object ShowProgressSend : SendUIState()
    object HideProgressSend : SendUIState()
    data class Error(val message: String) : SendUIState()
}
package com.penguinstudios.tradeguardian.ui.wallet

sealed class WalletUIState {
    data class SuccessGetBalance(
        val walletAddress: String,
        val walletBalance: String
    ) : WalletUIState()

    object ShowProgressWalletBalance : WalletUIState()
    object HideProgressWalletBalance : WalletUIState()
    object SuccessExportTrade : WalletUIState()

    data class SetSpinnerSelectedNetwork(val position: Int) : WalletUIState()

    data class Error(val message: String) : WalletUIState()
}
package com.penguinstudios.tradeguardian.ui.addtrade

sealed class AddTradeUIState {
    object SuccessAddTrade : AddTradeUIState()
    object ShowProgressAddTrade: AddTradeUIState()
    object HideProgressAddTrade: AddTradeUIState()
    data class Error(val message: String) : AddTradeUIState()
}
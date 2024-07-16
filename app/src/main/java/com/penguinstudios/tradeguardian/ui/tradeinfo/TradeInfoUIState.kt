package com.penguinstudios.tradeguardian.ui.tradeinfo

import com.penguinstudios.tradeguardian.data.model.Trade

sealed class TradeInfoUIState {
    data class SuccessDeposit(
        val contractAddress: String,
        val txHash: String,
        val formattedDepositAmount: String,
        val formattedGasUsed: String
    ) : TradeInfoUIState()

    object ShowProgressDeposit : TradeInfoUIState()
    object HideProgressDeposit : TradeInfoUIState()

    data class SuccessChangeDeliveryState(
        val contractAddress: String,
        val txHash: String,
        val formattedDeliveryState: String,
        val formattedGasUsed: String
    ) : TradeInfoUIState()

    object ShowItemDeliveryProgress : TradeInfoUIState()
    object HideItemDeliveryProgress : TradeInfoUIState()

    object ShowCancelingTradeProgress : TradeInfoUIState()
    object HideCancelingTradeProgress : TradeInfoUIState()

    object ShowRequestingSettleProgress : TradeInfoUIState()
    object HideRequestingSettleProgress : TradeInfoUIState()

    data class UpdateSellerDepositStatus(val status: String, val hasDeposited: Boolean) :
        TradeInfoUIState()

    data class UpdateBuyerDepositStatus(val status: String, val hasDeposited: Boolean) :
        TradeInfoUIState()

    object ShowDepositBtn : TradeInfoUIState()
    object HideDepositBtn : TradeInfoUIState()

    data class UpdateSellerDeliveryStatus(val status: String, val isDelivered: Boolean) :
        TradeInfoUIState()

    data class UpdateBuyerReceivedStatus(val status: String, val isDelivered: Boolean) :
        TradeInfoUIState()

    object ShowSuccessfulTradeStatus : TradeInfoUIState()
    object ShowIncorrectItemTradeStatus : TradeInfoUIState()
    object ShowSettledTradeStatus : TradeInfoUIState()

    object ShowBuyerReceivedBtns : TradeInfoUIState()
    object HideBuyerReceivedBtns : TradeInfoUIState()

    object ShowSellerDeliveredBtn : TradeInfoUIState()
    object HideSellerDeliveredBtn : TradeInfoUIState()

    data class UpdateSellerPayout(val status: String) : TradeInfoUIState()
    data class UpdateSellerReturnDepositStatus(val status: String) : TradeInfoUIState()
    data class UpdateBuyerReturnDepositStatus(val status: String) : TradeInfoUIState()
    data class UpdateFeePerParty(val status: String) : TradeInfoUIState()

    data class UpdateSellerSettleStatus(val hasRequestedToSettle: Boolean) : TradeInfoUIState()
    data class UpdateBuyerSettleStatus(val hasRequestedToSettle: Boolean) : TradeInfoUIState()

    object SetStepIndicatorStepOne : TradeInfoUIState()
    object SetStepIndicatorStepTwo : TradeInfoUIState()
    data class SetStepIndicatorStepThree(val showSettleStatus: Boolean) : TradeInfoUIState()

    data class IncorrectItem(val status: String) : TradeInfoUIState()

    data class SuccessDeleteTradeNoReceipt(val contractAddress: String) : TradeInfoUIState()

    data class SuccessDeleteWithReceipt(
        val txHash: String,
        val contractAddress: String,
        val formattedAmountReturned: String,
        val formattedGasUsed: String
    ) : TradeInfoUIState()

    data class SuccessSettle(
        val contractAddress: String,
        val title: String,
        val status: String,
        val txHash: String,
        val formattedGasCost: String
    ) : TradeInfoUIState()

    object ShowStepIndicatorProgress : TradeInfoUIState()
    object HideStepIndicatorProgress : TradeInfoUIState()
    object HideSwipeRefreshProgress : TradeInfoUIState()

    data class SuccessUpdateTradeStatus(val trade: Trade) : TradeInfoUIState()

    data class Error(val message: String) : TradeInfoUIState()
}
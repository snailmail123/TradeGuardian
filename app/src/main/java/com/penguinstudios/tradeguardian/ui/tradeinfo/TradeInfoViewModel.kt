package com.penguinstudios.tradeguardian.ui.tradeinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.LocalRepository
import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.model.ContractStatus
import com.penguinstudios.tradeguardian.data.model.Trade
import com.penguinstudios.tradeguardian.data.model.UserRole
import com.penguinstudios.tradeguardian.data.model.counterPartyRole
import com.penguinstudios.tradeguardian.data.model.getBuyerDepositAmount
import com.penguinstudios.tradeguardian.data.model.getFormattedAmountReturnedToBuyer
import com.penguinstudios.tradeguardian.data.model.getFormattedAmountReturnedToSeller
import com.penguinstudios.tradeguardian.data.model.getFormattedBuyerDepositAmount
import com.penguinstudios.tradeguardian.data.model.getFormattedItemPrice
import com.penguinstudios.tradeguardian.data.model.getFormattedPercentFeePerParty
import com.penguinstudios.tradeguardian.data.model.getFormattedSellerDepositAmount
import com.penguinstudios.tradeguardian.data.model.getSellerDepositAmount
import com.penguinstudios.tradeguardian.data.model.network
import com.penguinstudios.tradeguardian.data.model.networkTokenName
import com.penguinstudios.tradeguardian.data.model.userRole
import com.penguinstudios.tradeguardian.data.usecase.CancelTradeUseCase
import com.penguinstudios.tradeguardian.data.usecase.ContractInfoUseCase
import com.penguinstudios.tradeguardian.data.usecase.DepositUseCase
import com.penguinstudios.tradeguardian.data.usecase.SettleUseCase
import com.penguinstudios.tradeguardian.data.usecase.UpdateItemStateUseCase
import com.penguinstudios.tradeguardian.util.WalletUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TradeInfoViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val depositUseCase: DepositUseCase,
    private val cancelTradeUseCase: CancelTradeUseCase,
    private val updateItemStateUseCase: UpdateItemStateUseCase,
    private val settleUseCase: SettleUseCase,
    private val contractInfoUseCase: ContractInfoUseCase
) : ViewModel() {

    private val _uiState = MutableSharedFlow<TradeInfoUIState>()
    val uiState = _uiState.asSharedFlow()
    lateinit var trade: Trade

    fun loadTrade(trade: Trade) {
        this.trade = trade
    }

    fun deposit() {
        viewModelScope.launch {
            try {
                _uiState.emit(TradeInfoUIState.ShowProgressDeposit)

                if (contractInfoUseCase.hasDeposited(
                        trade.contractAddress,
                        walletRepository.credentials.address
                    )
                ) {
                    _uiState.emit(TradeInfoUIState.HideProgressDeposit)
                    _uiState.emit(TradeInfoUIState.Error("You have already deposited into the trade"))
                    return@launch
                }

                val gasPrice = remoteRepository.estimateGasPrice().gasPrice

                val txReceipt = if (trade.userRole == UserRole.SELLER) {
                    val sellerDepositGasLimit = depositUseCase.estimateSellerDepositGasLimit(
                        trade.contractAddress,
                        trade.itemPriceWei.toBigInteger()
                    ).amountUsed

                    depositUseCase.sellerDeposit(
                        trade.contractAddress,
                        trade.getSellerDepositAmount(),
                        gasPrice,
                        sellerDepositGasLimit
                    )
                } else {
                    val buyerDepositGasLimit = depositUseCase.estimateBuyerDepositGasLimit(
                        trade.contractAddress,
                        trade.itemPriceWei.toBigInteger()
                    ).amountUsed

                    depositUseCase.buyerDeposit(
                        trade.contractAddress,
                        trade.getBuyerDepositAmount(),
                        gasPrice,
                        buyerDepositGasLimit
                    )
                }

                val formattedDepositAmount = if (trade.userRole == UserRole.SELLER) {
                    trade.getFormattedSellerDepositAmount()
                } else {
                    trade.getFormattedBuyerDepositAmount()
                }

                _uiState.emit(TradeInfoUIState.HideProgressDeposit)

                val formattedGasCost = WalletUtil.weiToNetworkToken(
                    txReceipt.gasUsed.multiply(gasPrice),
                    trade.network
                )

                _uiState.emit(
                    TradeInfoUIState.SuccessDeposit(
                        //Deploying contract will return the TxReceipt with contract address
                        //However subsequent calls will return a null contract address in the TxReceipt
                        trade.contractAddress,
                        txReceipt.transactionHash,
                        formattedDepositAmount,
                        formattedGasCost
                    )
                )

            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradeInfoUIState.HideProgressDeposit)
                _uiState.emit(TradeInfoUIState.Error(e.message.toString()))
            }
        }
    }

    fun onCancelTrade() {
        viewModelScope.launch {
            try {
                if (System.currentTimeMillis() < trade.withdrawEligibilityDate) {
                    _uiState.emit(TradeInfoUIState.Error("Contract cancellation requires a minimum two-hour wait after creation"))
                    return@launch
                }

                if (contractInfoUseCase.getContractStatus(trade.contractAddress) != ContractStatus.AWAITING_DEPOSIT) {
                    _uiState.emit(TradeInfoUIState.Error("Trade cannot be canceled"))
                    return@launch
                }

                _uiState.emit(TradeInfoUIState.ShowCancelingTradeProgress)

                val gasPrice = remoteRepository.estimateGasPrice().gasPrice
                val gasLimit =
                    cancelTradeUseCase.estimateCancelTradeGasLimit(trade.contractAddress).amountUsed

                val txReceipt =
                    cancelTradeUseCase.cancelTrade(trade.contractAddress, gasPrice, gasLimit)
                localRepository.deleteTrade(trade.contractAddress)

                if (txReceipt == null) {
                    _uiState.emit(TradeInfoUIState.SuccessDeleteTradeNoReceipt(trade.contractAddress))
                } else {
                    val formattedGasCost = WalletUtil.weiToNetworkToken(
                        txReceipt.gasUsed.multiply(gasPrice),
                        trade.network
                    )

                    val formattedAmountReturned = if (trade.userRole == UserRole.SELLER) {
                        trade.getFormattedSellerDepositAmount()
                    } else {
                        trade.getFormattedBuyerDepositAmount()
                    }

                    _uiState.emit(
                        TradeInfoUIState.SuccessDeleteWithReceipt(
                            txReceipt.transactionHash,
                            trade.contractAddress,
                            formattedAmountReturned,
                            formattedGasCost
                        )
                    )
                }

                _uiState.emit(TradeInfoUIState.HideCancelingTradeProgress)
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradeInfoUIState.HideCancelingTradeProgress)
                _uiState.emit(TradeInfoUIState.Error(e.message.toString()))
            }
        }
    }

    fun settle() {
        viewModelScope.launch {
            try {
                val contractStatus = contractInfoUseCase.getContractStatus(trade.contractAddress)

                if (contractStatus == ContractStatus.SETTLED) {
                    _uiState.emit(TradeInfoUIState.Error("The trade has already been settled"))
                    return@launch
                }

                if (contractStatus != ContractStatus.ITEM_INCORRECT) {
                    _uiState.emit(TradeInfoUIState.Error("Users can only settle if item is incorrect"))
                    return@launch
                }

                _uiState.emit(TradeInfoUIState.ShowRequestingSettleProgress)

                val hasBuyerSettled = settleUseCase.hasBuyerSettled(trade.contractAddress)
                val hasSellerSettled = settleUseCase.hasSellerSettled(trade.contractAddress)

                val hasUserSettled = when (trade.userRole) {
                    UserRole.SELLER -> hasSellerSettled
                    UserRole.BUYER -> hasBuyerSettled
                }

                if (hasUserSettled) {
                    _uiState.emit(TradeInfoUIState.Error("You have already settled"))
                    _uiState.emit(TradeInfoUIState.HideRequestingSettleProgress)
                    return@launch
                }

                val gasPrice = remoteRepository.estimateGasPrice().gasPrice
                val gasLimit =
                    settleUseCase.estimateSettleGasLimit(trade.contractAddress).amountUsed

                val txReceipt = settleUseCase.settle(trade.contractAddress, gasPrice, gasLimit)

                val formattedGasCost = WalletUtil.weiToNetworkToken(
                    txReceipt.gasUsed.multiply(gasPrice),
                    trade.network
                )

                _uiState.emit(TradeInfoUIState.HideRequestingSettleProgress)

                //Uses the counter party's role to determine which message to display in dialog
                when (trade.counterPartyRole) {
                    UserRole.SELLER -> {
                        val depositAmountMessage = trade.getFormattedBuyerDepositAmount()
                        emitSuccessSettleMessage(
                            hasSellerSettled,
                            depositAmountMessage,
                            txReceipt.transactionHash,
                            formattedGasCost
                        )
                    }

                    UserRole.BUYER -> {
                        val depositAmountMessage = trade.getFormattedSellerDepositAmount()
                        emitSuccessSettleMessage(
                            hasBuyerSettled,
                            depositAmountMessage,
                            txReceipt.transactionHash,
                            formattedGasCost
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradeInfoUIState.HideRequestingSettleProgress)
                _uiState.emit(TradeInfoUIState.Error(e.message.toString()))
            }
        }
    }

    private suspend fun emitSuccessSettleMessage(
        hasCounterPartySettledBeforeYou: Boolean,
        formattedDepositAmount: String,
        txHash: String,
        formattedGasCost: String
    ) {

        val title = if (hasCounterPartySettledBeforeYou) {
            "Settlement Complete"
        } else {
            "Settlement Initiated"
        }

        val settlementDescription = if (hasCounterPartySettledBeforeYou) {
            "The trade has been settled. Your deposit of $formattedDepositAmount has been returned."
        } else {
            "You have requested to settle. Awaiting action from counterparty."
        }

        _uiState.emit(
            TradeInfoUIState.SuccessSettle(
                trade.contractAddress,
                title,
                settlementDescription,
                txHash,
                formattedGasCost
            )
        )
    }

    fun setTradeInfo(isLoadedFirstTime: Boolean) {
        viewModelScope.launch {
            try {
                if (isLoadedFirstTime) {
                    _uiState.emit(TradeInfoUIState.ShowStepIndicatorProgress)
                }

                val contractStatus = contractInfoUseCase.getContractStatus(trade.contractAddress)

                if (trade.contractStatusId != contractStatus.id) {
                    val updatedTrade = trade.copy(contractStatusId = contractStatus.id)
                    localRepository.updateTrade(updatedTrade)
                    _uiState.emit(TradeInfoUIState.SuccessUpdateTradeStatus(updatedTrade))
                }

                val hasUserDeposited = contractInfoUseCase.hasDeposited(
                    trade.contractAddress, trade.userWalletAddress
                )

                val hasCounterPartyDeposited = contractInfoUseCase.hasDeposited(
                    trade.contractAddress, trade.counterPartyWalletAddress
                )

                setDepositStatus(
                    contractStatus,
                    hasUserDeposited,
                    trade.userRole
                )

                setDepositStatus(
                    contractStatus,
                    hasCounterPartyDeposited,
                    trade.counterPartyRole
                )

                setDepositBtnState(contractStatus, hasUserDeposited)
                setAwaitingDeliveryBtnState(contractStatus)
                setItemDeliveryStatus(contractStatus)
                setReturnDepositStatus(contractStatus)
                setTradeStatus(contractStatus)
                setSettleStatus(contractStatus)

                //Call last so the text is set before showing layout
                setCurrentStepIndicator(contractStatus)

            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradeInfoUIState.Error(e.message.toString()))
            } finally {
                _uiState.emit(TradeInfoUIState.HideStepIndicatorProgress)
                _uiState.emit(TradeInfoUIState.HideSwipeRefreshProgress)
            }
        }
    }

    private suspend fun setSettleStatus(contractStatus: ContractStatus) {
        if (contractStatus == ContractStatus.ITEM_INCORRECT || contractStatus == ContractStatus.SETTLED) {
            coroutineScope {
                val hasSellerSettled =
                    async { settleUseCase.hasSellerSettled(trade.contractAddress) }
                val hasBuyerSettled =
                    async { settleUseCase.hasBuyerSettled(trade.contractAddress) }

                _uiState.emit(TradeInfoUIState.UpdateSellerSettleStatus(hasSellerSettled.await()))
                _uiState.emit(TradeInfoUIState.UpdateBuyerSettleStatus(hasBuyerSettled.await()))
            }
        }
    }

    private suspend fun setReturnDepositStatus(contractStatus: ContractStatus) {
        if (contractStatus == ContractStatus.ITEM_RECEIVED) {
            val formattedSellerReturnAmount = trade.getFormattedAmountReturnedToSeller()
            val formattedBuyerReturnAmount = trade.getFormattedAmountReturnedToBuyer()
            val payoutStatus = "Buyer paid ${trade.getFormattedItemPrice()} to seller"
            val sellerStatus = "$formattedSellerReturnAmount returned to seller"
            val buyerStatus = "$formattedBuyerReturnAmount returned to buyer"
            val feeStatus = "-${trade.getFormattedPercentFeePerParty()} fee per party"

            _uiState.emit(TradeInfoUIState.UpdateSellerPayout(payoutStatus))
            _uiState.emit(TradeInfoUIState.UpdateSellerReturnDepositStatus(sellerStatus))
            _uiState.emit(TradeInfoUIState.UpdateBuyerReturnDepositStatus(buyerStatus))
            _uiState.emit(TradeInfoUIState.UpdateFeePerParty(feeStatus))
        }
    }

    private suspend fun setTradeStatus(contractStatus: ContractStatus) {
        when (contractStatus) {
            ContractStatus.ITEM_RECEIVED -> {
                _uiState.emit(TradeInfoUIState.ShowSuccessfulTradeStatus)
            }

            ContractStatus.ITEM_INCORRECT -> {
                _uiState.emit(TradeInfoUIState.ShowIncorrectItemTradeStatus)
            }

            ContractStatus.SETTLED -> {
                _uiState.emit(TradeInfoUIState.ShowSettledTradeStatus)
            }

            else -> {}
        }
    }

    // @formatter:off
    private suspend fun setItemDeliveryStatus(contractStatus: ContractStatus) {
        when (contractStatus) {
            ContractStatus.AWAITING_DELIVERY -> {
                _uiState.emit(TradeInfoUIState.UpdateSellerDeliveryStatus("Seller has not marked item as delivered", false))
                _uiState.emit(TradeInfoUIState.UpdateBuyerReceivedStatus("Buyer has not updated received status", false))
            }

            ContractStatus.ITEM_SENT -> {
                _uiState.emit(TradeInfoUIState.UpdateSellerDeliveryStatus("Seller has marked item as delivered", true))
                _uiState.emit(TradeInfoUIState.UpdateBuyerReceivedStatus("Buyer has not updated received status", false))
            }

            ContractStatus.ITEM_RECEIVED -> {
                _uiState.emit(TradeInfoUIState.UpdateSellerDeliveryStatus("Seller has marked item as delivered", true))
                _uiState.emit(TradeInfoUIState.UpdateBuyerReceivedStatus("Buyer has received correct item", true))
            }

            ContractStatus.ITEM_INCORRECT -> {
                _uiState.emit(TradeInfoUIState.UpdateSellerDeliveryStatus("Seller has marked item as delivered", true))
                _uiState.emit(TradeInfoUIState.IncorrectItem("Buyer has incorrect item or no item"))
            }

            ContractStatus.SETTLED -> {
                _uiState.emit(TradeInfoUIState.UpdateSellerDeliveryStatus("Seller has marked item as delivered", true))
                _uiState.emit(TradeInfoUIState.IncorrectItem("Buyer has incorrect item or no item"))
            }

            else -> {}
        }
    }
    // @formatter:on

    private suspend fun setAwaitingDeliveryBtnState(
        contractStatus: ContractStatus
    ) {
        when (contractStatus) {
            ContractStatus.AWAITING_DELIVERY -> {
                if (trade.userRole == UserRole.SELLER) {
                    _uiState.emit(TradeInfoUIState.ShowSellerDeliveredBtn)
                }
            }

            ContractStatus.ITEM_SENT -> {
                _uiState.emit(TradeInfoUIState.HideSellerDeliveredBtn)

                if (trade.userRole == UserRole.BUYER) {
                    _uiState.emit(TradeInfoUIState.ShowBuyerReceivedBtns)
                }
            }

            else -> {
                _uiState.emit(TradeInfoUIState.HideBuyerReceivedBtns)
            }
        }
    }

    // @formatter:off
    private suspend fun setDepositStatus(
        contractStatus: ContractStatus,
        hasDeposited: Boolean,
        userRole: UserRole
    ) {
        if (contractStatus == ContractStatus.AWAITING_DEPOSIT) {
            if (hasDeposited) {
                when (userRole) {
                    UserRole.SELLER -> {
                        val formattedSellerDepositAmount = trade.getFormattedSellerDepositAmount()
                        val depositStatus = "Seller has deposited $formattedSellerDepositAmount"
                        _uiState.emit(TradeInfoUIState.UpdateSellerDepositStatus(depositStatus, true))
                    }

                    UserRole.BUYER -> {
                        val formattedBuyerDepositAmount = trade.getFormattedBuyerDepositAmount()
                        val depositStatus = "Buyer has deposited $formattedBuyerDepositAmount"
                        _uiState.emit(TradeInfoUIState.UpdateBuyerDepositStatus(depositStatus, true))
                    }
                }
            } else {
                when (userRole) {
                    UserRole.SELLER -> {
                        _uiState.emit(TradeInfoUIState.UpdateSellerDepositStatus("Seller has not deposited funds", false))
                    }

                    UserRole.BUYER -> {
                        _uiState.emit(TradeInfoUIState.UpdateBuyerDepositStatus("Buyer has not deposited funds", false))
                    }
                }
            }

        } else {
            //Has already deposited
            when (userRole) {
                UserRole.SELLER -> {
                    val formattedSellerDepositAmount = trade.getFormattedSellerDepositAmount()
                    val depositStatus = "Seller has deposited $formattedSellerDepositAmount"
                    _uiState.emit(TradeInfoUIState.UpdateSellerDepositStatus(depositStatus, true))
                }

                UserRole.BUYER -> {
                    val formattedBuyerDepositAmount = trade.getFormattedBuyerDepositAmount()
                    val depositStatus = "Buyer has deposited $formattedBuyerDepositAmount"
                    _uiState.emit(TradeInfoUIState.UpdateBuyerDepositStatus(depositStatus, true))
                }
            }
        }
    }
    // @formatter:on

    private suspend fun setDepositBtnState(contractStatus: ContractStatus, hasDeposited: Boolean) {
        //Any other state means both users have already deposited
        if (contractStatus == ContractStatus.AWAITING_DEPOSIT) {
            if (hasDeposited) {
                _uiState.emit(TradeInfoUIState.HideDepositBtn)
            } else {
                _uiState.emit(TradeInfoUIState.ShowDepositBtn)
            }
        } else {
            _uiState.emit(TradeInfoUIState.HideDepositBtn)
        }
    }

    private suspend fun setCurrentStepIndicator(contractStatus: ContractStatus) {
        when (contractStatus) {
            ContractStatus.AWAITING_DEPOSIT -> {
                _uiState.emit(TradeInfoUIState.SetStepIndicatorStepOne)
            }

            ContractStatus.AWAITING_DELIVERY, ContractStatus.ITEM_SENT -> {
                _uiState.emit(TradeInfoUIState.SetStepIndicatorStepTwo)
            }

            ContractStatus.ITEM_RECEIVED -> {
                _uiState.emit(TradeInfoUIState.SetStepIndicatorStepThree(false))
            }

            ContractStatus.ITEM_INCORRECT, ContractStatus.SETTLED -> {
                _uiState.emit(TradeInfoUIState.SetStepIndicatorStepThree(true))
            }

            else -> {}
        }
    }

    fun correctItemReceived() {
        viewModelScope.launch {
            try {
                _uiState.emit(TradeInfoUIState.ShowItemDeliveryProgress)

                val gasPrice = remoteRepository.estimateGasPrice().gasPrice

                val gasLimit =
                    updateItemStateUseCase.estimateCorrectItemGasLimit(trade.contractAddress).amountUsed

                val txReceipt = updateItemStateUseCase.correctItemReceived(
                    trade.contractAddress,
                    gasPrice,
                    gasLimit
                )

                _uiState.emit(TradeInfoUIState.HideItemDeliveryProgress)

                val formattedGasCost = WalletUtil.weiToNetworkToken(
                    txReceipt.gasUsed.multiply(gasPrice),
                    trade.network
                )

                _uiState.emit(
                    TradeInfoUIState.SuccessChangeDeliveryState(
                        trade.contractAddress,
                        txReceipt.transactionHash,
                        ContractStatus.ITEM_RECEIVED.statusName,
                        formattedGasCost
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradeInfoUIState.HideItemDeliveryProgress)
                _uiState.emit(TradeInfoUIState.Error(e.message.toString()))
            }
        }

    }

    fun incorrectItemReceived() {
        viewModelScope.launch {
            try {
                _uiState.emit(TradeInfoUIState.ShowItemDeliveryProgress)

                val gasPrice = remoteRepository.estimateGasPrice().gasPrice

                val gasLimit =
                    updateItemStateUseCase.estimateIncorrectItemGasLimit(trade.contractAddress).amountUsed

                val txReceipt = updateItemStateUseCase.incorrectItemReceived(
                    trade.contractAddress,
                    gasPrice,
                    gasLimit
                )

                _uiState.emit(TradeInfoUIState.HideItemDeliveryProgress)

                val formattedGasCost = WalletUtil.weiToNetworkToken(
                    txReceipt.gasUsed.multiply(gasPrice),
                    trade.network
                )

                _uiState.emit(
                    TradeInfoUIState.SuccessChangeDeliveryState(
                        trade.contractAddress,
                        txReceipt.transactionHash,
                        ContractStatus.ITEM_INCORRECT.statusName,
                        formattedGasCost
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradeInfoUIState.HideItemDeliveryProgress)
                _uiState.emit(TradeInfoUIState.Error(e.message.toString()))
            }
        }
    }

    fun itemDelivered() {
        viewModelScope.launch {
            try {
                _uiState.emit(TradeInfoUIState.ShowItemDeliveryProgress)

                val gasPrice = remoteRepository.estimateGasPrice().gasPrice

                val gasLimit =
                    updateItemStateUseCase.estimateItemDeliveredGasLimit(trade.contractAddress).amountUsed

                val txReceipt =
                    updateItemStateUseCase.itemDelivered(
                        trade.contractAddress,
                        gasPrice,
                        gasLimit
                    )

                _uiState.emit(TradeInfoUIState.HideItemDeliveryProgress)

                val formattedGasCost = WalletUtil.weiToNetworkToken(
                    txReceipt.gasUsed.multiply(gasPrice),
                    trade.network
                )

                _uiState.emit(
                    TradeInfoUIState.SuccessChangeDeliveryState(
                        trade.contractAddress,
                        txReceipt.transactionHash,
                        ContractStatus.ITEM_SENT.statusName,
                        formattedGasCost
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradeInfoUIState.HideItemDeliveryProgress)
                _uiState.emit(TradeInfoUIState.Error(e.message.toString()))
            }
        }
    }
}
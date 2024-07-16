package com.penguinstudios.tradeguardian.ui.createtrade

import com.penguinstudios.tradeguardian.data.model.ContractDeployment

sealed class CreateTradeUIState {
    data class SuccessDeployContract(
        val txHash: String,
        val contractAddress: String,
        val formattedGasUsed: String
    ) : CreateTradeUIState()

    data class ConfirmContractDeployment(
        val contractDeployment: ContractDeployment
    ) : CreateTradeUIState()

    data class SuccessGetDeploymentCosts(
        val itemCostUsd: String?,
        val totalDeploymentGasCostEther: String?,
        val totalDeploymentGasCostUsd: String?
    ) : CreateTradeUIState()

    object FailedToGetGasData : CreateTradeUIState()

    data class FailedToGetExchangeRate(val totalDeploymentGasCostEther: String) :
        CreateTradeUIState()

    object ShowDeployContractProgress : CreateTradeUIState()
    object HideDeployContractProgress : CreateTradeUIState()

    data class Error(val message: String) : CreateTradeUIState()
}
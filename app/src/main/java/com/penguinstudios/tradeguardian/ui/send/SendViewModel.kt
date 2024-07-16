package com.penguinstudios.tradeguardian.ui.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.data.model.network
import com.penguinstudios.tradeguardian.data.usecase.SendUseCase
import com.penguinstudios.tradeguardian.util.WalletUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository,
    private val sendUseCase: SendUseCase
) : ViewModel() {

    private val _uiState = MutableSharedFlow<SendUIState>()
    val uiState = _uiState.asSharedFlow()

    fun onSend(selectedNetwork: Network, sendToAddress: String, amount: String) {
        viewModelScope.launch {
            try {
                _uiState.emit(SendUIState.ShowProgressSend)

                val gasPrice = remoteRepository.estimateGasPrice().gasPrice

                val txReceipt = sendUseCase.send(sendToAddress, amount)

                val gasCostEther = txReceipt.gasUsed.multiply(gasPrice)

                val formattedGasCost = WalletUtil.weiToNetworkToken(
                    gasCostEther,
                    selectedNetwork
                )

                _uiState.emit(SendUIState.HideProgressSend)

                _uiState.emit(
                    SendUIState.SuccessfulSend(
                        walletRepository.credentials.address,
                        txReceipt.transactionHash,
                        sendToAddress,
                        "$amount ${selectedNetwork.networkTokenName}",
                        formattedGasCost
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(SendUIState.HideProgressSend)
                _uiState.emit(SendUIState.Error(e.message.toString()))
            }
        }
    }
}
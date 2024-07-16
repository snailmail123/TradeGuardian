package com.penguinstudios.tradeguardian.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.LocalRepository
import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.SharedPrefManager
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.data.usecase.WalletUseCase
import com.penguinstudios.tradeguardian.util.WalletUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository,
    private val localRepository: LocalRepository,
    private val sharedPrefManager: SharedPrefManager,
    private val walletUseCase: WalletUseCase
) : ViewModel() {

    private val _uiState = MutableSharedFlow<WalletUIState>()
    val uiState = _uiState.asSharedFlow()

    fun getWalletBalance() {
        viewModelScope.launch {
            try {
                val selectedNetwork = localRepository.getSelectedNetwork()

                val selectedNetworkPosition =
                    Network.values().indexOfFirst { it == selectedNetwork }

                if (selectedNetworkPosition == -1) {
                    throw IllegalStateException("Selected network is not valid.")
                }

                _uiState.emit(WalletUIState.SetSpinnerSelectedNetwork(selectedNetworkPosition))

                _uiState.emit(WalletUIState.ShowProgressWalletBalance)

                val walletBalance = walletUseCase.getWalletBalance().balance

                val formattedWalletBalance =
                    WalletUtil.weiToNetworkToken(walletBalance, selectedNetwork)

                _uiState.emit(
                    WalletUIState.SuccessGetBalance(
                        walletRepository.credentials.address, formattedWalletBalance
                    )
                )

                _uiState.emit(WalletUIState.HideProgressWalletBalance)
            } catch (e: TimeoutCancellationException) {
                Timber.e(e)
                _uiState.emit(WalletUIState.Error("Request timed out: Failed to get wallet balance"))
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(WalletUIState.Error("Failed to get wallet balance: " + e.message))
            }
        }
    }

    fun getWalletAddress(): String {
        return walletRepository.credentials.address
    }

    fun onExportTrades() {
        viewModelScope.launch {
            try {
                localRepository.exportTrades()
                _uiState.emit(WalletUIState.SuccessExportTrade)
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(WalletUIState.Error("Failed to export trades"))
            }
        }
    }

    fun updateSelectedNetwork(network: Network) {
        sharedPrefManager.selectedNetworkId = network.id
        remoteRepository.updateSelectedNetwork(network)
        getWalletBalance()
    }
}
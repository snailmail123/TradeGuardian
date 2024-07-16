package com.penguinstudios.tradeguardian.ui.trades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.LocalRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.model.Trade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TradesViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _uiState = MutableSharedFlow<TradesUIState>()
    val uiState = _uiState.asSharedFlow()
    val trades: MutableList<Trade> = mutableListOf()

    fun getTrades() {
        viewModelScope.launch {
            try {
                trades.clear()
                val list = localRepository.getTrades(walletRepository.credentials.address)
                if (list.isEmpty()) {
                    _uiState.emit(TradesUIState.NoTrades)
                    return@launch
                }
                trades.addAll(list)
                _uiState.emit(TradesUIState.SuccessGetTrades)
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(TradesUIState.Error(e.message.toString()))
            }
        }
    }

    suspend fun removeTradeFromList(contractAddress: String) {
        val index = trades.indexOfFirst { trade -> trade.contractAddress == contractAddress }
        if (index >= 0) {
            trades.removeAt(index)
            _uiState.emit(TradesUIState.DeleteTrade(index))
        }

        if (trades.isEmpty()) {
            _uiState.emit(TradesUIState.NoTrades)
        }
    }

    suspend fun updateTrade(trade: Trade) {
        val index = trades.indexOfFirst { it.id == trade.id }
        if (index >= 0) {
            trades[index] = trade
            _uiState.emit(TradesUIState.UpdateTrade(index))
        }
    }
}
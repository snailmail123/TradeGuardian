package com.penguinstudios.tradeguardian.ui.resetwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResetWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
) : ViewModel() {

    private val _uiState = MutableSharedFlow<ResetWalletUIState>()
    val uiState = _uiState.asSharedFlow()

    fun onTextChanged(s: String) {
        viewModelScope.launch {
            if (s == "delete") {
                _uiState.emit(ResetWalletUIState.ValidTextEntered)
            } else {
                _uiState.emit(ResetWalletUIState.InvalidTextEntered)
            }
        }
    }

    fun deleteWallet() {
        viewModelScope.launch {
            try {
                walletRepository.deleteWallet()
                _uiState.emit(ResetWalletUIState.SuccessResetWallet)
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(ResetWalletUIState.Error(e.message.toString()))
            }
        }
    }
}
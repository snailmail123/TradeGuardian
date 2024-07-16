package com.penguinstudios.tradeguardian.ui.welcomeback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.SharedPrefManager
import com.penguinstudios.tradeguardian.data.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WelcomeBackViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val sharedPrefManager: SharedPrefManager,
    private val filesDir: File
) : ViewModel() {

    private val _uiState = MutableSharedFlow<WelcomeBackUIState>()
    val uiState = _uiState.asSharedFlow()

    fun unlockWallet(password: String) {
        viewModelScope.launch {
            try {
                val walletName = sharedPrefManager.walletName
                    ?: throw Exception("Could not find wallet file. Must reset wallet.")

                withContext(Dispatchers.IO) {
                    val walletPath = File(filesDir, walletName)
                    walletRepository.unlockWallet(password, walletPath)
                }

                _uiState.emit(WelcomeBackUIState.SuccessUnlockWallet)
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(WelcomeBackUIState.Error(e.message.toString()))
            }
        }
    }

}
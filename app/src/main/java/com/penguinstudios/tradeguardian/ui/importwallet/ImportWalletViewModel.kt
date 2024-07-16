package com.penguinstudios.tradeguardian.ui.importwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.validator.PasswordStrengthEvaluator
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
class ImportWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableSharedFlow<ImportWalletUIState>()
    val uiState = _uiState.asSharedFlow()

    fun onImportBtnClick(mnemonic: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    walletRepository.validateMnemonicInput(mnemonic)
                    walletRepository.validateUserPasswordInput(newPassword, confirmPassword)
                    walletRepository.importWallet(mnemonic, newPassword)
                }
                _uiState.emit(ImportWalletUIState.SuccessImportWallet)
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(ImportWalletUIState.Error(e.message.toString()))
            }
        }
    }

    fun onNewPasswordTextChange(s: String) {
        viewModelScope.launch {
            val strength = PasswordStrengthEvaluator.evaluatePasswordStrength(s)
            _uiState.emit(ImportWalletUIState.UpdatePasswordStrength(strength))
        }
    }
}
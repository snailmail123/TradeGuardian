package com.penguinstudios.tradeguardian.ui.createwallet.viewmodel

import com.penguinstudios.tradeguardian.ui.createwallet.password.PasswordStrength

sealed class CreateWalletUIState {
    object SuccessCreateWallet : CreateWalletUIState()
    object SecureWalletComplete : CreateWalletUIState()
    data class UpdateSelectedWordsAdapter(val adapterPosition: Int) : CreateWalletUIState()
    data class UpdateSelectedWordsAdapterNextTarget(val adapterPosition: Int) : CreateWalletUIState()
    data class UpdateAvailableWordsAdapter(val adapterPosition: Int) : CreateWalletUIState()
    object EntireMnemonicNotEntered : CreateWalletUIState()
    object CorrectMnemonicEntered : CreateWalletUIState()
    object IncorrectMnemonicEntered : CreateWalletUIState()
    object CompleteBackup : CreateWalletUIState()
    data class ValidPassword(val password: String) : CreateWalletUIState()
    data class UpdatePasswordStrength(val strength: PasswordStrength) : CreateWalletUIState()
    data class Error(val message: String) : CreateWalletUIState()
}

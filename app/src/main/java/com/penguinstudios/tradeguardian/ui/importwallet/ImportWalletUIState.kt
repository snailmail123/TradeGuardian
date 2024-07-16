package com.penguinstudios.tradeguardian.ui.importwallet

import com.penguinstudios.tradeguardian.ui.createwallet.password.PasswordStrength
import com.penguinstudios.tradeguardian.ui.createwallet.viewmodel.CreateWalletUIState

sealed class ImportWalletUIState{
    object SuccessImportWallet : ImportWalletUIState()
    data class UpdatePasswordStrength(val strength: PasswordStrength) : ImportWalletUIState()
    data class Error(val message: String) : ImportWalletUIState()
}
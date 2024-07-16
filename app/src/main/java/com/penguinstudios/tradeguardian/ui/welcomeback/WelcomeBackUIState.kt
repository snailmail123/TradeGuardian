package com.penguinstudios.tradeguardian.ui.welcomeback

sealed class WelcomeBackUIState {
    object SuccessUnlockWallet : WelcomeBackUIState()
    data class Error(val message: String) : WelcomeBackUIState()
}
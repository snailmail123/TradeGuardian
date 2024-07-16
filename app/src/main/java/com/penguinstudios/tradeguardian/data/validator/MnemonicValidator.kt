package com.penguinstudios.tradeguardian.data.validator

import org.web3j.crypto.MnemonicUtils

object MnemonicValidator {

    fun String.isValidMnemonic() {
        require(MnemonicUtils.validateMnemonic(this)) { "Invalid mnemonic. Please try again." }
    }

}

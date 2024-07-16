package com.penguinstudios.tradeguardian.data

import com.penguinstudios.tradeguardian.data.validator.CreatePasswordValidator.isSameAs
import com.penguinstudios.tradeguardian.data.validator.CreatePasswordValidator.isValidPassword
import com.penguinstudios.tradeguardian.data.validator.MnemonicValidator.isValidMnemonic
import com.penguinstudios.tradeguardian.util.WalletUtil
import org.web3j.crypto.Bip39Wallet
import org.web3j.crypto.CipherException
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.WalletUtils
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val sharedPrefManager: SharedPrefManager,
    private val filesDir: File
) {

    lateinit var credentials: Credentials
    var mnemonicList: MutableList<String> = mutableListOf()
    var shuffledMnemonicList: MutableList<String> = mutableListOf()

    //Generates a wallet with a 12 word mnemonic that has no duplicate words
    @Throws(CipherException::class, IOException::class)
    fun generateWallet(password: String, filesDirectory: File): Bip39Wallet {
        while (true) {
            val wallet = WalletUtils.generateBip39Wallet(password, filesDirectory)
            val mnemonic = wallet.mnemonic
            val words = mnemonic.split(" ")
            val uniqueWords = words.toSet()

            if (uniqueWords.size == words.size) {
                return wallet
            }
        }
    }

    fun validateUserPasswordInput(password: String, confirmPassword: String) {
        password.isValidPassword()
        confirmPassword.isValidPassword()
        password.isSameAs(confirmPassword)
    }

    fun validateMnemonicInput(mnemonic: String) {
        mnemonic.isValidMnemonic()
    }

    fun importWallet(mnemonic: String, password: String) {
        val derivedKeypair = WalletUtil.deriveKeyPairFromMnemonic(mnemonic)
        val credentials = Credentials.create(derivedKeypair)
        val walletFileName = WalletUtils.generateWalletFile(
            password, credentials.ecKeyPair, filesDir, false
        )

        this.credentials = credentials
        sharedPrefManager.walletName = walletFileName
    }

    fun unlockWallet(password: String, filesDirectory: File) {
        val credentials = WalletUtils.loadCredentials(password, filesDirectory)
        this.credentials = credentials
    }

    fun createWallet(password: String, filesDirectory: File) {
        val wallet = generateWallet(password, filesDirectory)
        val derivedKeyPair = WalletUtil.deriveKeyPairFromMnemonic(wallet.mnemonic)
        this.credentials = Credentials.create(derivedKeyPair)

        sharedPrefManager.walletName = wallet.filename

        mnemonicList = wallet.mnemonic.split(" ").toMutableList()
        shuffledMnemonicList = mnemonicList.shuffled().toMutableList()
    }

    fun getMnemonic(): String {
        return mnemonicList.joinToString(separator = " ")
    }

    fun deleteWallet() {
        deleteKeystoreFile()
        sharedPrefManager.walletName = null
        credentials = Credentials.create(ECKeyPair(BigInteger.ZERO, BigInteger.ZERO))
    }

    private fun deleteKeystoreFile() {
        val walletName = sharedPrefManager.walletName ?: return
        File(filesDir, walletName).delete()
    }

    fun clearMnemonicLists() {
        mnemonicList.clear()
        shuffledMnemonicList.clear()
    }
}
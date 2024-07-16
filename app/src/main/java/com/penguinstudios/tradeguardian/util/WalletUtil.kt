package com.penguinstudios.tradeguardian.util

import com.penguinstudios.tradeguardian.data.model.Network
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.MnemonicUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object WalletUtil {

    private const val HARDENED_BIT = 0x80000000

    // Standard Ethereum derivation path -- metamask compliant
    fun deriveKeyPairFromMnemonic(mnemonic: String): Bip32ECKeyPair {
        val derivationPath = intArrayOf(
            44 or HARDENED_BIT.toInt(),
            60 or HARDENED_BIT.toInt(),
            0 or HARDENED_BIT.toInt(),
            0,
            0
        )
        val seed = MnemonicUtils.generateSeed(mnemonic, null)
        val masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed)
        return Bip32ECKeyPair.deriveKeyPair(masterKeyPair, derivationPath)
    }

    fun weiToEther(balance: BigInteger): BigDecimal {
        val divisor = BigDecimal.TEN.pow(18)
        return BigDecimal(balance).divide(divisor, 8, RoundingMode.HALF_UP)
    }

    fun formatToUSD(value: BigDecimal): String {
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        return format.format(value) + " USD"
    }

    fun weiToNetworkToken(amountWei: BigInteger, network: Network): String {
        val divisor = BigDecimal.TEN.pow(18)
        val ether = BigDecimal(amountWei)
            .divide(divisor, 8, RoundingMode.HALF_UP)
            .toPlainString() //toPlainString prevents scientific notation
        return "$ether ${network.networkTokenName}"
    }
}

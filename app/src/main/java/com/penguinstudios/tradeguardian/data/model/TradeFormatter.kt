package com.penguinstudios.tradeguardian.data.model

import com.penguinstudios.tradeguardian.util.Constants
import com.penguinstudios.tradeguardian.util.WalletUtil
import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Locale

private val dateFormat: SimpleDateFormat =
    SimpleDateFormat(Constants.DATE_PATTERN, Locale.US)

fun Trade.getFormattedDateCreated(): String = dateFormat.format(dateCreatedMillis)

val Trade.network: Network get() = Network.getNetworkById(networkId)
val Trade.networkName: String get() = network.networkName
val Trade.networkTokenName: String get() = network.networkTokenName
val Trade.userRole: UserRole get() = UserRole.getUserRoleById(userRoleId)
val Trade.counterPartyRole: UserRole get() = UserRole.getUserRoleById(counterPartyRoleId)

val Trade.gasCostEther: BigDecimal get() = WalletUtil.weiToEther(gasCostWei.toBigInteger())
val Trade.itemPriceEther: BigDecimal get() = WalletUtil.weiToEther(itemPriceWei.toBigInteger())

fun Trade.getFormattedItemPrice(): String = "${itemPriceEther.toPlainString()} $networkTokenName"
fun Trade.getFormattedGasCost(): String = "${gasCostEther.toPlainString()} $networkTokenName"

fun Trade.getSellerDepositAmount(): BigInteger =
    BigInteger.valueOf(itemPriceWei).multiply(Constants.SELLER_DEPOSIT_MULTIPLIER.toBigInteger())

fun Trade.getFormattedSellerDepositAmount(): String =
    WalletUtil.weiToNetworkToken(getSellerDepositAmount(), network)

fun Trade.getBuyerDepositAmount(): BigInteger =
    BigInteger.valueOf(itemPriceWei).multiply(Constants.BUYER_DEPOSIT_MULTIPLIER.toBigInteger())

fun Trade.getFormattedBuyerDepositAmount(): String =
    WalletUtil.weiToNetworkToken(getBuyerDepositAmount(), network)

private fun Trade.calculateSuccessfulTxFeePerParty(): BigInteger =
    BigInteger.valueOf(itemPriceWei)
        .multiply(BigInteger.valueOf(Constants.FEE_PERCENTAGE.toLong()))
        .divide(BigInteger.valueOf(100))
        .divide(BigInteger.valueOf(2))

fun Trade.getFormattedAmountReturnedToSeller(): String =
    WalletUtil.weiToNetworkToken(
        BigInteger.valueOf(itemPriceWei) - calculateSuccessfulTxFeePerParty(),
        this.network
    )

fun Trade.getFormattedAmountReturnedToBuyer(): String = getFormattedAmountReturnedToSeller()

fun Trade.getFormattedPercentFeePerParty(): String =
    WalletUtil.weiToNetworkToken(calculateSuccessfulTxFeePerParty(), network)



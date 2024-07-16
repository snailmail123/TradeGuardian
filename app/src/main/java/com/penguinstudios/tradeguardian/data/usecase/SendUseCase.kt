package com.penguinstudios.tradeguardian.data.usecase

import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.validator.EtherAmountValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import javax.inject.Inject

class SendUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository
) {

    suspend fun send(sendToAddress: String, amount: String): TransactionReceipt {
        return withContext(Dispatchers.IO) {
            require(WalletUtils.isValidAddress(sendToAddress)) { "Not a valid user wallet address" }

            val itemPriceDecimal = EtherAmountValidator.validateAndConvert(amount)

            Transfer.sendFunds(
                remoteRepository.web3j,
                walletRepository.credentials,
                sendToAddress,
                itemPriceDecimal,
                Convert.Unit.ETHER
            ).sendAsync().await()
        }
    }
}
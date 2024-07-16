package com.penguinstudios.tradeguardian.data.usecase

import com.penguinstudios.tradeguardian.contract.Escrow
import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.util.Constants
import com.penguinstudios.tradeguardian.util.GasUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthEstimateGas
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger
import javax.inject.Inject

class DepositUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository
) {

    suspend fun buyerDeposit(
        contractAddress: String,
        depositAmountWei: BigInteger,
        gasPrice: BigInteger,
        originalGasLimit: BigInteger
    ): TransactionReceipt {
        return withContext(Dispatchers.IO) {
            val margin =
                GasUtils.calculateMargin(originalGasLimit, Constants.GAS_LIMIT_PERCENT_MARGIN)
            val adjustedGasLimit = originalGasLimit.add(margin)

            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                StaticGasProvider(gasPrice, adjustedGasLimit)
            )
                .buyerDeposit(depositAmountWei)
                .sendAsync()
                .await()
        }
    }

    suspend fun estimateBuyerDepositGasLimit(
        contractAddress: String,
        itemPriceWei: BigInteger,
    ): EthEstimateGas {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                //Need to correctly calculate the required deposit amount
                //If incorrect amount provided a MessageDecodingException will be thrown when trying
                //to access amountUsed from EthEstimateGas
                val depositAmountWei =
                    itemPriceWei.multiply(Constants.BUYER_DEPOSIT_MULTIPLIER.toBigInteger())

                val function = Function(
                    "buyerDeposit",
                    emptyList(),
                    emptyList<TypeReference<*>>()
                )

                val encodedFunction = FunctionEncoder.encode(function)

                val transaction = Transaction.createFunctionCallTransaction(
                    walletRepository.credentials.address,
                    null,
                    null,
                    null,
                    contractAddress,
                    depositAmountWei,
                    encodedFunction
                )

                remoteRepository.web3j.ethEstimateGas(transaction).sendAsync().await()
            }
        }
    }

    suspend fun estimateSellerDepositGasLimit(
        contractAddress: String,
        itemPriceWei: BigInteger
    ): EthEstimateGas {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                val depositAmountWei =
                    itemPriceWei.multiply(Constants.SELLER_DEPOSIT_MULTIPLIER.toBigInteger())

                val function = Function(
                    "sellerDeposit",
                    emptyList(),
                    emptyList<TypeReference<*>>()
                )

                val encodedFunction = FunctionEncoder.encode(function)

                val transaction = Transaction.createFunctionCallTransaction(
                    walletRepository.credentials.address,
                    null, // nonce
                    null, // gasPrice
                    null, // gasLimit
                    contractAddress,
                    depositAmountWei,
                    encodedFunction
                )

                remoteRepository.web3j.ethEstimateGas(transaction).sendAsync().await()
            }
        }
    }

    suspend fun sellerDeposit(
        contractAddress: String,
        depositAmountWei: BigInteger,
        gasPrice: BigInteger,
        originalGasLimit: BigInteger
    ): TransactionReceipt {
        return withContext(Dispatchers.IO) {
            val margin =
                GasUtils.calculateMargin(originalGasLimit, Constants.GAS_LIMIT_PERCENT_MARGIN)
            val adjustedGasLimit = originalGasLimit.add(margin)

            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                StaticGasProvider(gasPrice, adjustedGasLimit)
            )
                .sellerDeposit(depositAmountWei)
                .sendAsync()
                .await()
        }
    }
}
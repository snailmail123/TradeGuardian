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

class UpdateItemStateUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository
) {

    suspend fun correctItemReceived(
        contractAddress: String,
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
                .setBuyerHasReceivedCorrectItem()
                .sendAsync()
                .await()
        }
    }

    suspend fun estimateCorrectItemGasLimit(
        contractAddress: String
    ): EthEstimateGas {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                val function = Function(
                    "setBuyerHasReceivedCorrectItem",
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
                    null,
                    encodedFunction
                )

                remoteRepository.web3j.ethEstimateGas(transaction).sendAsync().await()
            }
        }
    }

    suspend fun incorrectItemReceived(
        contractAddress: String,
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
                .setBuyerHasReceivedIncorrectItem()
                .sendAsync()
                .await()
        }
    }

    suspend fun estimateIncorrectItemGasLimit(
        contractAddress: String,
    ): EthEstimateGas {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                val function = Function(
                    "setBuyerHasReceivedIncorrectItem",
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
                    null,
                    encodedFunction
                )

                remoteRepository.web3j.ethEstimateGas(transaction).sendAsync().await()
            }
        }
    }

    suspend fun itemDelivered(
        contractAddress: String,
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
                .setSellerHasGivenItem()
                .sendAsync()
                .await()
        }
    }

    suspend fun estimateItemDeliveredGasLimit(
        contractAddress: String
    ): EthEstimateGas {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                val function = Function(
                    "setSellerHasGivenItem",
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
                    null,
                    encodedFunction
                )

                remoteRepository.web3j.ethEstimateGas(transaction).sendAsync().await()
            }
        }
    }
}
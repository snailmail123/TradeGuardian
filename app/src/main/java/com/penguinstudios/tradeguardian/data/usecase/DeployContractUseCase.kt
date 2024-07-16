package com.penguinstudios.tradeguardian.data.usecase

import com.penguinstudios.tradeguardian.contract.Escrow
import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.model.ContractDeployment
import com.penguinstudios.tradeguardian.util.Constants
import com.penguinstudios.tradeguardian.util.GasUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthEstimateGas
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger
import javax.inject.Inject

class DeployContractUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository
) {

    suspend fun deployContract(
        contractDeployment: ContractDeployment,
        gasPrice: BigInteger,
        originalGasLimit: BigInteger
    ): TransactionReceipt {
        return withContext(Dispatchers.IO) {
            // Calculate the additional gas as a margin
            val margin = GasUtils.calculateMargin(originalGasLimit, Constants.GAS_LIMIT_PERCENT_MARGIN)

            // Add the margin to the original gas limit
            val adjustedGasLimit = originalGasLimit.add(margin)

            Escrow.deploy(
                remoteRepository.web3j,
                walletRepository.credentials,
                StaticGasProvider(gasPrice, adjustedGasLimit),
                contractDeployment.feeRecipientAddress,
                contractDeployment.sellerAddress,
                contractDeployment.buyerAddress,
                contractDeployment.itemPriceWei,
                contractDeployment.description
            )
                .sendAsync()
                .await()
                .transactionReceipt
                .orElseThrow {
                    IllegalStateException("Transaction receipt not present")
                }
        }
    }

    suspend fun estimateDeployContractGasLimit(
        contractDeployment: ContractDeployment
    ): EthEstimateGas {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                val feeRecipientAddress = Address(contractDeployment.feeRecipientAddress)
                val sellerAddress = Address(contractDeployment.sellerAddress)
                val buyerAddress = Address(contractDeployment.buyerAddress)
                val itemPriceWei = Uint256(contractDeployment.itemPriceWei)
                val description = Utf8String(contractDeployment.description)

                val encodedConstructorParameters = FunctionEncoder.encodeConstructor(
                    listOf(
                        feeRecipientAddress,
                        sellerAddress,
                        buyerAddress,
                        itemPriceWei,
                        description
                    )
                )

                val data = Escrow.BINARY + encodedConstructorParameters

                remoteRepository.web3j.ethEstimateGas(
                    Transaction.createEthCallTransaction(
                        walletRepository.credentials.address,
                        null, // To address is null for contract deployment
                        data,
                    )
                ).sendAsync().await()
            }
        }
    }
}
package com.penguinstudios.tradeguardian.data.usecase

import com.penguinstudios.tradeguardian.contract.Escrow
import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.model.ContractStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import javax.inject.Inject

class ContractInfoUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository
) {

    suspend fun getContractUserBalance(
        contractAddress: String,
        walletAddress: String
    ): BigInteger {
        return withContext(Dispatchers.IO) {
            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                DefaultGasProvider()
            )
                .userBalances(walletAddress)
                .sendAsync()
                .await()
        }
    }

    suspend fun hasDeposited(
        contractAddress: String,
        walletAddress: String
    ): Boolean {
        return getContractUserBalance(
            contractAddress, walletAddress
        ) > BigInteger.ZERO
    }

    suspend fun getContractStatus(contractAddress: String): ContractStatus {
        return withContext(Dispatchers.IO) {
            val contractStatusId = Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                DefaultGasProvider()
            )
                .currentState()
                .sendAsync()
                .await()

            ContractStatus.getStatusById(contractStatusId.toInt())
        }
    }

    suspend fun getDateCreatedSeconds(contractAddress: String): BigInteger {
        return withContext(Dispatchers.IO) {
            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                DefaultGasProvider()
            )
                .contractCreationDate()
                .sendAsync()
                .await()
        }
    }

    suspend fun getItemPriceWei(contractAddress: String): BigInteger {
        return withContext(Dispatchers.IO) {
            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                DefaultGasProvider()
            )
                .itemPrice()
                .sendAsync()
                .await()
        }
    }

    suspend fun getSellerAddress(contractAddress: String): String {
        return withContext(Dispatchers.IO) {
            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                DefaultGasProvider()
            )
                .seller()
                .sendAsync()
                .await()
        }
    }

    suspend fun getBuyerAddress(contractAddress: String): String {
        return withContext(Dispatchers.IO) {
            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                DefaultGasProvider()
            )
                .buyer()
                .sendAsync()
                .await()
        }
    }

    suspend fun getDescription(contractAddress: String): String {
        return withContext(Dispatchers.IO) {
            Escrow.load(
                contractAddress,
                remoteRepository.web3j,
                walletRepository.credentials,
                DefaultGasProvider()
            )
                .description()
                .sendAsync()
                .await()
        }
    }

    fun isUserInvolvedInTrade(buyerAddress: String, sellerAddress: String): Boolean {
        val userWalletAddress = walletRepository.credentials.address
        return userWalletAddress == buyerAddress || userWalletAddress == sellerAddress
    }
}
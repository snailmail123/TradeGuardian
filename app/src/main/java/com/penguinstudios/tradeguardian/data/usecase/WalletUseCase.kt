package com.penguinstudios.tradeguardian.data.usecase

import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import javax.inject.Inject

class WalletUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository
) {

    suspend fun getWalletBalance(): EthGetBalance {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                remoteRepository.web3j.ethGetBalance(
                    walletRepository.credentials.address,
                    DefaultBlockParameterName.LATEST
                ).sendAsync().await()
            }
        }
    }
}
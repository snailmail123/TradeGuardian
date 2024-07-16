package com.penguinstudios.tradeguardian.data

import com.penguinstudios.tradeguardian.data.model.ExchangeRateResponse
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.protocol.http.HttpService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepository @Inject constructor(
    localRepository: LocalRepository,
    private val binanceService: BinanceService
) {

    var web3j: Web3j = Web3j.build(HttpService(localRepository.getSelectedNetwork().baseUrl))

    fun updateSelectedNetwork(network: Network) {
        web3j = Web3j.build(HttpService(network.baseUrl))
    }

    suspend fun getUsdExchangeRate(selectedNetwork: Network): ExchangeRateResponse {
        return withContext(Dispatchers.IO) {
            binanceService.getPrice(selectedNetwork.priceQuerySymbol)
        }
    }

    suspend fun estimateGasPrice(): EthGasPrice {
        return withContext(Dispatchers.IO) {
            withTimeout(Constants.DEFAULT_TIMEOUT_MILLIS) {
                web3j.ethGasPrice().sendAsync().await()
            }
        }
    }
}
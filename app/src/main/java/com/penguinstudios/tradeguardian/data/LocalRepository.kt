package com.penguinstudios.tradeguardian.data

import android.app.Application
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.data.model.Trade
import com.penguinstudios.tradeguardian.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val application: Application,
    private val walletRepository: WalletRepository,
    private val appDatabase: AppDatabase,
    private val sharedPrefManager: SharedPrefManager
) {

    suspend fun getTrades(userWalletAddress: String): List<Trade> {
        return appDatabase.tradesDao().getTrades(userWalletAddress)
    }

    suspend fun insertTrade(trade: Trade) {
        appDatabase.tradesDao().insertTrade(trade)
    }

    suspend fun updateTrade(trade: Trade) {
        appDatabase.tradesDao().updateTrade(trade)
    }

    suspend fun deleteTrade(contractAddress: String) {
        appDatabase.tradesDao().deleteTrade(contractAddress)
    }

    suspend fun tradeExists(contractAddress: String): Boolean {
        val userWalletAddress = walletRepository.credentials.address
        return getTrades(userWalletAddress).any { it.contractAddress == contractAddress.lowercase() }
    }

    fun getSelectedNetwork(): Network {
        return Network.getNetworkById(sharedPrefManager.selectedNetworkId)
    }

    suspend fun exportTrades() {
        withContext(Dispatchers.IO) {
            val gson = Gson()
            val jsonTrades = gson.toJson(getTrades(walletRepository.credentials.address))

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, Constants.EXPORTED_TRADES_FILE_NAME)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val resolver = application.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonTrades.toByteArray())
                outputStream.flush()
            }
        }
    }
}
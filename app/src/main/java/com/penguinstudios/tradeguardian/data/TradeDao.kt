package com.penguinstudios.tradeguardian.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.penguinstudios.tradeguardian.data.model.Trade

@Dao
interface TradeDao {

    @Insert
    suspend fun insertTrade(trade: Trade)

    @Update
    suspend fun updateTrade(trade: Trade)

    @Query("SELECT * FROM trades WHERE user_wallet_address = :userWalletAddress ORDER BY id DESC")
    suspend fun getTrades(userWalletAddress: String): List<Trade>

    @Query("DELETE FROM trades WHERE contract_address = :contractAddress")
    suspend fun deleteTrade(contractAddress: String)
}
package com.penguinstudios.tradeguardian.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.penguinstudios.tradeguardian.data.model.Trade

@Database(entities = [Trade::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tradesDao(): TradeDao

}
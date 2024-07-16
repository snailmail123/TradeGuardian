package com.penguinstudios.tradeguardian.data

import com.penguinstudios.tradeguardian.data.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceService {
    @GET("price")
    suspend fun getPrice(@Query("symbol") symbol: String): ExchangeRateResponse
}
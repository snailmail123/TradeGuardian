package com.penguinstudios.tradeguardian.util

object Constants {
    const val CONTRACT_BIN: String = "contract_binary_code.txt"
    const val EXPORTED_TRADES_FILE_NAME: String = "trades"
    const val FEE_RECIPIENT: String = "0x64ed56c19182aa10d4c0dc0db979d9c407c6b0c3"
    const val BINANCE_API_BASE_URL: String = "https://www.binance.com/api/v3/ticker/"
    const val DATE_PATTERN: String = "EEE, MMM d yyyy, hh:mm a z"
    const val BUYER_DEPOSIT_MULTIPLIER: Int = 2
    const val SELLER_DEPOSIT_MULTIPLIER: Int = 1

    // 1% fee of item price if successful transaction between both parties
    const val FEE_PERCENTAGE: Int = 1
    const val MILLIS_PER_SECOND: Int = 1000
    const val TWO_HOURS_IN_SECONDS: Int = 7200
    const val DEFAULT_TIMEOUT_MILLIS: Long = 30000L
    const val GAS_LIMIT_PERCENT_MARGIN: Int = 20
}

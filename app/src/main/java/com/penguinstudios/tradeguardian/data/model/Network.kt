package com.penguinstudios.tradeguardian.data.model

import com.penguinstudios.tradeguardian.R

enum class Network(
    val id: Int,
    val baseUrl: String,
    val networkName: String,
    val networkTokenName: String,
    val chainId: Int,
    val explorerUrl: String,
    val networkImage: Int,
    val priceQuerySymbol: String,
    val explorerName: String
) {
    ROOTSTOCK_TESTNET(
        0,
         "https://public-node.testnet.rsk.co",
        "Rootstock Testnet",
        "tRBTC",
        31,
        "https://explorer.testnet.rootstock.io/address/",
        R.drawable.rbtc_icon,
        "BTCUSDT",
        "RSK Explorer"
    );

    companion object {
        fun getNetworkById(id: Int): Network {
            return values().firstOrNull { it.id == id }
                ?: throw IllegalStateException("Invalid id: $id")
        }

        fun getFirstAvailableNetwork(): Network {
            return values().firstOrNull()
                ?: throw NoSuchElementException("No networks are available.")
        }
    }
}

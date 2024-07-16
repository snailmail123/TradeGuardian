package com.penguinstudios.tradeguardian.data

import android.content.SharedPreferences
import com.penguinstudios.tradeguardian.data.model.Network
import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences){
    companion object {
        private const val WALLET_NAME = "wallet_name"
        private const val SELECTED_NETWORK_ID = "selected_network_id"
    }

    var walletName: String?
        get() = sharedPreferences.getString(WALLET_NAME, null)
        set(value) = sharedPreferences.edit().putString(WALLET_NAME, value).apply()

    var selectedNetworkId: Int
        get() = sharedPreferences.getInt(SELECTED_NETWORK_ID, Network.getFirstAvailableNetwork().id)
        set(value) = sharedPreferences.edit().putInt(SELECTED_NETWORK_ID, value).apply()
}
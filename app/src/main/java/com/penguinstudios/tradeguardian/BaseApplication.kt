package com.penguinstudios.tradeguardian

import android.app.Application
import com.penguinstudios.tradeguardian.contract.Escrow
import com.penguinstudios.tradeguardian.util.AssetUtil
import com.penguinstudios.tradeguardian.util.Constants
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.IOException

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        try {
            Escrow.loadContract(AssetUtil.getContractBin(this, Constants.CONTRACT_BIN))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
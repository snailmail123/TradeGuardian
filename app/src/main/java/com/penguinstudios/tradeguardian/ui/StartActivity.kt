package com.penguinstudios.tradeguardian.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.penguinstudios.tradeguardian.data.SharedPrefManager
import com.penguinstudios.tradeguardian.ui.welcomeback.WelcomeBackActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val intent = if (sharedPrefManager.walletName != null) {
            Intent(this, WelcomeBackActivity::class.java)
        } else {
            Intent(this, WalletSetupActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}
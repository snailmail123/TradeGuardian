package com.penguinstudios.tradeguardian.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.penguinstudios.tradeguardian.databinding.WalletSetupActivityBinding
import com.penguinstudios.tradeguardian.ui.createwallet.CreateWalletFragment
import com.penguinstudios.tradeguardian.ui.importwallet.ImportWalletFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WalletSetupActivity : AppCompatActivity() {

    private lateinit var binding: WalletSetupActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WalletSetupActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnCreateWallet.setOnClickListener {
            CreateWalletFragment().show(supportFragmentManager, null)
        }

        binding.btnImport.setOnClickListener {
            ImportWalletFragment().show(supportFragmentManager, null)
        }
    }
}
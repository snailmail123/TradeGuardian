package com.penguinstudios.tradeguardian.ui.welcomeback

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.penguinstudios.tradeguardian.databinding.WelcomeBackActivityBinding
import com.penguinstudios.tradeguardian.ui.MainActivity
import com.penguinstudios.tradeguardian.ui.resetwallet.ResetWalletFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeBackActivity : AppCompatActivity() {

    private lateinit var binding: WelcomeBackActivityBinding
    private val viewModel: WelcomeBackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeBackActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUnlock.setOnClickListener {
            viewModel.unlockWallet(binding.etPassword.text.toString())
        }

        binding.btnResetWallet.setOnClickListener {
            ResetWalletFragment().show(supportFragmentManager, null)
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is WelcomeBackUIState.SuccessUnlockWallet -> {
                        val intent = Intent(this@WelcomeBackActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    is WelcomeBackUIState.Error -> {
                        Toast.makeText(
                            this@WelcomeBackActivity,
                            uiState.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
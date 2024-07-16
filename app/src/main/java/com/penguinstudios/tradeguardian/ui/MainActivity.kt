package com.penguinstudios.tradeguardian.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.databinding.MainActivityBinding
import com.penguinstudios.tradeguardian.ui.createtrade.CreateTradeFragment
import com.penguinstudios.tradeguardian.ui.trades.TradesFragment
import com.penguinstudios.tradeguardian.ui.wallet.WalletFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val tradesFragment by lazy { TradesFragment() }
    private val createTradeFragment by lazy { CreateTradeFragment() }
    private val walletFragment by lazy { WalletFragment() }
    private var currentFragment: Fragment = tradesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_trades -> {
                    showFragment(tradesFragment)
                    true
                }

                R.id.navigation_create -> {
                    showFragment(createTradeFragment)
                    true
                }

                R.id.navigation_wallet -> {
                    showFragment(walletFragment)
                    true
                }

                else -> false
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, currentFragment)
            .commit()
    }


    private fun showFragment(fragment: Fragment) {
        if (fragment == currentFragment) {
            return
        }

        val transaction = supportFragmentManager.beginTransaction()

        if (fragment.isAdded) {
            transaction.hide(currentFragment).show(fragment)
        } else {
            transaction.add(R.id.fragment_container, fragment).hide(currentFragment)
        }

        transaction.commit()
        currentFragment = fragment
    }
}
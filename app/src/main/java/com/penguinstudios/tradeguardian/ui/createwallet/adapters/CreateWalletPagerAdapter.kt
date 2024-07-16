package com.penguinstudios.tradeguardian.ui.createwallet.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.penguinstudios.tradeguardian.ui.createwallet.ConfirmMnemonicFragment
import com.penguinstudios.tradeguardian.ui.createwallet.CreatePasswordFragment
import com.penguinstudios.tradeguardian.ui.createwallet.SecureWalletFragment

class CreateWalletPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    companion object {
        private const val NUM_ITEMS = 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CreatePasswordFragment()
            1 -> SecureWalletFragment()
            2 -> ConfirmMnemonicFragment()
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int {
        return NUM_ITEMS
    }
}

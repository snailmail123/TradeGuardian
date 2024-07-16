package com.penguinstudios.tradeguardian.ui.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.databinding.PopupWindowWalletOptionsBinding
import com.penguinstudios.tradeguardian.util.SpacingUtil

class WalletPopupWindow(
    anchorView: View,
    private val callback: Callback
) : PopupWindow() {

    interface Callback {
        fun onClearWallet()
    }

    private var binding: PopupWindowWalletOptionsBinding =
        PopupWindowWalletOptionsBinding.inflate(
            LayoutInflater.from(anchorView.context),
            null,
            false
        )

    init {
        contentView = binding.root
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(
            ContextCompat.getDrawable(
                anchorView.context,
                R.drawable.bgr_menu_clear_wallet
            )
        )
        elevation = SpacingUtil.convertIntToDP(anchorView.context, 4).toFloat()

        setupView()
    }

    private fun setupView() {
        binding.tvClearWallet.setOnClickListener {
            dismiss()
            callback.onClearWallet()
        }
    }
}
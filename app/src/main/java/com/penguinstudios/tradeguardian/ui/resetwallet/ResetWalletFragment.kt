package com.penguinstudios.tradeguardian.ui.resetwallet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.databinding.LayoutConfirmEraseOneBinding
import com.penguinstudios.tradeguardian.databinding.LayoutConfirmEraseTwoBinding
import com.penguinstudios.tradeguardian.databinding.ResetWalletFragmentBinding
import com.penguinstudios.tradeguardian.ui.MainActivity
import com.penguinstudios.tradeguardian.ui.WalletSetupActivity
import com.penguinstudios.tradeguardian.ui.welcomeback.WelcomeBackActivity
import com.penguinstudios.tradeguardian.util.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ResetWalletFragment : DialogFragment() {

    private lateinit var binding: ResetWalletFragmentBinding
    private lateinit var layoutConfirmEraseOne: LayoutConfirmEraseOneBinding
    private lateinit var layoutConfirmEraseTwo: LayoutConfirmEraseTwoBinding
    private val viewModel: ResetWalletViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.Theme_TradeGuardian)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.FragmentSlideUpAnim
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ResetWalletFragmentBinding.inflate(inflater, container, false)
        layoutConfirmEraseOne =
            LayoutConfirmEraseOneBinding.bind(binding.layoutConfirmEraseOne.root)
        layoutConfirmEraseTwo =
            LayoutConfirmEraseTwoBinding.bind(binding.layoutConfirmEraseTwo.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutConfirmEraseOne.btnCancel.setOnClickListener {
            dismiss()
        }

        layoutConfirmEraseTwo.btnCancel.setOnClickListener {
            dismiss()
        }

        layoutConfirmEraseOne.btnContinue.setOnClickListener {
            layoutConfirmEraseTwo.root.visibility = View.VISIBLE
            layoutConfirmEraseOne.root.visibility = View.GONE
            layoutConfirmEraseTwo.etDeleteWallet.requestFocus()
            KeyboardUtils.showKeyboard(layoutConfirmEraseTwo.etDeleteWallet)
        }

        layoutConfirmEraseTwo.etDeleteWallet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.onTextChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        layoutConfirmEraseTwo.btnDeleteWallet.setOnClickListener {
            viewModel.deleteWallet()
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is ResetWalletUIState.ValidTextEntered -> {
                        layoutConfirmEraseTwo.btnDeleteWallet.isEnabled = true
                    }

                    is ResetWalletUIState.InvalidTextEntered -> {
                        layoutConfirmEraseTwo.btnDeleteWallet.isEnabled = false
                    }

                    is ResetWalletUIState.SuccessResetWallet -> {
                        val intent = Intent(requireActivity(), WalletSetupActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    else -> {}
                }
            }
        }
    }
}
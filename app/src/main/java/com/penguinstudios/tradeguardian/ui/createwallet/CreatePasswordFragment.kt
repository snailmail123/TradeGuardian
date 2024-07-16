package com.penguinstudios.tradeguardian.ui.createwallet

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.penguinstudios.tradeguardian.databinding.CreatePasswordFragmentBinding
import com.penguinstudios.tradeguardian.ui.createwallet.password.PasswordStrength
import com.penguinstudios.tradeguardian.ui.createwallet.viewmodel.CreateWalletUIState
import com.penguinstudios.tradeguardian.ui.createwallet.viewmodel.CreateWalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePasswordFragment : Fragment() {

    private lateinit var binding: CreatePasswordFragmentBinding
    private val viewModel: CreateWalletViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreatePasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is CreateWalletUIState.UpdatePasswordStrength -> {
                        binding.tvPasswordStrength.visibility = View.VISIBLE
                        updatePasswordStrength(uiState.strength)
                    }

                    is CreateWalletUIState.ValidPassword -> {
                        if (binding.checkboxConfirm.isChecked) {
                            viewModel.createPassword(uiState.password)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Must agree to terms.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> {}
                }
            }
        }

        binding.btnCreatePassword.setOnClickListener {
            viewModel.onCreatePasswordClick(
                binding.etNewPassword.text.toString(),
                binding.etConfirmPassword.text.toString()
            )
        }

        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) {
                    binding.tvPasswordStrength.visibility = View.INVISIBLE
                } else {
                    viewModel.onNewPasswordTextChange(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun updatePasswordStrength(strength: PasswordStrength) {
        val baseText = "Password strength: "
        val coloredText = strength.strengthName
        val spannableString = SpannableString(baseText + coloredText)

        val color = Color.parseColor(strength.hexColor)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            baseText.length,
            (baseText + coloredText).length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvPasswordStrength.text = spannableString
    }
}
package com.penguinstudios.tradeguardian.ui.importwallet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.databinding.ImportFragmentBinding
import com.penguinstudios.tradeguardian.ui.MainActivity
import com.penguinstudios.tradeguardian.ui.createwallet.password.PasswordStrength
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ImportWalletFragment : DialogFragment() {

    private lateinit var binding: ImportFragmentBinding
    private val viewModel: ImportWalletViewModel by viewModels()

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
        binding = ImportFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        binding.btnImport.setOnClickListener {
            viewModel.onImportBtnClick(
                binding.etSecretPhrase.text.toString(),
                binding.etNewPassword.text.toString(),
                binding.etConfirmPassword.text.toString()
            )
        }

        binding.tilSecretPhrase.setEndIconOnClickListener {
            initScanner()
        }

        binding.btnShow.setOnClickListener {
            binding.etSecretPhrase.clearFocus()

            if (binding.etSecretPhrase.transformationMethod is PasswordTransformationMethod) {
                // Password is hidden, show it
                binding.etSecretPhrase.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.btnShow.text = "Hide"
            } else {
                // Password is visible, hide it
                binding.etSecretPhrase.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.btnShow.text = "Show"
            }
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

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is ImportWalletUIState.SuccessImportWallet -> {
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    is ImportWalletUIState.UpdatePasswordStrength -> {
                        binding.tvPasswordStrength.visibility = View.VISIBLE
                        updatePasswordStrength(uiState.strength)
                    }

                    is ImportWalletUIState.Error -> {
                        Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val barcodeLauncher =
        registerForActivityResult(ScanContract()) { result ->
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val s = result.contents
                binding.etSecretPhrase.setText(s)
            }
        }

    private fun initScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan seed phrase")
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
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
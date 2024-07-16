package com.penguinstudios.tradeguardian.ui.addtrade

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.databinding.AddTradeFragmentBinding
import com.penguinstudios.tradeguardian.ui.trades.TradesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTradeFragment : DialogFragment() {

    private lateinit var binding: AddTradeFragmentBinding
    private val addTradeViewModel: AddTradeViewModel by viewModels()
    private val tradesViewModel: TradesViewModel by activityViewModels()
    private var progressAddTrade: AlertDialog? = null

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
        binding = AddTradeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            addTradeViewModel.onConfirm(binding.etContractAddress.text.toString())
        }

        binding.tilContractAddress.setEndIconOnClickListener {
            initScanner()
        }

        lifecycleScope.launch {
            addTradeViewModel.uiState.collect { uiState ->
                when (uiState) {
                    is AddTradeUIState.SuccessAddTrade -> {
                        Toast.makeText(
                            requireContext(),
                            "Successfully added trade",
                            Toast.LENGTH_SHORT
                        ).show()

                        tradesViewModel.getTrades()

                        dismiss()
                    }

                    is AddTradeUIState.ShowProgressAddTrade -> {
                        showProgressAddTrade()
                    }

                    is AddTradeUIState.HideProgressAddTrade -> {
                        hideProgressAddTrade()
                    }

                    is AddTradeUIState.Error -> {
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
                binding.etContractAddress.setText(s)
            }
        }

    private fun initScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan contract address")
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }

    private fun showProgressAddTrade() {
        if (progressAddTrade == null) {
            val builder = AlertDialog.Builder(requireContext(), R.style.alertDialogTheme)
            builder.setView(R.layout.progress_add_trade)
            progressAddTrade = builder.create().apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        progressAddTrade?.show()
    }

    private fun hideProgressAddTrade() {
        progressAddTrade?.hide()
    }
}
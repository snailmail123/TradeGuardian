package com.penguinstudios.tradeguardian.ui.send

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.databinding.LayoutSpinnerBinding
import com.penguinstudios.tradeguardian.databinding.SendFragmentBinding
import com.penguinstudios.tradeguardian.util.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SendFragment : DialogFragment() {

    private lateinit var binding: SendFragmentBinding
    private lateinit var spinnerBinding: LayoutSpinnerBinding
    private val sendViewModel: SendViewModel by viewModels()
    private var progressSend: AlertDialog? = null

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
        binding = SendFragmentBinding.inflate(inflater, container, false)
        spinnerBinding = LayoutSpinnerBinding.bind(binding.layoutSpinner.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        binding.tilSendToAddress.setEndIconOnClickListener {
            initScanner()
        }

        binding.btnConfirm.setOnClickListener {
            KeyboardUtils.hideKeyboard(binding.etAmount)

            sendViewModel.onSend(
                Network.values()[spinnerBinding.spinnerNetwork.selectedItemPosition],
                binding.etSendToAddress.text.toString(),
                binding.etAmount.text.toString()
            )
        }

        initSpinner()

        lifecycleScope.launch {
            sendViewModel.uiState.collect { uiState ->
                when (uiState) {
                    is SendUIState.SuccessfulSend -> {
                        dismiss()

                        SuccessSendFragment(
                            uiState.userWalletAddress,
                            uiState.txHash,
                            uiState.sentToAddress,
                            uiState.formattedAmountSent,
                            uiState.formattedGasUsed
                        ).show(requireActivity().supportFragmentManager, null)
                    }

                    is SendUIState.ShowProgressSend -> {
                        binding.parentLayout.clearFocus()
                        showProgressSend()
                    }

                    is SendUIState.HideProgressSend -> {
                        hideProgressSend()
                    }

                    is SendUIState.Error -> {
                        Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initSpinner() {
        val spinnerItems = mutableListOf<String>()

        Network.values().forEach { network ->
            spinnerItems.add(network.networkName)
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_network_drop_down, spinnerItems
        )

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_network_drop_down)
        spinnerBinding.spinnerNetwork.adapter = spinnerAdapter
    }

    private val barcodeLauncher =
        registerForActivityResult(ScanContract()) { result ->
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val s = result.contents
                binding.etSendToAddress.setText(s)
            }
        }

    private fun initScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan wallet address")
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }

    private fun showProgressSend() {
        if (progressSend == null) {
            val builder = AlertDialog.Builder(requireContext(), R.style.alertDialogTheme)
            builder.setView(R.layout.progress_send)
            progressSend = builder.create().apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        progressSend?.show()
    }

    private fun hideProgressSend() {
        progressSend?.hide()
    }
}
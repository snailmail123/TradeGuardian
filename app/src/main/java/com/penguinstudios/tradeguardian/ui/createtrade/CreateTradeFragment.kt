package com.penguinstudios.tradeguardian.ui.createtrade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.data.model.UserRole
import com.penguinstudios.tradeguardian.databinding.CreateTradeFragmentBinding
import com.penguinstudios.tradeguardian.databinding.LayoutSpinnerBinding
import com.penguinstudios.tradeguardian.ui.confirmtrade.ConfirmTradeFragment
import com.penguinstudios.tradeguardian.ui.trades.TradesViewModel
import com.penguinstudios.tradeguardian.util.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateTradeFragment : Fragment() {

    private lateinit var binding: CreateTradeFragmentBinding
    private lateinit var spinnerBinding: LayoutSpinnerBinding
    private val createTradeViewModel: CreateTradeViewModel by activityViewModels()
    private val tradesViewModel: TradesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateTradeFragmentBinding.inflate(inflater, container, false)
        spinnerBinding = LayoutSpinnerBinding.bind(binding.layoutSpinner.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNetworkSpinner()

        binding.tilCountPartyAddress.setEndIconOnClickListener {
            initScanner()
        }

        binding.btnCreateTrade.setOnClickListener {
            val userRole: UserRole? = when (binding.radioGroup.checkedRadioButtonId) {
                R.id.radio_btn_seller -> UserRole.SELLER
                R.id.radio_btn_buyer -> UserRole.BUYER
                else -> null
            }

            if (userRole == null) {
                Toast.makeText(requireContext(), "No user role selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            KeyboardUtils.hideKeyboard(binding.etDescription)

            createTradeViewModel.onCreateTradeClick(
                Network.values()[spinnerBinding.spinnerNetwork.selectedItemPosition],
                userRole,
                binding.etCounterPartyAddress.text.toString(),
                binding.etItemPrice.text.toString(),
                binding.etDescription.text.toString()
            )

        }

        lifecycleScope.launch {
            createTradeViewModel.uiState.collect { uiState ->
                when (uiState) {
                    is CreateTradeUIState.SuccessDeployContract -> {
                        binding.radioGroup.clearCheck()
                        binding.etCounterPartyAddress.text?.clear()
                        binding.etItemPrice.text?.clear()
                        binding.etDescription.text?.clear()
                        binding.parentLayout.clearFocus()

                        tradesViewModel.getTrades()
                    }

                    is CreateTradeUIState.ConfirmContractDeployment -> {
                        ConfirmTradeFragment(uiState.contractDeployment).show(
                            requireActivity().supportFragmentManager,
                            null
                        )
                    }

                    is CreateTradeUIState.Error -> {
                        Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initNetworkSpinner() {
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
                binding.etCounterPartyAddress.setText(s)
            }
        }

    private fun initScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan counterparty address")
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }
}

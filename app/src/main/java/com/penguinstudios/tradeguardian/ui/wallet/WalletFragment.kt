package com.penguinstudios.tradeguardian.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.databinding.LayoutSpinnerBinding
import com.penguinstudios.tradeguardian.databinding.WalletFragmentBinding
import com.penguinstudios.tradeguardian.ui.resetwallet.ResetWalletFragment
import com.penguinstudios.tradeguardian.ui.send.SendFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletFragment : Fragment(), WalletPopupWindow.Callback {

    private lateinit var binding: WalletFragmentBinding
    private lateinit var spinnerBinding: LayoutSpinnerBinding
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WalletFragmentBinding.inflate(inflater, container, false)
        spinnerBinding = LayoutSpinnerBinding.bind(binding.layoutSpinner.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNetworkSpinner()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getWalletBalance()
        }

        binding.layoutWalletBalance.btnWalletMoreOptions.setOnClickListener {
            val popupWindow = WalletPopupWindow(it, this)
            popupWindow.showAsDropDown(it, -100, 0)
        }

        binding.btnSend.setOnClickListener {
            SendFragment().show(requireActivity().supportFragmentManager, null)
        }

        binding.btnReceive.setOnClickListener {
            QrWalletFragment(viewModel.getWalletAddress()).show(
                requireActivity().supportFragmentManager,
                null
            )
        }

        binding.btnExportTrades.setOnClickListener {
            viewModel.onExportTrades()
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is WalletUIState.SetSpinnerSelectedNetwork -> {
                        spinnerBinding.spinnerNetwork.setSelection(uiState.position)
                    }

                    is WalletUIState.ShowProgressWalletBalance -> {
                        binding.layoutProgressWalletBalance.root.visibility = View.VISIBLE
                    }

                    is WalletUIState.HideProgressWalletBalance -> {
                        binding.layoutProgressWalletBalance.root.visibility = View.GONE
                    }

                    is WalletUIState.SuccessGetBalance -> {
                        binding.layoutWalletBalance.tvWalletAddress.text = uiState.walletAddress
                        binding.layoutWalletBalance.tvWalletBalance.text = uiState.walletBalance
                        binding.layoutWalletBalance.root.visibility = View.VISIBLE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    is WalletUIState.SuccessExportTrade -> {
                        Toast.makeText(
                            requireContext(),
                            "Exported trades.json to downloads directory",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is WalletUIState.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.getWalletBalance()
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

        spinnerBinding.spinnerNetwork.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedNetworkName = spinnerItems[position]
                    val selectedNetwork =
                        Network.values().firstOrNull { it.networkName == selectedNetworkName }
                            ?: throw IllegalArgumentException("Network not found: $selectedNetworkName")

                    viewModel.updateSelectedNetwork(selectedNetwork)

                    Glide.with(this@WalletFragment)
                        .load(selectedNetwork.networkImage)
                        .centerInside()
                        .into(binding.layoutWalletBalance.ivNetworkImage)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This is called when nothing is selected
                }
            }
    }

    override fun onClearWallet() {
        ResetWalletFragment().show(requireActivity().supportFragmentManager, null)
    }
}
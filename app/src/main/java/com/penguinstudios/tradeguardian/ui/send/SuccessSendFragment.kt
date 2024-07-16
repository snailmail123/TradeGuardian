package com.penguinstudios.tradeguardian.ui.send

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.DialogFragment
import com.penguinstudios.tradeguardian.data.LocalRepository
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.databinding.SuccessCreateTradeFragmentBinding
import com.penguinstudios.tradeguardian.databinding.SuccessDepositFragmentBinding
import com.penguinstudios.tradeguardian.databinding.SuccessSendFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuccessSendFragment(
    private val userWalletAddress: String,
    private val txHash: String,
    private val sendToAddress: String,
    private val formattedAmountSent: String,
    private val formattedGasUsed: String
) : DialogFragment() {

    @Inject
    lateinit var localRepository: LocalRepository

    private lateinit var binding: SuccessSendFragmentBinding

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SuccessSendFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTxHash.text = txHash
        binding.tvSentToAddress.text = sendToAddress
        binding.tvAmount.text = formattedAmountSent
        binding.tvGasUsed.text = formattedGasUsed
        val selectedNetwork = localRepository.getSelectedNetwork()
        binding.tvExplorerName.text = "View on ${selectedNetwork.explorerName}"

        binding.btnViewExplorer.setOnClickListener {
            val url = selectedNetwork.explorerUrl + userWalletAddress
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
            dismiss()
        }
    }
}
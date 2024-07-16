package com.penguinstudios.tradeguardian.ui.tradeinfo

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.data.model.Trade
import com.penguinstudios.tradeguardian.data.model.counterPartyRole
import com.penguinstudios.tradeguardian.data.model.getFormattedItemPrice
import com.penguinstudios.tradeguardian.data.model.networkName
import com.penguinstudios.tradeguardian.data.model.userRole
import com.penguinstudios.tradeguardian.databinding.TradeInfoFragmentBinding
import com.penguinstudios.tradeguardian.ui.trades.TradesViewModel
import com.penguinstudios.tradeguardian.util.ClipboardUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TradeInfoFragment(
    private val trade: Trade
) : DialogFragment() {

    private lateinit var binding: TradeInfoFragmentBinding
    private val tradeInfoViewModel: TradeInfoViewModel by activityViewModels()
    private val tradesViewModel: TradesViewModel by activityViewModels()
    private var progressDeposit: AlertDialog? = null
    private var progressDelivery: AlertDialog? = null
    private var progressDeleteTrade: AlertDialog? = null
    private var progressRequestSettle: AlertDialog? = null

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
        binding = TradeInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val greenColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.green_400)
    }

    private val redColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.red_400)
    }

    private val defaultTextColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.default_text_color)
    }

    private val purpleColor by lazy {
        ContextCompat.getColor(requireContext(), R.color.purple_200)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tradeInfoViewModel.loadTrade(trade)

        binding.btnBack.setOnClickListener {
            dismiss()
        }

        binding.tvContractAddress.text = trade.contractAddress
        binding.tvDeployedOn.text = tradeInfoViewModel.trade.networkName
        binding.tvItemPrice.text = tradeInfoViewModel.trade.getFormattedItemPrice()
        binding.tvUserRole.text = tradeInfoViewModel.trade.userRole.roleName
        binding.tvUserWalletAddress.text = trade.userWalletAddress
        binding.tvCounterPartyRole.text = tradeInfoViewModel.trade.counterPartyRole.roleName
        binding.tvCounterpartyWalletAddress.text = trade.counterPartyWalletAddress
        binding.tvDescription.text = trade.description

        binding.btnDeposit.setOnClickListener {
            ConfirmDepositFragment().show(requireActivity().supportFragmentManager, null)
        }

        binding.btnItemReceived.setOnClickListener {
            ConfirmItemReceivedFragment(true).show(requireActivity().supportFragmentManager, null)
        }

        binding.btnIncorrectItem.setOnClickListener {
            ConfirmItemReceivedFragment(false).show(requireActivity().supportFragmentManager, null)
        }

        binding.btnItemDelivered.setOnClickListener {
            ConfirmItemDeliveredFragment().show(requireActivity().supportFragmentManager, null)
        }

        binding.btnRequestSettle.setOnClickListener {
            ConfirmRequestSettleFragment().show(requireActivity().supportFragmentManager, null)
        }

        binding.btnViewOnExplorer.setOnClickListener {
            openBrowser()
        }

        binding.btnCancelTrade.setOnClickListener {
            ConfirmCancelTradeFragment().show(requireActivity().supportFragmentManager, null)
        }

        binding.btnCopyContractAddress.setOnClickListener {
            Toast.makeText(requireContext(), "Copied contract address", Toast.LENGTH_SHORT).show()
            ClipboardUtil.copyText(requireContext(), trade.contractAddress)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            tradeInfoViewModel.setTradeInfo(false)
        }

        lifecycleScope.launch {
            tradeInfoViewModel.uiState.collect { uiState ->
                when (uiState) {
                    is TradeInfoUIState.SuccessDeposit -> {
                        SuccessDepositFragment(
                            uiState.contractAddress,
                            uiState.txHash,
                            uiState.formattedDepositAmount,
                            uiState.formattedGasUsed
                        ).show(requireActivity().supportFragmentManager, null)
                    }

                    is TradeInfoUIState.SuccessChangeDeliveryState -> {
                        SuccessChangeDeliveryStateFragment(
                            uiState.contractAddress,
                            uiState.txHash,
                            uiState.formattedDeliveryState,
                            uiState.formattedGasUsed
                        ).show(requireActivity().supportFragmentManager, null)
                    }

                    is TradeInfoUIState.ShowProgressDeposit -> {
                        showProgressDeposit()
                    }

                    is TradeInfoUIState.HideProgressDeposit -> {
                        hideProgressDeposit()
                    }

                    is TradeInfoUIState.ShowItemDeliveryProgress -> {
                        showProgressDelivery()
                    }

                    is TradeInfoUIState.HideItemDeliveryProgress -> {
                        hideProgressDelivery()
                    }

                    is TradeInfoUIState.ShowCancelingTradeProgress -> {
                        showProgressCancelTrade()
                    }

                    is TradeInfoUIState.HideCancelingTradeProgress -> {
                        hideProgressCancelTrade()
                    }

                    is TradeInfoUIState.ShowRequestingSettleProgress -> {
                        showProgressRequestSettle()
                    }

                    is TradeInfoUIState.HideRequestingSettleProgress -> {
                        hideProgressRequestSettle()
                    }

                    is TradeInfoUIState.UpdateBuyerDepositStatus -> {
                        binding.tvBuyerDepositStatus.text = uiState.status
                        if (uiState.hasDeposited) {
                            binding.tvBuyerDepositStatus.setTextColor(greenColor)
                        }
                    }

                    is TradeInfoUIState.UpdateSellerDepositStatus -> {
                        binding.tvSellerDepositStatus.text = uiState.status
                        if (uiState.hasDeposited) {
                            binding.tvSellerDepositStatus.setTextColor(greenColor)
                        }
                    }

                    is TradeInfoUIState.ShowDepositBtn -> {
                        binding.btnDeposit.visibility = View.VISIBLE
                    }

                    is TradeInfoUIState.HideDepositBtn -> {
                        binding.btnDeposit.visibility = View.GONE
                    }

                    is TradeInfoUIState.Error -> {
                        Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                    }

                    is TradeInfoUIState.SetStepIndicatorStepOne -> {
                        setCurrentStepIndicator(1, false)
                    }

                    is TradeInfoUIState.SetStepIndicatorStepTwo -> {
                        setCurrentStepIndicator(2, false)
                    }

                    is TradeInfoUIState.SetStepIndicatorStepThree -> {
                        setCurrentStepIndicator(3, uiState.showSettleStatus)
                    }

                    is TradeInfoUIState.SuccessDeleteTradeNoReceipt -> {
                        tradesViewModel.removeTradeFromList(uiState.contractAddress)
                        Toast.makeText(
                            requireContext(),
                            "Successfully canceled trade",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }

                    is TradeInfoUIState.SuccessDeleteWithReceipt -> {
                        tradesViewModel.removeTradeFromList(uiState.contractAddress)
                        SuccessDeleteFragment(
                            uiState.txHash,
                            uiState.contractAddress,
                            uiState.formattedAmountReturned,
                            uiState.formattedGasUsed
                        ).show(requireActivity().supportFragmentManager, null)
                        dismiss()
                    }

                    is TradeInfoUIState.ShowBuyerReceivedBtns -> {
                        binding.btnItemReceived.visibility = View.VISIBLE
                        binding.btnIncorrectItem.visibility = View.VISIBLE
                    }

                    is TradeInfoUIState.HideBuyerReceivedBtns -> {
                        binding.btnItemReceived.visibility = View.GONE
                        binding.btnIncorrectItem.visibility = View.GONE
                    }

                    is TradeInfoUIState.ShowSellerDeliveredBtn -> {
                        binding.btnItemDelivered.visibility = View.VISIBLE
                    }

                    is TradeInfoUIState.HideSellerDeliveredBtn -> {
                        binding.btnItemDelivered.visibility = View.GONE
                    }

                    is TradeInfoUIState.IncorrectItem -> {
                        binding.tvBuyerReceivedStatus.text = uiState.status
                        binding.tvBuyerReceivedStatus.setTextColor(redColor)
                    }

                    is TradeInfoUIState.UpdateBuyerReceivedStatus -> {
                        binding.tvBuyerReceivedStatus.text = uiState.status
                        if (uiState.isDelivered) {
                            binding.tvBuyerReceivedStatus.setTextColor(greenColor)
                        }
                    }

                    is TradeInfoUIState.UpdateSellerDeliveryStatus -> {
                        binding.tvSellerDeliveryStatus.text = uiState.status
                        if (uiState.isDelivered) {
                            binding.tvSellerDeliveryStatus.setTextColor(greenColor)
                        }
                    }

                    is TradeInfoUIState.UpdateSellerReturnDepositStatus -> {
                        binding.tvReturnSellerStatus.text = uiState.status
                        binding.tvReturnSellerStatus.setTextColor(greenColor)
                    }

                    is TradeInfoUIState.UpdateBuyerReturnDepositStatus -> {
                        binding.tvReturnBuyerStatus.text = uiState.status
                        binding.tvReturnBuyerStatus.setTextColor(greenColor)
                    }

                    is TradeInfoUIState.UpdateSellerPayout -> {
                        binding.tvAmountPayedToSeller.text = uiState.status
                        binding.tvAmountPayedToSeller.setTextColor(greenColor)
                    }

                    is TradeInfoUIState.UpdateFeePerParty -> {
                        binding.tvFeesPerParty.text = uiState.status
                        binding.tvFeesPerParty.setTextColor(defaultTextColor)
                    }

                    is TradeInfoUIState.ShowSuccessfulTradeStatus -> {
                        binding.tvTradeStatus.visibility = View.VISIBLE
                        binding.tvTradeStatus.text = "Trade Successful"
                        binding.tvTradeStatus.setTextColor(greenColor)
                    }

                    is TradeInfoUIState.ShowIncorrectItemTradeStatus -> {
                        binding.tvTradeStatus.visibility = View.VISIBLE
                        binding.tvTradeStatus.text = "Deposits Locked"
                        binding.tvTradeStatus.setTextColor(redColor)
                    }

                    is TradeInfoUIState.ShowSettledTradeStatus -> {
                        binding.tvTradeStatus.visibility = View.VISIBLE
                        binding.tvTradeStatus.text = "Trade Settled"
                        binding.tvTradeStatus.setTextColor(greenColor)
                    }

                    is TradeInfoUIState.SuccessSettle -> {
                        SuccessSettleFragment(
                            uiState.contractAddress,
                            uiState.title,
                            uiState.status,
                            uiState.txHash,
                            uiState.formattedGasCost
                        ).show(requireActivity().supportFragmentManager, null)
                    }

                    is TradeInfoUIState.UpdateSellerSettleStatus -> {
                        if (uiState.hasRequestedToSettle) {
                            binding.tvSellerSettleStatus.text = "Seller has requested to settle"
                            binding.tvSellerSettleStatus.setTextColor(greenColor)
                        } else {
                            binding.tvSellerSettleStatus.text = "Seller has not requested to settle"
                            binding.tvSellerSettleStatus.setTextColor(defaultTextColor)
                        }
                    }

                    is TradeInfoUIState.UpdateBuyerSettleStatus -> {
                        if (uiState.hasRequestedToSettle) {
                            binding.tvBuyerSettleStatus.text = "Buyer has requested to settle"
                            binding.tvBuyerSettleStatus.setTextColor(greenColor)
                        } else {
                            binding.tvBuyerSettleStatus.text = "Buyer has not requested to settle"
                            binding.tvBuyerSettleStatus.setTextColor(defaultTextColor)
                        }
                    }

                    is TradeInfoUIState.ShowStepIndicatorProgress -> {
                        binding.progressStepIndicator.visibility = View.VISIBLE
                    }

                    is TradeInfoUIState.HideStepIndicatorProgress -> {
                        binding.progressStepIndicator.visibility = View.GONE
                    }

                    is TradeInfoUIState.HideSwipeRefreshProgress -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    is TradeInfoUIState.SuccessUpdateTradeStatus -> {
                        tradesViewModel.updateTrade(uiState.trade)
                    }
                }
            }
        }

        tradeInfoViewModel.setTradeInfo(true)
    }

    // @formatter:off
    private fun setCurrentStepIndicator(step: Int, showSettleStatus: Boolean) {
        // Common actions for all steps
        binding.tvStaticAwaitingDeposit.setTextColor(Color.WHITE)
        binding.layoutAwaitingDeposit.visibility = View.VISIBLE

        when (step) {
            1 -> {
                binding.circle1.setBackgroundResource(R.drawable.circle_holo_purple)
                binding.circle1Text.setTextColor(purpleColor)
            }
            2 -> {
                fillIndicatorOne()
                setupStepTwo()
            }
            3 -> {
                fillIndicatorOne()
                setupStepTwo()
                fillIndicatorTwo()
                binding.line2.setBackgroundColor(purpleColor)
                binding.circle3.setBackgroundResource(R.drawable.circle_filled_purple)
                binding.circle3Text.setTextColor(Color.WHITE)
                binding.tvStaticReturnDeposits.setTextColor(Color.WHITE)

                if(showSettleStatus){
                    binding.layoutSettleStatus.visibility = View.VISIBLE
                }else{
                    binding.layoutReturnDeposits.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupStepTwo() {
        binding.circle1.setBackgroundResource(R.drawable.circle_filled_purple)
        binding.circle1Text.setTextColor(Color.WHITE)
        binding.line1.setBackgroundColor(purpleColor)
        binding.tvStaticAwaitingItemDelivery.setTextColor(Color.WHITE)
        binding.circle2.setBackgroundResource(R.drawable.circle_holo_purple)
        binding.circle2Text.setTextColor(purpleColor)
        binding.layoutAwaitingItem.visibility = View.VISIBLE
    }
    // @formatter:on

    private fun fillIndicatorOne() {
        binding.circle1.setBackgroundResource(R.drawable.circle_filled_purple)
        binding.circle1Text.setTextColor(Color.WHITE)
    }

    private fun fillIndicatorTwo() {
        binding.circle2.setBackgroundResource(R.drawable.circle_filled_purple)
        binding.circle2Text.setTextColor(Color.WHITE)
    }

    private fun showProgressDeposit() {
        if (progressDeposit == null) {
            val builder = AlertDialog.Builder(requireContext(), R.style.alertDialogTheme)
            builder.setView(R.layout.progress_deposit)
            progressDeposit = builder.create().apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        progressDeposit?.show()
    }

    private fun hideProgressDeposit() {
        progressDeposit?.hide()
    }

    private fun showProgressDelivery() {
        if (progressDelivery == null) {
            val builder = AlertDialog.Builder(requireContext(), R.style.alertDialogTheme)
            builder.setView(R.layout.progress_delivery)
            progressDelivery = builder.create().apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        progressDelivery?.show()
    }

    private fun hideProgressDelivery() {
        progressDelivery?.hide()
    }

    private fun showProgressCancelTrade() {
        if (progressDeleteTrade == null) {
            val builder = AlertDialog.Builder(requireContext(), R.style.alertDialogTheme)
            builder.setView(R.layout.progress_delete_trade)
            progressDeleteTrade = builder.create().apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        progressDeleteTrade?.show()
    }

    private fun hideProgressCancelTrade() {
        progressDeleteTrade?.hide()
    }

    private fun showProgressRequestSettle() {
        if (progressRequestSettle == null) {
            val builder = AlertDialog.Builder(requireContext(), R.style.alertDialogTheme)
            builder.setView(R.layout.progress_request_settle)
            progressRequestSettle = builder.create().apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        progressRequestSettle?.show()
    }

    private fun hideProgressRequestSettle() {
        progressRequestSettle?.hide()
    }

    private fun openBrowser() {
        val url = Network.getNetworkById(trade.networkId).explorerUrl + trade.contractAddress
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }
}
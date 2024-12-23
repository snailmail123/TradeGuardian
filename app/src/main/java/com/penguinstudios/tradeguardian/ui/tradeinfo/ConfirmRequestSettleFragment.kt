package com.penguinstudios.tradeguardian.ui.tradeinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.penguinstudios.tradeguardian.databinding.ConfirmCancelTradeFragmentBinding
import com.penguinstudios.tradeguardian.databinding.ConfirmRequestSettleFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmRequestSettleFragment : DialogFragment() {

    private lateinit var binding: ConfirmRequestSettleFragmentBinding
    private val tradeInfoViewModel: TradeInfoViewModel by activityViewModels()

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
        binding = ConfirmRequestSettleFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            tradeInfoViewModel.settle()
            dismiss()
        }
    }
}
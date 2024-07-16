package com.penguinstudios.tradeguardian.ui.tradeinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.databinding.ConfirmItemReceivedFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmItemReceivedFragment(
    private val isCorrectItem: Boolean
) : DialogFragment() {

    private lateinit var binding: ConfirmItemReceivedFragmentBinding
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
        binding = ConfirmItemReceivedFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            if (isCorrectItem) {
                tradeInfoViewModel.correctItemReceived()
            } else {
                tradeInfoViewModel.incorrectItemReceived()
            }
            dismiss()
        }

        if (isCorrectItem) {
            binding.tvTitle.text = "Correct Item Received"
            binding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green_400
                )
            )
            binding.tvDepositDescription.text = getString(R.string.deposit_description_correct_item)

            Glide.with(this)
                .load(R.drawable.ic_check_circle)
                .centerInside()
                .into(binding.ivTitleIcon)
        } else {
            binding.tvTitle.text = "Incorrect Item or Not Received"
            binding.tvTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red_400
                )
            )
            binding.tvDepositDescription.text =
                getString(R.string.deposit_description_incorrect_item)

            Glide.with(this)
                .load(R.drawable.alert_2)
                .centerInside()
                .into(binding.ivTitleIcon)
        }
    }
}
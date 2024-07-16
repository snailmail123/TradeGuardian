package com.penguinstudios.tradeguardian.ui.wallet

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.penguinstudios.tradeguardian.data.LocalRepository
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.databinding.QrWalletFragmentBinding
import com.penguinstudios.tradeguardian.util.ClipboardUtil
import com.penguinstudios.tradeguardian.util.SpacingUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QrWalletFragment(
    private val walletAddress: String
) : DialogFragment() {

    companion object {
        private const val QR_SIZE = 150
    }

    @Inject
    lateinit var localRepository: LocalRepository

    private lateinit var binding: QrWalletFragmentBinding

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
        binding = QrWalletFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvWalletAddress.text = walletAddress
        binding.tvTitle.text = localRepository.getSelectedNetwork().networkName

        val bitmap = createQrCode(walletAddress)
        Glide.with(this)
            .load(bitmap)
            .centerInside()
            .into(binding.ivWalletQr)

        binding.layoutWalletAddress.setOnClickListener {
            ClipboardUtil.copyText(requireContext(), walletAddress)
            Toast.makeText(requireContext(), "Address copied", Toast.LENGTH_SHORT).show()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun createQrCode(walletAddress: String): Bitmap {
        val qrWidthHeight = SpacingUtil.convertIntToDP(requireContext(), QR_SIZE)
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(
            walletAddress,
            BarcodeFormat.QR_CODE,
            qrWidthHeight,
            qrWidthHeight
        )
    }
}
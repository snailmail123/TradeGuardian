package com.penguinstudios.tradeguardian.ui.createwallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.penguinstudios.tradeguardian.databinding.SecureWalletFragmentBinding
import com.penguinstudios.tradeguardian.ui.createwallet.adapters.ViewMnemonicAdapter
import com.penguinstudios.tradeguardian.ui.createwallet.util.MnemonicItemDecoration
import com.penguinstudios.tradeguardian.util.SpacingUtil
import com.penguinstudios.tradeguardian.ui.createwallet.viewmodel.CreateWalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecureWalletFragment : Fragment() {

    companion object {
        private const val GRID_VIEW_SPACING = 16
        private const val GRID_VIEW_COLUMN_COUNT = 2
    }

    private lateinit var binding: SecureWalletFragmentBinding
    private val viewModel: CreateWalletViewModel by viewModels({requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SecureWalletFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        val mnemonicAdapter = ViewMnemonicAdapter(viewModel.getMnemonicList())
        val spacing = SpacingUtil.convertIntToDP(requireContext(), GRID_VIEW_SPACING)

        binding.recyclerView.apply {
            addItemDecoration(MnemonicItemDecoration(GRID_VIEW_COLUMN_COUNT, spacing, true))
            layoutManager = GridLayoutManager(requireContext(), GRID_VIEW_COLUMN_COUNT)
            adapter = mnemonicAdapter
        }
    }

    private fun setupButtons() {
        binding.btnView.setOnClickListener {
            binding.layoutTapToReveal.visibility = View.GONE
        }

        binding.btnContinue.setOnClickListener {
            if (binding.layoutTapToReveal.visibility == View.VISIBLE) {
                Toast.makeText(
                    requireContext(),
                    "View your secret phrase and write it down",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.onContinue()
            }
        }
    }
}

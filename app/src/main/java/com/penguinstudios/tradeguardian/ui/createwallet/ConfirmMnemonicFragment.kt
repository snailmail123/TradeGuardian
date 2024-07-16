package com.penguinstudios.tradeguardian.ui.createwallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.penguinstudios.tradeguardian.R
import com.penguinstudios.tradeguardian.databinding.ConfirmMnemonicFragmentBinding
import com.penguinstudios.tradeguardian.ui.createwallet.adapters.AvailableWordsAdapter
import com.penguinstudios.tradeguardian.ui.createwallet.adapters.SelectedWordsAdapter
import com.penguinstudios.tradeguardian.ui.createwallet.util.MnemonicItemDecoration
import com.penguinstudios.tradeguardian.util.SpacingUtil
import com.penguinstudios.tradeguardian.ui.createwallet.viewmodel.CreateWalletUIState
import com.penguinstudios.tradeguardian.ui.createwallet.viewmodel.CreateWalletViewModel
import com.penguinstudios.tradeguardian.ui.createwallet.viewmodel.CreateWalletViewModel.Companion.NUM_WORDS_MNEMONIC
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConfirmMnemonicFragment : Fragment(), SelectedWordsAdapter.Callback,
    AvailableWordsAdapter.Callback {

    companion object {
        private const val GRID_VIEW_SPACING = 16
        private const val SELECTED_WORDS_GRID_COLUMN_COUNT = 2
        private const val AVAILABLE_WORDS_GRID_COLUMN_COUNT = 3
    }

    private lateinit var binding: ConfirmMnemonicFragmentBinding
    private lateinit var selectedWordsAdapter: SelectedWordsAdapter
    private lateinit var availableWordsAdapter: AvailableWordsAdapter
    private val viewModel: CreateWalletViewModel by viewModels({requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConfirmMnemonicFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()

        binding.btnCompleteBackup.setOnClickListener {
            viewModel.onCompleteBackup()
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is CreateWalletUIState.UpdateSelectedWordsAdapter -> {
                        selectedWordsAdapter.notifyItemChanged(uiState.adapterPosition)
                    }

                    is CreateWalletUIState.UpdateAvailableWordsAdapter -> {
                        availableWordsAdapter.notifyItemChanged(uiState.adapterPosition)
                    }

                    is CreateWalletUIState.UpdateSelectedWordsAdapterNextTarget -> {
                        selectedWordsAdapter.setNextTarget(uiState.adapterPosition)
                    }

                    is CreateWalletUIState.CorrectMnemonicEntered -> {
                        binding.selectedWordsRecycler.setBackgroundResource(R.drawable.bgr_selected_words_correct)
                        binding.btnCompleteBackup.isEnabled = true
                    }

                    is CreateWalletUIState.IncorrectMnemonicEntered -> {
                        binding.selectedWordsRecycler.setBackgroundResource(R.drawable.bgr_selected_words_incorrect)
                        binding.btnCompleteBackup.isEnabled = false
                    }

                    is CreateWalletUIState.EntireMnemonicNotEntered -> {
                        binding.selectedWordsRecycler.setBackgroundResource(R.drawable.bgr_selected_words_default)
                        binding.btnCompleteBackup.isEnabled = false
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        val spacing = SpacingUtil.convertIntToDP(requireContext(), GRID_VIEW_SPACING)

        setupSelectedWordsRecyclerView(spacing)
        setupAvailableWordsRecyclerView(spacing)
    }

    private fun setupSelectedWordsRecyclerView(spacing: Int) {
        val selectedWordsLayoutManager =
            GridLayoutManager(requireContext(), SELECTED_WORDS_GRID_COLUMN_COUNT)

        selectedWordsAdapter = SelectedWordsAdapter(
            viewModel.selectedWordsMap, NUM_WORDS_MNEMONIC, this
        )

        binding.selectedWordsRecycler.apply {
            addItemDecoration(
                MnemonicItemDecoration(
                    SELECTED_WORDS_GRID_COLUMN_COUNT,
                    spacing,
                    true
                )
            )
            layoutManager = selectedWordsLayoutManager
            adapter = selectedWordsAdapter
            itemAnimator = null
        }
    }

    private fun setupAvailableWordsRecyclerView(spacing: Int) {
        val availableWordsLayoutManager = GridLayoutManager(
            requireContext(), AVAILABLE_WORDS_GRID_COLUMN_COUNT
        )

        availableWordsAdapter = AvailableWordsAdapter(
            viewModel.getShuffledMnemonicList(), viewModel.selectedWordsMap, this
        )

        binding.availableWordsRecycler.apply {
            addItemDecoration(
                MnemonicItemDecoration(AVAILABLE_WORDS_GRID_COLUMN_COUNT, spacing, true)
            )
            layoutManager = availableWordsLayoutManager
            adapter = availableWordsAdapter
        }
    }

    override fun onSelectedWordsClick(adapterPosition: Int) {
        viewModel.onSelectedWordClick(adapterPosition)
    }

    override fun onAvailableWordsClick(adapterPosition: Int) {
        viewModel.onAvailableWordClick(adapterPosition)
    }
}

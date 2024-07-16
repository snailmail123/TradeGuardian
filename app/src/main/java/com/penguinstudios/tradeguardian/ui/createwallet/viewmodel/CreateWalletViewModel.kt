package com.penguinstudios.tradeguardian.ui.createwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.validator.PasswordStrengthEvaluator
import com.penguinstudios.tradeguardian.data.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.TreeMap
import javax.inject.Inject

@HiltViewModel
class CreateWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val filesDir: File
) : ViewModel() {

    companion object {
        const val NUM_WORDS_MNEMONIC = 12
        private const val ALL_MNEMONIC_SPACES_FILLED = -1
    }

    private val _uiState = MutableSharedFlow<CreateWalletUIState>()
    val uiState = _uiState.asSharedFlow()
    val selectedWordsMap: MutableMap<Int, String> = TreeMap()
    private var nextWordIndex = 0;

    fun createPassword(password: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    walletRepository.createWallet(password, filesDir)
                }
                _uiState.emit(CreateWalletUIState.SuccessCreateWallet)
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(CreateWalletUIState.Error(e.message.toString()))
            }
        }
    }

    fun onCreatePasswordClick(password: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                walletRepository.validateUserPasswordInput(password, confirmPassword)
                _uiState.emit(CreateWalletUIState.ValidPassword(password))
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(CreateWalletUIState.Error(e.message.toString()))
            }
        }
    }

    fun onContinue() {
        viewModelScope.launch {
            _uiState.emit(CreateWalletUIState.SecureWalletComplete)
        }
    }

    fun getMnemonicList(): List<String> {
        return walletRepository.mnemonicList
    }

    fun getShuffledMnemonicList(): List<String> {
        return walletRepository.shuffledMnemonicList
    }

    fun onSelectedWordClick(adapterPosition: Int) {
        viewModelScope.launch {
            //The user clicked on an empty space, move pointer to empty space
            if (!selectedWordsMap.containsKey(adapterPosition)) {
                nextWordIndex = adapterPosition
                _uiState.emit(
                    CreateWalletUIState.UpdateSelectedWordsAdapterNextTarget(
                        adapterPosition
                    )
                )
                return@launch
            }

            _uiState.emit(CreateWalletUIState.UpdateSelectedWordsAdapter(adapterPosition))

            selectedWordsMap[adapterPosition]?.let { word ->
                val shuffledPosition = getPositionOfWordInShuffledMnemonicList(word)
                _uiState.emit(CreateWalletUIState.UpdateAvailableWordsAdapter(shuffledPosition))
            } ?: throw IllegalStateException("Expected non-null value for adapterPosition")

            //Updates the outline back to default because only 11 words are now selected
            if (selectedWordsMap.size == NUM_WORDS_MNEMONIC) {
                _uiState.emit(CreateWalletUIState.EntireMnemonicNotEntered)
            }

            //.remove must be called after updating the available words adapter
            //.remove must also be called after checking if user has unselected an word
            //to update the outline color around the selected words
            //and be called before updating the next item click target
            selectedWordsMap.remove(adapterPosition)

            //All 12 words are selected. This updates the purple dashed line if the user deselects one
            if (nextWordIndex == ALL_MNEMONIC_SPACES_FILLED) {
                nextWordIndex = getNextAvailableWordIndex()
                _uiState.emit(CreateWalletUIState.UpdateSelectedWordsAdapterNextTarget(nextWordIndex))
            }
        }
    }

    private fun getPositionOfWordInShuffledMnemonicList(word: String): Int {
        val position = walletRepository.shuffledMnemonicList.indexOfFirst { it == word }
        if (position != -1) {
            return position
        }
        throw IllegalStateException("Could not find word in shuffled list")
    }

    private fun getNextAvailableWordIndex(): Int {
        for (i in 0 until walletRepository.mnemonicList.size) {
            if (!selectedWordsMap.containsKey(i)) {
                return i
            }
        }
        return ALL_MNEMONIC_SPACES_FILLED
    }

    fun onAvailableWordClick(adapterPosition: Int) {
        viewModelScope.launch {
            val shuffledMnemonicList = walletRepository.shuffledMnemonicList
            val word = shuffledMnemonicList[adapterPosition]

            if (selectedWordsMap.containsValue(word)) {
                return@launch
            }

            selectedWordsMap[nextWordIndex] = word
            nextWordIndex = getNextAvailableWordIndex()

            _uiState.emit(CreateWalletUIState.UpdateSelectedWordsAdapter(nextWordIndex))
            _uiState.emit(CreateWalletUIState.UpdateAvailableWordsAdapter(adapterPosition))
            _uiState.emit(CreateWalletUIState.UpdateSelectedWordsAdapterNextTarget(nextWordIndex))

            if (selectedWordsMap.size == NUM_WORDS_MNEMONIC) {
                //All words have been entered
                if (isEnteredMnemonicCorrect(walletRepository.getMnemonic(), selectedWordsMap)) {
                    _uiState.emit(CreateWalletUIState.CorrectMnemonicEntered)
                } else {
                    _uiState.emit(CreateWalletUIState.IncorrectMnemonicEntered)
                }
            }
        }
    }

    private fun isEnteredMnemonicCorrect(mnemonic: String, map: Map<Int, String>): Boolean {
        return mnemonic == map.values.joinToString(separator = " ")
    }

    fun onCompleteBackup() {
        viewModelScope.launch {
            _uiState.emit(CreateWalletUIState.CompleteBackup)
        }
    }

    fun onNewPasswordTextChange(s: String) {
        viewModelScope.launch {
            val strength = PasswordStrengthEvaluator.evaluatePasswordStrength(s)
            _uiState.emit(CreateWalletUIState.UpdatePasswordStrength(strength))
        }
    }

    override fun onCleared() {
        super.onCleared()
        walletRepository.clearMnemonicLists()
    }
}
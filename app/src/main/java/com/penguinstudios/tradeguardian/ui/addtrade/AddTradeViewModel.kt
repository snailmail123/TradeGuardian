package com.penguinstudios.tradeguardian.ui.addtrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguinstudios.tradeguardian.data.LocalRepository
import com.penguinstudios.tradeguardian.data.RemoteRepository
import com.penguinstudios.tradeguardian.data.WalletRepository
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.data.model.Trade
import com.penguinstudios.tradeguardian.data.model.UserRole
import com.penguinstudios.tradeguardian.data.usecase.ContractInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.web3j.tx.exceptions.ContractCallException
import timber.log.Timber
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class AddTradeViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val walletRepository: WalletRepository,
    private val localRepository: LocalRepository,
    private val contractInfoUseCase: ContractInfoUseCase
) : ViewModel() {

    private val _uiState = MutableSharedFlow<AddTradeUIState>()
    val uiState = _uiState.asSharedFlow()

    fun onConfirm(contractAddress: String) {
        viewModelScope.launch {
            try {
                if (localRepository.tradeExists(contractAddress)) {
                    throw Exception("Trade already exists")
                }

                _uiState.emit(AddTradeUIState.ShowProgressAddTrade)

                val buyerAddress = contractInfoUseCase.getBuyerAddress(contractAddress)
                val sellerAddress = contractInfoUseCase.getSellerAddress(contractAddress)

                if (!contractInfoUseCase.isUserInvolvedInTrade(buyerAddress, sellerAddress)) {
                    throw Exception("This trade does not belong to you")
                }

                val trade = createTrade(contractAddress, buyerAddress, sellerAddress)

                localRepository.insertTrade(trade)

                _uiState.emit(AddTradeUIState.SuccessAddTrade)
            } catch (e: NoSuchMethodError) {
                //Triggered when an invalid string that does not match an address
                Timber.e(e)
                _uiState.emit(AddTradeUIState.Error("Invalid contract address"))
            } catch (e: ContractCallException) {
                //Triggered when a wallet address is entered instead of a contract address
                Timber.e(e)
                _uiState.emit(AddTradeUIState.Error("Invalid contract address"))
            } catch (e: Exception) {
                Timber.e(e)
                _uiState.emit(AddTradeUIState.Error(e.message.toString()))
            } finally {
                _uiState.emit(AddTradeUIState.HideProgressAddTrade)
            }
        }
    }

    private suspend fun createTrade(
        contractAddress: String,
        buyerAddress: String,
        sellerAddress: String
    ): Trade {
        val network = localRepository.getSelectedNetwork()
        val contractStatusId = contractInfoUseCase.getContractStatus(contractAddress)
        val dateCreated = contractInfoUseCase.getDateCreatedSeconds(contractAddress)
        val itemPriceWei = contractInfoUseCase.getItemPriceWei(contractAddress)
        val description = contractInfoUseCase.getDescription(contractAddress)
        val userWalletAddress = walletRepository.credentials.address

        val (userRole, counterPartyRole, counterPartyAddress) =
            determineRoles(userWalletAddress, buyerAddress, sellerAddress)

        return Trade.builder()
            .network(network)
            .contractAddress(contractAddress)
            .contractStatus(contractStatusId)
            .dateCreatedSeconds(dateCreated)
            .itemPriceWei(itemPriceWei)
            .gasCostWei(BigInteger.valueOf(-1))
            .userRole(userRole)
            .userWalletAddress(userWalletAddress)
            .counterPartyRole(counterPartyRole)
            .counterPartyWalletAddress(counterPartyAddress)
            .description(description)
            .build()
    }

    private fun determineRoles(
        userWalletAddress: String,
        buyerAddress: String,
        sellerAddress: String
    ): Triple<UserRole, UserRole, String> {
        return if (userWalletAddress == buyerAddress) {
            Triple(UserRole.BUYER, UserRole.SELLER, sellerAddress)
        } else {
            Triple(UserRole.SELLER, UserRole.BUYER, buyerAddress)
        }
    }
}
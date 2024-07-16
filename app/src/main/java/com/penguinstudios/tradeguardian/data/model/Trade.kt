package com.penguinstudios.tradeguardian.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penguinstudios.tradeguardian.util.Constants
import timber.log.Timber
import java.math.BigInteger

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "network") val networkId: Int,
    @ColumnInfo(name = "contract_address") val contractAddress: String,
    @ColumnInfo(name = "contract_status_id") val contractStatusId: Int,
    @ColumnInfo(name = "date_created_millis") val dateCreatedMillis: Long,
    @ColumnInfo(name = "withdrawal_eligibility_date") val withdrawEligibilityDate: Long,
    @ColumnInfo(name = "item_price_wei") val itemPriceWei: Long,
    @ColumnInfo(name = "gas_cost_wei") val gasCostWei: Long,
    @ColumnInfo(name = "user_role_id") val userRoleId: Int,
    @ColumnInfo(name = "user_wallet_address") val userWalletAddress: String,
    @ColumnInfo(name = "counter_party_role_id") val counterPartyRoleId: Int,
    @ColumnInfo(name = "counter_party_wallet_address") val counterPartyWalletAddress: String,
    @ColumnInfo(name = "description") val description: String
) {
    interface NetworkStep {
        fun network(network: Network): ContractAddressStep
    }

    interface ContractAddressStep {
        fun contractAddress(address: String): ContractStatusIdStep
    }

    interface ContractStatusIdStep {
        fun contractStatus(status: ContractStatus): DateCreatedMillisStep
    }

    interface DateCreatedMillisStep {
        fun dateCreatedSeconds(dateCreatedSeconds: BigInteger): ItemPriceWeiStep
    }

    interface ItemPriceWeiStep {
        fun itemPriceWei(priceWei: BigInteger): GasCostWeiStep
    }

    interface GasCostWeiStep {
        fun gasCostWei(costWei: BigInteger): UserRoleIdStep
    }

    interface UserRoleIdStep {
        fun userRole(role: UserRole): UserWalletAddressStep
    }

    interface UserWalletAddressStep {
        fun userWalletAddress(address: String): CounterPartyRoleIdStep
    }

    interface CounterPartyRoleIdStep {
        fun counterPartyRole(role: UserRole): CounterPartyWalletAddressStep
    }

    interface CounterPartyWalletAddressStep {
        fun counterPartyWalletAddress(address: String): DescriptionStep
    }

    interface DescriptionStep {
        fun description(description: String): BuildStep
    }

    interface BuildStep {
        fun build(): Trade
    }

    companion object {
        fun builder(): NetworkStep = Builder()
    }

    private class Builder : NetworkStep, ContractAddressStep, ContractStatusIdStep,
        DateCreatedMillisStep, ItemPriceWeiStep, GasCostWeiStep, UserRoleIdStep,
        UserWalletAddressStep,
        CounterPartyRoleIdStep, CounterPartyWalletAddressStep, DescriptionStep, BuildStep {

        private var id: Int = 0 //Placeholder, will be replaced by Room upon insertion
        private var networkId: Int = 0
        private lateinit var contractAddress: String
        private var contractStatusId: Int = 0
        private var dateCreatedMillis: Long = 0L
        private var withdrawEligibilityDateMillis: Long = 0L
        private var itemPriceWei: Long = 0L
        private var gasCostWei: Long = 0L
        private var userRoleId: Int = 0
        private lateinit var userWalletAddress: String
        private var counterPartyRoleId: Int = 0
        private lateinit var counterPartyWalletAddress: String
        private lateinit var description: String

        override fun network(network: Network) = apply {
            this.networkId = network.id
        }

        override fun contractAddress(address: String) = apply {
            this.contractAddress = address.lowercase()
        }

        override fun contractStatus(status: ContractStatus) = apply {
            this.contractStatusId = status.id
        }

        override fun dateCreatedSeconds(dateCreatedSeconds: BigInteger) = apply {
            //Date format requires milliseconds
            this.dateCreatedMillis = dateCreatedSeconds.toLong() * Constants.MILLIS_PER_SECOND
            this.withdrawEligibilityDateMillis =
                (dateCreatedSeconds.toLong() + Constants.TWO_HOURS_IN_SECONDS) * Constants.MILLIS_PER_SECOND
        }

        override fun itemPriceWei(priceWei: BigInteger) = apply {
            this.itemPriceWei = priceWei.toLong()
        }

        override fun gasCostWei(costWei: BigInteger) = apply {
            this.gasCostWei = costWei.toLong()
        }

        override fun userRole(role: UserRole) = apply {
            this.userRoleId = role.id
        }

        override fun userWalletAddress(address: String) = apply {
            this.userWalletAddress = address
        }

        override fun counterPartyRole(role: UserRole) = apply {
            this.counterPartyRoleId = role.id
        }

        override fun counterPartyWalletAddress(address: String) = apply {
            this.counterPartyWalletAddress = address.lowercase()
        }

        override fun description(description: String) = apply {
            this.description = description
        }

        override fun build(): Trade {
            return Trade(
                id, //Will be ignored by Room on insert due to autoGenerate
                networkId,
                contractAddress,
                contractStatusId,
                dateCreatedMillis,
                withdrawEligibilityDateMillis,
                itemPriceWei,
                gasCostWei,
                userRoleId,
                userWalletAddress,
                counterPartyRoleId,
                counterPartyWalletAddress,
                description
            )
        }
    }
}

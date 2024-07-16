package com.penguinstudios.tradeguardian

import com.penguinstudios.tradeguardian.data.model.ContractDeployment
import com.penguinstudios.tradeguardian.data.model.Network
import com.penguinstudios.tradeguardian.data.model.UserRole
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

import java.math.BigDecimal

class ContractDeploymentItemPriceTest {

    private lateinit var builder: ContractDeployment.ItemPriceStep

    companion object {
        const val DESCRIPTION: String = "Valid description"
        const val VALID_USER_WALLET_ADDRESS: String = "0xee2b0e42f91f2fa64e84a206062f0cb59339ff85"
        const val VALID_COUNTERPARTY_ADDRESS: String = "0x34b3bfb660d3e2cade3dcd9a2332020a412ed91c"
    }

    @Before
    fun setUp() {
        builder = ContractDeployment.builder()
            .network(Network.TEST_NET)
            .feeRecipientAddress("0x64ed56c19182aa10d4c0dc0db979d9c407c6b0c3")
            .userRole(UserRole.BUYER)
    }

    @Test
    fun `throw exception for invalid item price format`() {
        assertThrows(IllegalArgumentException::class.java) {
            builder
                .itemPrice("notANumber")
                .description(DESCRIPTION)
                .userWalletAddress(VALID_USER_WALLET_ADDRESS)
                .counterPartyAddress(VALID_COUNTERPARTY_ADDRESS)
                .build()
        }
    }

    @Test
    fun `throw exception for invalid item price of 0`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            builder
                .itemPrice("0")
                .description(DESCRIPTION)
                .userWalletAddress(VALID_USER_WALLET_ADDRESS)
                .counterPartyAddress(VALID_COUNTERPARTY_ADDRESS)
                .build()
        }

        assertEquals("Invalid item price", exception.message)
    }

    @Test
    fun `object creation is successful with valid item price 0_1`() {
        val deployment = builder
            .itemPrice("0.1")
            .description(DESCRIPTION)
            .userWalletAddress(VALID_USER_WALLET_ADDRESS)
            .counterPartyAddress(VALID_COUNTERPARTY_ADDRESS)
            .build()

        assertEquals(BigDecimal("0.1"), deployment.itemPriceDecimal)
    }

    @Test
    fun `object creation is successful with valid item price _1`() {
        val deployment = builder
            .itemPrice(".1")
            .description(DESCRIPTION)
            .userWalletAddress(VALID_USER_WALLET_ADDRESS)
            .counterPartyAddress(VALID_COUNTERPARTY_ADDRESS)
            .build()

        assertEquals(BigDecimal("0.1"), deployment.itemPriceDecimal)
    }

    @Test
    fun `throw exception for invalid item price of ___1`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            builder
                .itemPrice("...1")
                .description(DESCRIPTION)
                .userWalletAddress(VALID_USER_WALLET_ADDRESS)
                .counterPartyAddress(VALID_COUNTERPARTY_ADDRESS)
                .build()
        }

        assertEquals("Invalid item price, not a number", exception.message)
    }

    @Test
    fun `throw exception for invalid item price of 1_`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            builder
                .itemPrice("1.")
                .description(DESCRIPTION)
                .userWalletAddress(VALID_USER_WALLET_ADDRESS)
                .counterPartyAddress(VALID_COUNTERPARTY_ADDRESS)
                .build()
        }

        assertEquals("Invalid item price: trailing decimal point", exception.message)
    }
}

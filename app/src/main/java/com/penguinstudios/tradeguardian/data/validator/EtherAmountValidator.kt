package com.penguinstudios.tradeguardian.data.validator

import java.math.BigDecimal

object EtherAmountValidator {

    fun validateAndConvert(amount: String): BigDecimal {
        require(amount.isNotEmpty()) { "No amount entered" }

        if (amount.startsWith("0") && !amount.matches(Regex("^0\\.\\d+$"))) {
            throw IllegalArgumentException("Invalid amount")
        }

        if (amount.matches(Regex(".*\\.$"))) {
            throw IllegalArgumentException("Invalid amount: trailing decimal point")
        }

        val itemPriceDecimal = try {
            BigDecimal(amount)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid item price, not a number")
        }

        require(itemPriceDecimal > BigDecimal.ZERO) { "Item price must be greater than 0" }

        return itemPriceDecimal
    }
}


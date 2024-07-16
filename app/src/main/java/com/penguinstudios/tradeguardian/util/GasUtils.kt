package com.penguinstudios.tradeguardian.util

import java.math.BigInteger

object GasUtils {

    /**
     * Calculates the margin for a given value.
     *
     * @param originalValue The original value to calculate the margin for.
     * @param percentMargin The percentage margin to apply.
     * @return The calculated margin.
     */
    fun calculateMargin(originalValue: BigInteger, percentMargin: Int): BigInteger {
        return originalValue.multiply(BigInteger.valueOf(percentMargin.toLong()))
            .divide(BigInteger.valueOf(100))
    }
}
package com.penguinstudios.tradeguardian.data.validator

import com.penguinstudios.tradeguardian.ui.createwallet.password.PasswordStrength

object PasswordStrengthEvaluator {

    fun evaluatePasswordStrength(password: String): PasswordStrength {
        val strongPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*_]).{12,}$"
        if (Regex(strongPattern).matches(password)) {
            return PasswordStrength.STRONG
        }

        val mediumPattern = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        if (Regex(mediumPattern).matches(password)) {
            return PasswordStrength.MEDIUM
        }

        return PasswordStrength.WEAK
    }
}

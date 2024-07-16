package com.penguinstudios.tradeguardian.data.validator

object CreatePasswordValidator {

    fun String.isValidPassword() {
        require(this.length >= 8) { "The password must be at least 8 characters long." }
    }

    fun String.isSameAs(confirmPassword: String) {
        require(this == confirmPassword) { "Passwords do not match." }
    }

}

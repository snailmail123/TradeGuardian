package com.penguinstudios.tradeguardian

import com.penguinstudios.tradeguardian.data.validator.CreatePasswordValidator.isSameAs
import com.penguinstudios.tradeguardian.data.validator.CreatePasswordValidator.isValidPassword
import org.junit.Test
import org.junit.Assert.assertThrows

class CreatePasswordValidatorTest {

    @Test
    fun `isValidPassword with valid password passes`() {
        val password = "password123"
        password.isValidPassword()
    }

    @Test
    fun `isValidPassword with short password throws exception`() {
        val password = "short"
        assertThrows(IllegalArgumentException::class.java) {
            password.isValidPassword()
        }
    }

    @Test
    fun `isSameAs with matching passwords passes`() {
        val password = "password123"
        val confirmPassword = "password123"
        password.isSameAs(confirmPassword)
    }

    @Test
    fun `isSameAs with non-matching passwords throws exception`() {
        val password = "password123"
        val confirmPassword = "differentPassword"
        assertThrows(IllegalArgumentException::class.java) {
            password.isSameAs(confirmPassword)
        }
    }
}

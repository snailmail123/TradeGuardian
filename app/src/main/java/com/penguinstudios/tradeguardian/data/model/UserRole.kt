package com.penguinstudios.tradeguardian.data.model

enum class UserRole(val id: Int, val roleName: String) {
    SELLER(0, "Seller"),
    BUYER(1, "Buyer");

    companion object {
        fun getUserRoleById(id: Int): UserRole {
            return UserRole.values().firstOrNull { it.id == id }
                ?: throw IllegalStateException("Invalid id: $id")
        }
    }
}

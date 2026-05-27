package com.dionomy.academy.domain

data class MakeupPolicy(
    val enabled: Boolean,
    val expiresInDays: Int,
    val maxCount: Int,
) {
    init {
        require(expiresInDays >= 0)
        require(maxCount >= 0)
    }
}

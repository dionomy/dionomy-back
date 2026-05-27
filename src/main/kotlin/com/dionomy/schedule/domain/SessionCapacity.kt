package com.dionomy.schedule.domain

data class SessionCapacity(
    val current: Int,
    val maximum: Int,
) {
    init {
        require(current >= 0)
        require(maximum > 0)
        require(current <= maximum)
    }
}

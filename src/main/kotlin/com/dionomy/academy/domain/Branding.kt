package com.dionomy.academy.domain

data class Branding(
    val logoUrl: String?,
    val mainColor: String,
) {
    init {
        require(mainColor.matches(Regex("^#[0-9A-Fa-f]{6}$")))
    }
}

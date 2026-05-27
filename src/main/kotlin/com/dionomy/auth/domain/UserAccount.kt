package com.dionomy.auth.domain

import java.util.UUID

data class UserAccount(
    val id: UUID,
    val tenantId: UUID?,
    val name: String,
    val role: UserRole,
)

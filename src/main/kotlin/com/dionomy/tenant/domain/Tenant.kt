package com.dionomy.tenant.domain

import java.util.UUID

data class Tenant(
    val id: UUID,
    val name: String,
    val status: TenantStatus,
)

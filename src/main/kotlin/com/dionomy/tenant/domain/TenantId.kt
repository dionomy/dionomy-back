package com.dionomy.tenant.domain

import java.util.UUID

@JvmInline
value class TenantId(
    val value: UUID,
)

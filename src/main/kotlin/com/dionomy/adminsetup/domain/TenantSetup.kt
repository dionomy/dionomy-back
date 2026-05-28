package com.dionomy.adminsetup.domain

import com.dionomy.tenant.domain.TenantStatus
import java.time.LocalDateTime
import java.util.UUID

class TenantSetup(
    val id: UUID,
    val academyName: String,
    val ownerContact: String,
    val mainColor: String,
    val tenantStatus: TenantStatus = TenantStatus.ACTIVE,
    val buildStatus: WhiteLabelBuildStatus = WhiteLabelBuildStatus.QUEUED,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(academyName.isNotBlank())
        require(ownerContact.isNotBlank())
        require(mainColor.matches(Regex("^#[0-9A-Fa-f]{6}$")))
    }
}

enum class WhiteLabelBuildStatus {
    QUEUED,
    BUILDING,
    COMPLETED,
    FAILED,
}

package com.dionomy.company.domain

import java.time.LocalDateTime
import java.util.UUID

class DemoRequest(
    val id: UUID,
    val academyName: String,
    val businessType: String,
    val academySize: String,
    val contact: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(academyName.isNotBlank())
        require(contact.isNotBlank())
    }
}

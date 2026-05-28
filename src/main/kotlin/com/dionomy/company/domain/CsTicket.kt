package com.dionomy.company.domain

import java.time.LocalDateTime
import java.util.UUID

class CsTicket(
    val id: UUID,
    val title: String,
    val body: String,
    val contact: String,
    val status: CsTicketStatus = CsTicketStatus.OPEN,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(title.isNotBlank())
        require(body.isNotBlank())
    }
}

enum class CsTicketStatus {
    OPEN,
    ANSWERED,
    CLOSED,
}

package com.dionomy.schedule.domain

import java.time.LocalDateTime
import java.util.UUID

class ClassSession(
    val id: UUID,
    val tenantId: UUID,
    val title: String,
    val type: ClassType,
    val teacherId: UUID,
    val placeId: UUID?,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val capacity: SessionCapacity,
) {
    init {
        require(title.isNotBlank())
        require(endsAt.isAfter(startsAt))
    }
}

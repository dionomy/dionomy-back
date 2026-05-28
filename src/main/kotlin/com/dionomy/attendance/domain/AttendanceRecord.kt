package com.dionomy.attendance.domain

import java.time.LocalDateTime
import java.util.UUID

class AttendanceRecord(
    val id: UUID,
    val tenantId: UUID,
    val sessionId: UUID,
    val studentId: UUID,
    val status: AttendanceStatus,
    val checkedByTeacherId: UUID,
    val checkedAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(sessionId != studentId)
    }
}

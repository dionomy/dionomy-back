package com.dionomy.absence.domain

import java.time.LocalDateTime
import java.util.UUID

class AbsenceRequest(
    val id: UUID,
    val tenantId: UUID,
    val studentId: UUID,
    val sessionId: UUID,
    val reason: String,
    val desiredResult: AbsenceDesiredResult,
    private var statusValue: AbsenceRequestStatus = AbsenceRequestStatus.PENDING,
    val requestedAt: LocalDateTime = LocalDateTime.now(),
    private var resolvedAtValue: LocalDateTime? = null,
) {
    init {
        require(reason.isNotBlank())
    }

    val status: AbsenceRequestStatus
        get() = statusValue

    val resolvedAt: LocalDateTime?
        get() = resolvedAtValue

    fun approve() {
        require(statusValue == AbsenceRequestStatus.PENDING)
        statusValue = AbsenceRequestStatus.APPROVED
        resolvedAtValue = LocalDateTime.now()
    }

    fun reject() {
        require(statusValue == AbsenceRequestStatus.PENDING)
        statusValue = AbsenceRequestStatus.REJECTED
        resolvedAtValue = LocalDateTime.now()
    }
}

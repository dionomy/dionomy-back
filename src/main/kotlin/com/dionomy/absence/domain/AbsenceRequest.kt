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
    private var resolvedTargetSessionIdValue: UUID? = null,
) {
    init {
        require(reason.isNotBlank())
    }

    val status: AbsenceRequestStatus
        get() = statusValue

    val resolvedAt: LocalDateTime?
        get() = resolvedAtValue

    val resolvedTargetSessionId: UUID?
        get() = resolvedTargetSessionIdValue

    fun approve(targetSessionId: UUID? = null) {
        require(statusValue == AbsenceRequestStatus.PENDING)
        statusValue = AbsenceRequestStatus.APPROVED
        resolvedAtValue = LocalDateTime.now()
        resolvedTargetSessionIdValue = targetSessionId
    }

    fun reject() {
        require(statusValue == AbsenceRequestStatus.PENDING)
        statusValue = AbsenceRequestStatus.REJECTED
        resolvedAtValue = LocalDateTime.now()
    }
}

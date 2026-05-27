package com.dionomy.absence.domain

import java.util.UUID

data class AbsenceRequest(
    val id: UUID,
    val tenantId: UUID,
    val studentId: UUID,
    val sessionId: UUID,
    val reason: String,
    val desiredResult: AbsenceDesiredResult,
    val status: AbsenceRequestStatus,
)

package com.dionomy.absence.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class MakeupCredit(
    val id: UUID,
    val tenantId: UUID,
    val absenceRequestId: UUID,
    val studentId: UUID,
    val sourceSessionId: UUID,
    val status: MakeupCreditStatus = MakeupCreditStatus.AVAILABLE,
    val expiresOn: LocalDate,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(!expiresOn.isBefore(createdAt.toLocalDate()))
    }
}

enum class MakeupCreditStatus {
    AVAILABLE,
    USED,
    EXPIRED,
}

interface MakeupCreditRepository {
    fun save(credit: MakeupCredit): MakeupCredit
    fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<MakeupCredit>
}

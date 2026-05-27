package com.dionomy.pass.domain

import java.time.LocalDate
import java.util.UUID

class StudentPass(
    val id: UUID,
    val tenantId: UUID,
    val studentId: UUID,
    val totalCount: Int,
    val usedCount: Int,
    val expiresOn: LocalDate,
) {
    val remainingCount: Int
        get() = totalCount - usedCount

    fun isExpired(today: LocalDate): Boolean =
        remainingCount <= 0 || !expiresOn.isAfter(today)
}

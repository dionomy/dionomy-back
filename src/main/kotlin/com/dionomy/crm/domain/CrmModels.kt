package com.dionomy.crm.domain

import java.time.LocalDateTime
import java.util.UUID

enum class RetentionSignalType {
    DORMANT,
    PASS_EXPIRING_SOON,
    FREQUENT_CHANGES,
    MAKEUP_ACCUMULATED,
    NEW_SETTLING,
}

data class RetentionSignal(
    val type: RetentionSignalType,
    val label: String,
    val reason: String,
)

data class RiskStudent(
    val studentId: UUID,
    val studentName: String,
    val signals: List<RetentionSignal>,
)

enum class CareRecordStatus {
    PENDING,
    CONTACTED,
    RENEWED,
    DROPPED,
}

class CareRecord(
    val id: UUID,
    val tenantId: UUID,
    val studentId: UUID,
    val memo: String,
    val status: CareRecordStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    init {
        require(memo.isNotBlank())
    }
}

interface CareRecordRepository {
    fun save(record: CareRecord): CareRecord
    fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<CareRecord>
}

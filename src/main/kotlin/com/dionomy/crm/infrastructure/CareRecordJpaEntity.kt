package com.dionomy.crm.infrastructure

import com.dionomy.crm.domain.CareRecord
import com.dionomy.crm.domain.CareRecordRepository
import com.dionomy.crm.domain.CareRecordStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "care_records")
class CareRecordJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "student_id", nullable = false)
    var studentId: UUID = UUID.randomUUID(),
    @Column(name = "memo", nullable = false)
    var memo: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: CareRecordStatus = CareRecordStatus.PENDING,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): CareRecord =
        CareRecord(
            id = id,
            tenantId = tenantId,
            studentId = studentId,
            memo = memo,
            status = status,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(record: CareRecord): CareRecordJpaEntity =
            CareRecordJpaEntity(
                id = record.id,
                tenantId = record.tenantId,
                studentId = record.studentId,
                memo = record.memo,
                status = record.status,
                createdAt = record.createdAt,
            )
    }
}

interface SpringDataCareRecordJpaRepository : JpaRepository<CareRecordJpaEntity, UUID> {
    fun findByTenantIdAndStudentIdOrderByCreatedAtDesc(tenantId: UUID, studentId: UUID): List<CareRecordJpaEntity>
}

@Repository
class JpaCareRecordRepository(
    private val springDataRepository: SpringDataCareRecordJpaRepository,
) : CareRecordRepository {
    override fun save(record: CareRecord): CareRecord =
        springDataRepository.save(CareRecordJpaEntity.fromDomain(record)).toDomain()

    override fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<CareRecord> =
        springDataRepository.findByTenantIdAndStudentIdOrderByCreatedAtDesc(tenantId, studentId).map { it.toDomain() }
}

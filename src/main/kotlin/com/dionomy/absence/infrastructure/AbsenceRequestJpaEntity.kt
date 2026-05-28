package com.dionomy.absence.infrastructure

import com.dionomy.absence.domain.AbsenceDesiredResult
import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.absence.domain.AbsenceRequest
import com.dionomy.absence.domain.AbsenceRequestStatus
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
@Table(name = "absence_requests")
class AbsenceRequestJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "student_id", nullable = false)
    var studentId: UUID = UUID.randomUUID(),
    @Column(name = "session_id", nullable = false)
    var sessionId: UUID = UUID.randomUUID(),
    @Column(name = "reason", nullable = false)
    var reason: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "desired_result", nullable = false)
    var desiredResult: AbsenceDesiredResult = AbsenceDesiredResult.MAKEUP,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: AbsenceRequestStatus = AbsenceRequestStatus.PENDING,
    @Column(name = "requested_at", nullable = false)
    var requestedAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "resolved_at")
    var resolvedAt: LocalDateTime? = null,
) {
    fun toDomain(): AbsenceRequest =
        AbsenceRequest(
            id = id,
            tenantId = tenantId,
            studentId = studentId,
            sessionId = sessionId,
            reason = reason,
            desiredResult = desiredResult,
            statusValue = status,
            requestedAt = requestedAt,
            resolvedAtValue = resolvedAt,
        )

    companion object {
        fun fromDomain(request: AbsenceRequest): AbsenceRequestJpaEntity =
            AbsenceRequestJpaEntity(
                id = request.id,
                tenantId = request.tenantId,
                studentId = request.studentId,
                sessionId = request.sessionId,
                reason = request.reason,
                desiredResult = request.desiredResult,
                status = request.status,
                requestedAt = request.requestedAt,
                resolvedAt = request.resolvedAt,
            )
    }
}

interface SpringDataAbsenceRequestJpaRepository : JpaRepository<AbsenceRequestJpaEntity, UUID> {
    fun findByTenantIdOrderByRequestedAtDesc(tenantId: UUID): List<AbsenceRequestJpaEntity>
    fun findByTenantIdAndStudentIdOrderByRequestedAtDesc(tenantId: UUID, studentId: UUID): List<AbsenceRequestJpaEntity>
    fun findByTenantIdAndId(tenantId: UUID, id: UUID): AbsenceRequestJpaEntity?
}

@Repository
class JpaAbsenceRepository(
    private val springDataRepository: SpringDataAbsenceRequestJpaRepository,
) : AbsenceRepository {
    override fun save(request: AbsenceRequest): AbsenceRequest =
        springDataRepository.save(AbsenceRequestJpaEntity.fromDomain(request)).toDomain()

    override fun findByTenant(tenantId: UUID): List<AbsenceRequest> =
        springDataRepository.findByTenantIdOrderByRequestedAtDesc(tenantId).map { it.toDomain() }

    override fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<AbsenceRequest> =
        springDataRepository.findByTenantIdAndStudentIdOrderByRequestedAtDesc(tenantId, studentId).map { it.toDomain() }

    override fun findByTenantAndId(tenantId: UUID, requestId: UUID): AbsenceRequest? =
        springDataRepository.findByTenantIdAndId(tenantId, requestId)?.toDomain()
}

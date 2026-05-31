package com.dionomy.absence.infrastructure

import com.dionomy.absence.domain.AbsenceDesiredResult
import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.absence.domain.AbsenceRequest
import com.dionomy.absence.domain.AbsenceRequestStatus
import com.dionomy.absence.domain.MakeupCredit
import com.dionomy.absence.domain.MakeupCreditRepository
import com.dionomy.absence.domain.MakeupCreditStatus
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
    @Column(name = "resolved_target_session_id")
    var resolvedTargetSessionId: UUID? = null,
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
            resolvedTargetSessionIdValue = resolvedTargetSessionId,
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
                resolvedTargetSessionId = request.resolvedTargetSessionId,
            )
    }
}

@Entity
@Table(name = "makeup_credits")
class MakeupCreditJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "absence_request_id", nullable = false)
    var absenceRequestId: UUID = UUID.randomUUID(),
    @Column(name = "student_id", nullable = false)
    var studentId: UUID = UUID.randomUUID(),
    @Column(name = "source_session_id", nullable = false)
    var sourceSessionId: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: MakeupCreditStatus = MakeupCreditStatus.AVAILABLE,
    @Column(name = "expires_on", nullable = false)
    var expiresOn: java.time.LocalDate = java.time.LocalDate.now(),
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): MakeupCredit =
        MakeupCredit(
            id = id,
            tenantId = tenantId,
            absenceRequestId = absenceRequestId,
            studentId = studentId,
            sourceSessionId = sourceSessionId,
            status = status,
            expiresOn = expiresOn,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(credit: MakeupCredit): MakeupCreditJpaEntity =
            MakeupCreditJpaEntity(
                id = credit.id,
                tenantId = credit.tenantId,
                absenceRequestId = credit.absenceRequestId,
                studentId = credit.studentId,
                sourceSessionId = credit.sourceSessionId,
                status = credit.status,
                expiresOn = credit.expiresOn,
                createdAt = credit.createdAt,
            )
    }
}

interface SpringDataAbsenceRequestJpaRepository : JpaRepository<AbsenceRequestJpaEntity, UUID> {
    fun findByTenantIdOrderByRequestedAtDesc(tenantId: UUID): List<AbsenceRequestJpaEntity>
    fun findByTenantIdAndStudentIdOrderByRequestedAtDesc(tenantId: UUID, studentId: UUID): List<AbsenceRequestJpaEntity>
    fun findByTenantIdAndId(tenantId: UUID, id: UUID): AbsenceRequestJpaEntity?
}

interface SpringDataMakeupCreditJpaRepository : JpaRepository<MakeupCreditJpaEntity, UUID> {
    fun findByTenantIdAndStudentIdOrderByCreatedAtDesc(tenantId: UUID, studentId: UUID): List<MakeupCreditJpaEntity>
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

@Repository
class JpaMakeupCreditRepository(
    private val springDataRepository: SpringDataMakeupCreditJpaRepository,
) : MakeupCreditRepository {
    override fun save(credit: MakeupCredit): MakeupCredit =
        springDataRepository.save(MakeupCreditJpaEntity.fromDomain(credit)).toDomain()

    override fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<MakeupCredit> =
        springDataRepository.findByTenantIdAndStudentIdOrderByCreatedAtDesc(tenantId, studentId).map { it.toDomain() }
}

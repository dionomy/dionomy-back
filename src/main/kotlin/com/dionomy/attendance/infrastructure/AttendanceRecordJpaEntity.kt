package com.dionomy.attendance.infrastructure

import com.dionomy.attendance.domain.AttendanceRecord
import com.dionomy.attendance.domain.AttendanceRepository
import com.dionomy.attendance.domain.AttendanceStatus
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
@Table(name = "attendance_records")
class AttendanceRecordJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "session_id", nullable = false)
    var sessionId: UUID = UUID.randomUUID(),
    @Column(name = "student_id", nullable = false)
    var studentId: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: AttendanceStatus = AttendanceStatus.PRESENT,
    @Column(name = "checked_by_teacher_id", nullable = false)
    var checkedByTeacherId: UUID = UUID.randomUUID(),
    @Column(name = "checked_at", nullable = false)
    var checkedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): AttendanceRecord =
        AttendanceRecord(
            id = id,
            tenantId = tenantId,
            sessionId = sessionId,
            studentId = studentId,
            status = status,
            checkedByTeacherId = checkedByTeacherId,
            checkedAt = checkedAt,
        )

    companion object {
        fun fromDomain(record: AttendanceRecord): AttendanceRecordJpaEntity =
            AttendanceRecordJpaEntity(
                id = record.id,
                tenantId = record.tenantId,
                sessionId = record.sessionId,
                studentId = record.studentId,
                status = record.status,
                checkedByTeacherId = record.checkedByTeacherId,
                checkedAt = record.checkedAt,
            )
    }
}

interface SpringDataAttendanceRecordJpaRepository : JpaRepository<AttendanceRecordJpaEntity, UUID> {
    fun findByTenantIdAndSessionIdOrderByCheckedAtAsc(tenantId: UUID, sessionId: UUID): List<AttendanceRecordJpaEntity>
    fun findByTenantIdAndStudentIdOrderByCheckedAtDesc(tenantId: UUID, studentId: UUID): List<AttendanceRecordJpaEntity>
}

@Repository
class JpaAttendanceRepository(
    private val springDataRepository: SpringDataAttendanceRecordJpaRepository,
) : AttendanceRepository {
    override fun save(record: AttendanceRecord): AttendanceRecord =
        springDataRepository.save(AttendanceRecordJpaEntity.fromDomain(record)).toDomain()

    override fun findByTenantAndSession(tenantId: UUID, sessionId: UUID): List<AttendanceRecord> =
        springDataRepository.findByTenantIdAndSessionIdOrderByCheckedAtAsc(tenantId, sessionId).map { it.toDomain() }

    override fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<AttendanceRecord> =
        springDataRepository.findByTenantIdAndStudentIdOrderByCheckedAtDesc(tenantId, studentId).map { it.toDomain() }
}

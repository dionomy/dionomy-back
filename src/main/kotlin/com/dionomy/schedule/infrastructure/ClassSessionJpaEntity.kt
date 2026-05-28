package com.dionomy.schedule.infrastructure

import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ClassType
import com.dionomy.schedule.domain.RecurrenceFrequency
import com.dionomy.schedule.domain.RecurrenceRule
import com.dionomy.schedule.domain.ScheduleRepository
import com.dionomy.schedule.domain.SessionCapacity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "class_sessions")
class ClassSessionJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "title", nullable = false)
    var title: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ClassType = ClassType.GROUP,
    @Column(name = "teacher_id", nullable = false)
    var teacherId: UUID = UUID.randomUUID(),
    @Column(name = "place_id")
    var placeId: UUID? = null,
    @Column(name = "starts_at", nullable = false)
    var startsAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "ends_at", nullable = false)
    var endsAt: LocalDateTime = LocalDateTime.now().plusHours(1),
    @Column(name = "capacity_current", nullable = false)
    var capacityCurrent: Int = 0,
    @Column(name = "capacity_maximum", nullable = false)
    var capacityMaximum: Int = 1,
    @Column(name = "assigned_student_ids", nullable = false)
    var assignedStudentIds: String = "",
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_frequency")
    var recurrenceFrequency: RecurrenceFrequency? = null,
    @Column(name = "recurrence_days_of_week")
    var recurrenceDaysOfWeek: String? = null,
    @Column(name = "recurrence_until")
    var recurrenceUntil: LocalDate? = null,
) {
    fun toDomain(): ClassSession =
        ClassSession(
            id = id,
            tenantId = tenantId,
            title = title,
            type = type,
            teacherId = teacherId,
            placeId = placeId,
            startsAt = startsAt,
            endsAt = endsAt,
            capacity = SessionCapacity(
                current = capacityCurrent,
                maximum = capacityMaximum,
            ),
            assignedStudentIds = assignedStudentIds.toUuidList(),
            recurrence = toRecurrenceRule(),
        )

    private fun toRecurrenceRule(): RecurrenceRule? {
        val frequency = recurrenceFrequency ?: return null
        val until = recurrenceUntil ?: return null
        val days = recurrenceDaysOfWeek
            ?.split(",")
            ?.map { DayOfWeek.valueOf(it) }
            ?.toSet()
            ?: return null

        return RecurrenceRule(
            frequency = frequency,
            daysOfWeek = days,
            until = until,
        )
    }

    companion object {
        fun fromDomain(session: ClassSession): ClassSessionJpaEntity =
            ClassSessionJpaEntity(
                id = session.id,
                tenantId = session.tenantId,
                title = session.title,
                type = session.type,
                teacherId = session.teacherId,
                placeId = session.placeId,
                startsAt = session.startsAt,
                endsAt = session.endsAt,
                capacityCurrent = session.capacity.current,
                capacityMaximum = session.capacity.maximum,
                assignedStudentIds = session.assignedStudentIds.joinToString(","),
                recurrenceFrequency = session.recurrence?.frequency,
                recurrenceDaysOfWeek = session.recurrence?.daysOfWeek?.joinToString(",") { it.name },
                recurrenceUntil = session.recurrence?.until,
            )
    }
}

interface SpringDataClassSessionJpaRepository : JpaRepository<ClassSessionJpaEntity, UUID> {
    fun findByTenantIdAndStartsAtBetweenOrderByStartsAtAsc(
        tenantId: UUID,
        from: LocalDateTime,
        to: LocalDateTime,
    ): List<ClassSessionJpaEntity>
}

@Repository
class JpaScheduleRepository(
    private val springDataRepository: SpringDataClassSessionJpaRepository,
) : ScheduleRepository {
    override fun save(session: ClassSession): ClassSession =
        springDataRepository.save(ClassSessionJpaEntity.fromDomain(session)).toDomain()

    override fun get(tenantId: UUID, sessionId: UUID): ClassSession =
        springDataRepository.findById(sessionId)
            .filter { it.tenantId == tenantId }
            .map { it.toDomain() }
            .orElseThrow { NoSuchElementException("Class session not found: $sessionId") }

    override fun findByTenantAndDateRange(tenantId: UUID, from: LocalDate, to: LocalDate): List<ClassSession> =
        springDataRepository.findByTenantIdAndStartsAtBetweenOrderByStartsAtAsc(
            tenantId = tenantId,
            from = from.atStartOfDay(),
            to = to.plusDays(1).atStartOfDay().minusNanos(1),
        ).map { it.toDomain() }

    override fun delete(tenantId: UUID, sessionId: UUID) {
        val session = get(tenantId, sessionId)
        springDataRepository.delete(ClassSessionJpaEntity.fromDomain(session))
    }
}

private fun String.toUuidList(): List<UUID> =
    split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { UUID.fromString(it) }

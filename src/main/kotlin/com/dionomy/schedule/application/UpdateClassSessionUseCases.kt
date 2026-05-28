package com.dionomy.schedule.application

import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ScheduleConflictPolicy
import com.dionomy.schedule.domain.ScheduleRepository
import com.dionomy.schedule.domain.SessionCapacity
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class AssignStudentsToClassSessionUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    fun execute(tenantId: UUID, sessionId: UUID, studentIds: List<UUID>): ClassSession {
        val session = scheduleRepository.get(tenantId, sessionId)
        val updated = session.copyWith(
            assignedStudentIds = studentIds.distinct(),
            capacity = SessionCapacity(
                current = studentIds.distinct().size,
                maximum = session.capacity.maximum,
            ),
        )

        return scheduleRepository.save(updated)
    }
}

@Service
class MoveClassSessionUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    private val conflictPolicy = ScheduleConflictPolicy()

    fun execute(tenantId: UUID, sessionId: UUID, startsAt: LocalDateTime, endsAt: LocalDateTime): ClassSession {
        val session = scheduleRepository.get(tenantId, sessionId)
        val updated = session.copyWith(startsAt = startsAt, endsAt = endsAt)
        val existingSessions = scheduleRepository.findByTenantAndDateRange(
            tenantId = tenantId,
            from = startsAt.toLocalDate(),
            to = endsAt.toLocalDate(),
        )
        conflictPolicy.validate(updated, existingSessions)

        return scheduleRepository.save(updated)
    }
}

@Service
class CancelClassSessionUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    fun execute(tenantId: UUID, sessionId: UUID) {
        scheduleRepository.delete(tenantId, sessionId)
    }
}

private fun ClassSession.copyWith(
    startsAt: LocalDateTime = this.startsAt,
    endsAt: LocalDateTime = this.endsAt,
    capacity: SessionCapacity = this.capacity,
    assignedStudentIds: List<UUID> = this.assignedStudentIds,
): ClassSession =
    ClassSession(
        id = id,
        tenantId = tenantId,
        title = title,
        type = type,
        teacherId = teacherId,
        placeId = placeId,
        startsAt = startsAt,
        endsAt = endsAt,
        capacity = capacity,
        assignedStudentIds = assignedStudentIds,
        recurrence = recurrence,
    )

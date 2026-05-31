package com.dionomy.absence.application

import com.dionomy.absence.domain.AbsenceRepository
import com.dionomy.absence.domain.AbsenceRequest
import com.dionomy.absence.domain.AbsenceDesiredResult
import com.dionomy.absence.domain.MakeupCredit
import com.dionomy.absence.domain.MakeupCreditRepository
import com.dionomy.operation.domain.InstructorAvailabilityRepository
import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ScheduleRepository
import com.dionomy.schedule.domain.SessionCapacity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class ResolveAbsenceUseCase(
    private val absenceRepository: AbsenceRepository,
    private val scheduleRepository: ScheduleRepository,
    private val makeupCreditRepository: MakeupCreditRepository,
    private val instructorAvailabilityRepository: InstructorAvailabilityRepository,
) {
    fun approve(tenantId: UUID, requestId: UUID, targetSessionId: UUID?, targetAvailabilityId: UUID?): AbsenceRequest {
        val request = requireNotNull(absenceRepository.findByTenantAndId(tenantId, requestId))

        when (request.desiredResult) {
            AbsenceDesiredResult.MOVE_TO_OTHER_SESSION -> moveStudentToTargetSession(request, requireNotNull(targetSessionId))
            AbsenceDesiredResult.MAKEUP -> createMakeupCredit(request, requireNotNull(targetAvailabilityId))
        }

        request.approve(targetSessionId, targetAvailabilityId)
        return absenceRepository.save(request)
    }

    fun reject(tenantId: UUID, requestId: UUID): AbsenceRequest {
        val request = requireNotNull(absenceRepository.findByTenantAndId(tenantId, requestId))
        request.reject()
        return absenceRepository.save(request)
    }

    private fun moveStudentToTargetSession(request: AbsenceRequest, targetSessionId: UUID) {
        require(request.sessionId != targetSessionId)

        val sourceSession = scheduleRepository.get(request.tenantId, request.sessionId)
        val targetSession = scheduleRepository.get(request.tenantId, targetSessionId)
        val availability = instructorAvailabilityRepository.findByInstructor(request.tenantId, targetSession.teacherId)
            .firstOrNull { it.covers(targetSession.startsAt, targetSession.endsAt) }

        val sourceStudentIds = sourceSession.assignedStudentIds.filter { it != request.studentId }
        val targetStudentIds = (targetSession.assignedStudentIds + request.studentId).distinct()

        require(availability != null)
        require(targetStudentIds.size <= targetSession.capacity.maximum)

        scheduleRepository.save(sourceSession.withAssignedStudents(sourceStudentIds))
        scheduleRepository.save(targetSession.withAssignedStudents(targetStudentIds))
    }

    private fun createMakeupCredit(request: AbsenceRequest, targetAvailabilityId: UUID) {
        val sourceSession = scheduleRepository.get(request.tenantId, request.sessionId)
        val availability = requireNotNull(
            instructorAvailabilityRepository.findByTenantAndId(request.tenantId, targetAvailabilityId),
        )

        require(availability.instructorId == sourceSession.teacherId)

        makeupCreditRepository.save(
            MakeupCredit(
                id = UUID.randomUUID(),
                tenantId = request.tenantId,
                absenceRequestId = request.id,
                studentId = request.studentId,
                sourceSessionId = request.sessionId,
                expiresOn = LocalDate.now().plusDays(30),
            ),
        )
    }
}

private fun ClassSession.withAssignedStudents(studentIds: List<UUID>): ClassSession =
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
            current = studentIds.distinct().size,
            maximum = capacity.maximum,
        ),
        assignedStudentIds = studentIds.distinct(),
        recurrence = recurrence,
    )

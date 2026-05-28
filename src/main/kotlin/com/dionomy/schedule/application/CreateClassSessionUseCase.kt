package com.dionomy.schedule.application

import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ScheduleConflictPolicy
import com.dionomy.schedule.domain.ScheduleRepository
import org.springframework.stereotype.Service

@Service
class CreateClassSessionUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    private val conflictPolicy = ScheduleConflictPolicy()

    fun execute(session: ClassSession): ClassSession {
        val existingSessions = scheduleRepository.findByTenantAndDateRange(
            tenantId = session.tenantId,
            from = session.startsAt.toLocalDate(),
            to = session.endsAt.toLocalDate(),
        )
        conflictPolicy.validate(session, existingSessions)
        return scheduleRepository.save(session)
    }
}

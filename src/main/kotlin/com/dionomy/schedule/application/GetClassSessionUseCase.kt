package com.dionomy.schedule.application

import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ScheduleRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetClassSessionUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    fun execute(tenantId: UUID, sessionId: UUID): ClassSession =
        scheduleRepository.get(tenantId, sessionId)
}

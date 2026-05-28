package com.dionomy.schedule.application

import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ScheduleRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class ListClassSessionsUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    fun execute(tenantId: UUID, from: LocalDate, to: LocalDate): List<ClassSession> =
        scheduleRepository.findByTenantAndDateRange(tenantId, from, to)
}

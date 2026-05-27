package com.dionomy.schedule.domain

import java.time.LocalDate
import java.util.UUID

interface ScheduleRepository {
    fun save(session: ClassSession): ClassSession
    fun findByTenantAndDateRange(tenantId: UUID, from: LocalDate, to: LocalDate): List<ClassSession>
}

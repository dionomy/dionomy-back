package com.dionomy.schedule.infrastructure

import com.dionomy.schedule.domain.ClassSession
import com.dionomy.schedule.domain.ScheduleRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryScheduleRepository : ScheduleRepository {
    private val store = ConcurrentHashMap<UUID, ClassSession>()

    override fun save(session: ClassSession): ClassSession {
        store[session.id] = session
        return session
    }

    override fun findByTenantAndDateRange(tenantId: UUID, from: LocalDate, to: LocalDate): List<ClassSession> =
        store.values
            .filter { it.tenantId == tenantId }
            .filter {
                val sessionDate = it.startsAt.toLocalDate()
                !sessionDate.isBefore(from) && !sessionDate.isAfter(to)
            }
            .sortedBy { it.startsAt }
}

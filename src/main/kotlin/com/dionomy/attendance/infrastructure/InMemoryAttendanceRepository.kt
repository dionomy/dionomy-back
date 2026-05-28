package com.dionomy.attendance.infrastructure

import com.dionomy.attendance.domain.AttendanceRecord
import com.dionomy.attendance.domain.AttendanceRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryAttendanceRepository : AttendanceRepository {
    private val records = ConcurrentHashMap<String, AttendanceRecord>()

    override fun save(record: AttendanceRecord): AttendanceRecord {
        records["${record.sessionId}:${record.studentId}"] = record
        return record
    }

    override fun findByTenantAndSession(tenantId: UUID, sessionId: UUID): List<AttendanceRecord> =
        records.values
            .filter { it.tenantId == tenantId && it.sessionId == sessionId }
            .sortedBy { it.checkedAt }

    override fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<AttendanceRecord> =
        records.values
            .filter { it.tenantId == tenantId && it.studentId == studentId }
            .sortedByDescending { it.checkedAt }
}

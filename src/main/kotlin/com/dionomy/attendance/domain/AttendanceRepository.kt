package com.dionomy.attendance.domain

import java.util.UUID

interface AttendanceRepository {
    fun save(record: AttendanceRecord): AttendanceRecord
    fun findByTenantAndSession(tenantId: UUID, sessionId: UUID): List<AttendanceRecord>
    fun findByTenantAndStudent(tenantId: UUID, studentId: UUID): List<AttendanceRecord>
}

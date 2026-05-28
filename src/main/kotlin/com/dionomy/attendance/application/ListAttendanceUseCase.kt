package com.dionomy.attendance.application

import com.dionomy.attendance.domain.AttendanceRecord
import com.dionomy.attendance.domain.AttendanceRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository,
) {
    fun bySession(tenantId: UUID, sessionId: UUID): List<AttendanceRecord> =
        attendanceRepository.findByTenantAndSession(tenantId, sessionId)

    fun byStudent(tenantId: UUID, studentId: UUID): List<AttendanceRecord> =
        attendanceRepository.findByTenantAndStudent(tenantId, studentId)
}

package com.dionomy.attendance.application

import com.dionomy.attendance.domain.AttendanceRecord
import com.dionomy.attendance.domain.AttendanceRepository
import com.dionomy.attendance.domain.AttendanceStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RecordAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository,
) {
    fun execute(command: RecordAttendanceCommand): AttendanceRecord =
        attendanceRepository.save(
            AttendanceRecord(
                id = UUID.randomUUID(),
                tenantId = command.tenantId,
                sessionId = command.sessionId,
                studentId = command.studentId,
                status = command.status,
                checkedByTeacherId = command.teacherId,
            ),
        )
}

data class RecordAttendanceCommand(
    val tenantId: UUID,
    val sessionId: UUID,
    val studentId: UUID,
    val teacherId: UUID,
    val status: AttendanceStatus,
)

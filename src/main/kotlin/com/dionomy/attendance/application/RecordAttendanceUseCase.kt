package com.dionomy.attendance.application

import com.dionomy.attendance.domain.AttendanceRecord
import com.dionomy.attendance.domain.AttendanceRepository
import com.dionomy.attendance.domain.AttendanceStatus
import com.dionomy.pass.domain.PassRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class RecordAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository,
    private val passRepository: PassRepository,
) {
    fun execute(command: RecordAttendanceCommand): AttendanceRecord {
        val previousRecord = attendanceRepository.findByTenantAndSession(command.tenantId, command.sessionId)
            .filter { it.studentId == command.studentId }
            .maxByOrNull { it.checkedAt }

        syncPassUsage(command, previousRecord?.status)

        return attendanceRepository.save(
            AttendanceRecord(
                id = previousRecord?.id ?: UUID.randomUUID(),
                tenantId = command.tenantId,
                sessionId = command.sessionId,
                studentId = command.studentId,
                status = command.status,
                checkedByTeacherId = command.teacherId,
                checkedAt = LocalDateTime.now(),
            ),
        )
    }

    private fun syncPassUsage(command: RecordAttendanceCommand, previousStatus: AttendanceStatus?) {
        if (previousStatus == command.status) {
            return
        }

        val wasChargeable = previousStatus?.isChargeable() ?: false
        val isChargeable = command.status.isChargeable()

        if (wasChargeable == isChargeable) {
            return
        }

        val activePass = passRepository.findPassesByTenantAndStudent(command.tenantId, command.studentId)
            .filter { !it.isExpired(LocalDate.now()) }
            .maxByOrNull { it.expiresOn }
            ?: return

        if (isChargeable) {
            activePass.consume(1)
            passRepository.saveStudentPass(activePass)
            passRepository.appendUsageLog(command.toPassUsageLog(activePass.id, "출석 체크 차감"))
        } else {
            if (activePass.usedCount <= 0) {
                return
            }
            activePass.restore(1)
            passRepository.saveStudentPass(activePass)
            passRepository.appendUsageLog(command.toPassUsageLog(activePass.id, "출석 상태 변경 복구", restore = true))
        }
    }
}

data class RecordAttendanceCommand(
    val tenantId: UUID,
    val sessionId: UUID,
    val studentId: UUID,
    val teacherId: UUID,
    val status: AttendanceStatus,
) {
    fun toPassUsageLog(passId: UUID, reason: String, restore: Boolean = false): com.dionomy.pass.domain.PassUsageLog =
        com.dionomy.pass.domain.PassUsageLog(
            id = UUID.randomUUID(),
            tenantId = tenantId,
            passId = passId,
            studentId = studentId,
            type = if (restore) com.dionomy.pass.domain.PassUsageType.RESTORE else com.dionomy.pass.domain.PassUsageType.CONSUME,
            count = 1,
            reason = reason,
        )
}

private fun AttendanceStatus.isChargeable(): Boolean =
    this == AttendanceStatus.PRESENT || this == AttendanceStatus.LATE

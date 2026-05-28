package com.dionomy.attendance.presentation

import com.dionomy.attendance.application.ListAttendanceUseCase
import com.dionomy.attendance.application.RecordAttendanceCommand
import com.dionomy.attendance.application.RecordAttendanceUseCase
import com.dionomy.attendance.domain.AttendanceRecord
import com.dionomy.attendance.domain.AttendanceStatus
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/attendance")
class AttendanceController(
    private val recordAttendanceUseCase: RecordAttendanceUseCase,
    private val listAttendanceUseCase: ListAttendanceUseCase,
) {
    @GetMapping("/sessions/{sessionId}")
    fun bySession(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable sessionId: UUID,
    ): List<AttendanceRecordResponse> =
        listAttendanceUseCase.bySession(tenantId, sessionId).map { it.toResponse() }

    @PostMapping("/sessions/{sessionId}")
    fun record(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable sessionId: UUID,
        @Valid @RequestBody request: RecordAttendanceRequest,
    ): AttendanceRecordResponse =
        recordAttendanceUseCase.execute(request.toCommand(tenantId, sessionId)).toResponse()
}

data class RecordAttendanceRequest(
    val studentId: UUID,
    val teacherId: UUID,
    val status: AttendanceStatus,
) {
    fun toCommand(tenantId: UUID, sessionId: UUID): RecordAttendanceCommand =
        RecordAttendanceCommand(
            tenantId = tenantId,
            sessionId = sessionId,
            studentId = studentId,
            teacherId = teacherId,
            status = status,
        )
}

data class AttendanceRecordResponse(
    val id: UUID,
    val sessionId: UUID,
    val studentId: UUID,
    val status: AttendanceStatus,
    val checkedByTeacherId: UUID,
    val checkedAt: LocalDateTime,
)

private fun AttendanceRecord.toResponse(): AttendanceRecordResponse =
    AttendanceRecordResponse(
        id = id,
        sessionId = sessionId,
        studentId = studentId,
        status = status,
        checkedByTeacherId = checkedByTeacherId,
        checkedAt = checkedAt,
    )

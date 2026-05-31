package com.dionomy.crm.presentation

import com.dionomy.crm.application.CareRecordUseCases
import com.dionomy.crm.application.ListRiskStudentsUseCase
import com.dionomy.crm.application.RefreshRetentionSignalsUseCase
import com.dionomy.crm.domain.CareRecord
import com.dionomy.crm.domain.CareRecordStatus
import com.dionomy.crm.domain.RetentionSignal
import com.dionomy.crm.domain.RetentionSignalType
import com.dionomy.crm.domain.RiskStudent
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
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
@RequestMapping("/api/crm")
class CrmController(
    private val listRiskStudentsUseCase: ListRiskStudentsUseCase,
    private val refreshRetentionSignalsUseCase: RefreshRetentionSignalsUseCase,
    private val careRecordUseCases: CareRecordUseCases,
) {
    @GetMapping("/risk-students")
    fun riskStudents(@RequestHeader("X-Tenant-Id") tenantId: UUID): List<RiskStudentResponse> =
        listRiskStudentsUseCase.execute(tenantId).map { it.toResponse() }

    @PostMapping("/retention-signals/refresh")
    fun refreshSignals(@RequestHeader("X-Tenant-Id") tenantId: UUID): List<RiskStudentResponse> {
        refreshRetentionSignalsUseCase.refreshTenant(tenantId)

        return listRiskStudentsUseCase.execute(tenantId).map { it.toResponse() }
    }

    @GetMapping("/students/{studentId}/care-records")
    fun careRecords(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable studentId: UUID,
    ): List<CareRecordResponse> =
        careRecordUseCases.list(tenantId, studentId).map { it.toResponse() }

    @PostMapping("/students/{studentId}/care-records")
    fun createCareRecord(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable studentId: UUID,
        @Valid @RequestBody request: CreateCareRecordRequest,
    ): CareRecordResponse =
        careRecordUseCases.create(tenantId, studentId, request.memo, request.status).toResponse()
}

data class CreateCareRecordRequest(
    @field:NotBlank
    val memo: String,
    val status: CareRecordStatus,
)

data class RiskStudentResponse(
    val studentId: UUID,
    val studentName: String,
    val signals: List<RetentionSignalResponse>,
)

data class RetentionSignalResponse(
    val type: RetentionSignalType,
    val label: String,
    val reason: String,
)

data class CareRecordResponse(
    val id: UUID,
    val studentId: UUID,
    val memo: String,
    val status: CareRecordStatus,
    val createdAt: LocalDateTime,
)

private fun RiskStudent.toResponse(): RiskStudentResponse =
    RiskStudentResponse(
        studentId = studentId,
        studentName = studentName,
        signals = signals.map { it.toResponse() },
    )

private fun RetentionSignal.toResponse(): RetentionSignalResponse =
    RetentionSignalResponse(
        type = type,
        label = label,
        reason = reason,
    )

private fun CareRecord.toResponse(): CareRecordResponse =
    CareRecordResponse(
        id = id,
        studentId = studentId,
        memo = memo,
        status = status,
        createdAt = createdAt,
    )

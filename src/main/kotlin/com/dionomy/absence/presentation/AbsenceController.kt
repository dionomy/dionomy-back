package com.dionomy.absence.presentation

import com.dionomy.absence.application.ListAbsenceRequestsUseCase
import com.dionomy.absence.application.RequestAbsenceCommand
import com.dionomy.absence.application.RequestAbsenceUseCase
import com.dionomy.absence.application.ResolveAbsenceUseCase
import com.dionomy.absence.domain.AbsenceDesiredResult
import com.dionomy.absence.domain.AbsenceRequest
import com.dionomy.absence.domain.AbsenceRequestStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/absence-requests")
class AbsenceController(
    private val requestAbsenceUseCase: RequestAbsenceUseCase,
    private val resolveAbsenceUseCase: ResolveAbsenceUseCase,
    private val listAbsenceRequestsUseCase: ListAbsenceRequestsUseCase,
) {
    @GetMapping
    fun list(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @RequestParam(required = false) studentId: UUID?,
    ): List<AbsenceRequestResponse> =
        if (studentId == null) {
            listAbsenceRequestsUseCase.byTenant(tenantId)
        } else {
            listAbsenceRequestsUseCase.byStudent(tenantId, studentId)
        }.map { it.toResponse() }

    @PostMapping
    fun request(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreateAbsenceRequest,
    ): AbsenceRequestResponse =
        requestAbsenceUseCase.execute(request.toCommand(tenantId)).toResponse()

    @PostMapping("/{requestId}/approve")
    fun approve(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable requestId: UUID,
    ): AbsenceRequestResponse =
        resolveAbsenceUseCase.approve(tenantId, requestId).toResponse()

    @PostMapping("/{requestId}/reject")
    fun reject(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable requestId: UUID,
    ): AbsenceRequestResponse =
        resolveAbsenceUseCase.reject(tenantId, requestId).toResponse()
}

data class CreateAbsenceRequest(
    val studentId: UUID,
    val sessionId: UUID,
    @field:NotBlank
    val reason: String,
    val desiredResult: AbsenceDesiredResult,
) {
    fun toCommand(tenantId: UUID): RequestAbsenceCommand =
        RequestAbsenceCommand(
            tenantId = tenantId,
            studentId = studentId,
            sessionId = sessionId,
            reason = reason,
            desiredResult = desiredResult,
        )
}

data class AbsenceRequestResponse(
    val id: UUID,
    val studentId: UUID,
    val sessionId: UUID,
    val reason: String,
    val desiredResult: AbsenceDesiredResult,
    val status: AbsenceRequestStatus,
    val requestedAt: LocalDateTime,
    val resolvedAt: LocalDateTime?,
)

private fun AbsenceRequest.toResponse(): AbsenceRequestResponse =
    AbsenceRequestResponse(
        id = id,
        studentId = studentId,
        sessionId = sessionId,
        reason = reason,
        desiredResult = desiredResult,
        status = status,
        requestedAt = requestedAt,
        resolvedAt = resolvedAt,
    )

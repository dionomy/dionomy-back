package com.dionomy.student.presentation

import com.dionomy.student.application.GetStudentUseCase
import com.dionomy.student.application.GetStudentOperationSummaryUseCase
import com.dionomy.student.application.ListStudentsUseCase
import com.dionomy.student.application.RegisterStudentCommand
import com.dionomy.student.application.RegisterStudentUseCase
import com.dionomy.student.application.StudentOperationSummary
import com.dionomy.student.application.StudentPassSummary
import com.dionomy.pass.domain.PassExpirationReason
import com.dionomy.pass.domain.PassLifecycleStatus
import com.dionomy.student.domain.Student
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
@RequestMapping("/api/students")
class StudentController(
    private val registerStudentUseCase: RegisterStudentUseCase,
    private val listStudentsUseCase: ListStudentsUseCase,
    private val getStudentUseCase: GetStudentUseCase,
    private val getStudentOperationSummaryUseCase: GetStudentOperationSummaryUseCase,
) {
    @GetMapping
    fun list(@RequestHeader("X-Tenant-Id") tenantId: UUID): List<StudentResponse> =
        listStudentsUseCase.execute(tenantId).map { it.toResponse() }

    @PostMapping
    fun register(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: RegisterStudentRequest,
    ): StudentResponse =
        registerStudentUseCase.execute(request.toCommand(tenantId)).toResponse()

    @GetMapping("/{studentId}")
    fun get(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable studentId: UUID,
    ): StudentResponse =
        getStudentUseCase.execute(tenantId, studentId).toResponse()

    @GetMapping("/operation-summary")
    fun operationSummary(@RequestHeader("X-Tenant-Id") tenantId: UUID): StudentOperationSummaryResponse =
        getStudentOperationSummaryUseCase.execute(tenantId).toResponse()
}

data class RegisterStudentRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val phone: String,
    val memo: String?,
    val tags: List<String> = emptyList(),
) {
    fun toCommand(tenantId: UUID): RegisterStudentCommand =
        RegisterStudentCommand(
            tenantId = tenantId,
            name = name,
            phone = phone,
            memo = memo,
            tags = tags,
        )
}

data class StudentResponse(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val phone: String,
    val memo: String?,
    val tags: List<String>,
    val createdAt: LocalDateTime,
)

data class StudentOperationSummaryResponse(
    val totalStudents: Int,
    val passExpiringSoonCount: Int,
    val passLowRemainingCount: Int,
    val students: List<StudentPassSummaryResponse>,
)

data class StudentPassSummaryResponse(
    val studentId: UUID,
    val activePassId: UUID?,
    val remainingCount: Int?,
    val totalCount: Int?,
    val expiresOn: java.time.LocalDate?,
    val lifecycleStatus: PassLifecycleStatus?,
    val expirationReason: PassExpirationReason?,
    val expiringSoon: Boolean,
    val lowRemaining: Boolean,
)

private fun Student.toResponse(): StudentResponse =
    StudentResponse(
        id = id,
        tenantId = tenantId,
        name = name,
        phone = phone,
        memo = memo,
        tags = tags,
        createdAt = createdAt,
    )

private fun StudentOperationSummary.toResponse(): StudentOperationSummaryResponse =
    StudentOperationSummaryResponse(
        totalStudents = totalStudents,
        passExpiringSoonCount = passExpiringSoonCount,
        passLowRemainingCount = passLowRemainingCount,
        students = students.map { it.toResponse() },
    )

private fun StudentPassSummary.toResponse(): StudentPassSummaryResponse =
    StudentPassSummaryResponse(
        studentId = studentId,
        activePassId = activePassId,
        remainingCount = remainingCount,
        totalCount = totalCount,
        expiresOn = expiresOn,
        lifecycleStatus = lifecycleStatus,
        expirationReason = expirationReason,
        expiringSoon = expiringSoon,
        lowRemaining = lowRemaining,
    )

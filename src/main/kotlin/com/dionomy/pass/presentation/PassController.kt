package com.dionomy.pass.presentation

import com.dionomy.pass.application.CreatePassProductCommand
import com.dionomy.pass.application.CreatePassProductUseCase
import com.dionomy.pass.application.IssueStudentPassCommand
import com.dionomy.pass.application.IssueStudentPassUseCase
import com.dionomy.pass.application.ListPassProductsUseCase
import com.dionomy.pass.application.ListStudentPassesUseCase
import com.dionomy.pass.application.RecordPassUsageCommand
import com.dionomy.pass.application.RecordPassUsageUseCase
import com.dionomy.pass.domain.PassProduct
import com.dionomy.pass.domain.PassUsageLog
import com.dionomy.pass.domain.PassUsageType
import com.dionomy.pass.domain.StudentPass
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@RestController
class PassController(
    private val createPassProductUseCase: CreatePassProductUseCase,
    private val listPassProductsUseCase: ListPassProductsUseCase,
    private val issueStudentPassUseCase: IssueStudentPassUseCase,
    private val listStudentPassesUseCase: ListStudentPassesUseCase,
    private val recordPassUsageUseCase: RecordPassUsageUseCase,
) {
    @GetMapping("/api/pass-products")
    fun listProducts(@RequestHeader("X-Tenant-Id") tenantId: UUID): List<PassProductResponse> =
        listPassProductsUseCase.execute(tenantId).map { it.toResponse() }

    @PostMapping("/api/pass-products")
    fun createProduct(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: CreatePassProductRequest,
    ): PassProductResponse =
        createPassProductUseCase.execute(request.toCommand(tenantId)).toResponse()

    @GetMapping("/api/students/{studentId}/passes")
    fun listStudentPasses(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable studentId: UUID,
    ): List<StudentPassResponse> =
        listStudentPassesUseCase.execute(tenantId, studentId).map { it.toResponse() }

    @PostMapping("/api/students/{studentId}/passes")
    fun issueStudentPass(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable studentId: UUID,
        @Valid @RequestBody request: IssueStudentPassRequest,
    ): StudentPassResponse =
        issueStudentPassUseCase.execute(request.toCommand(tenantId, studentId)).toResponse()

    @PostMapping("/api/student-passes/{passId}/consume")
    fun consume(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable passId: UUID,
        @Valid @RequestBody request: RecordPassUsageRequest,
    ): PassUsageLogResponse =
        recordPassUsageUseCase.consume(request.toCommand(tenantId, passId)).toResponse()

    @PostMapping("/api/student-passes/{passId}/restore")
    fun restore(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable passId: UUID,
        @Valid @RequestBody request: RecordPassUsageRequest,
    ): PassUsageLogResponse =
        recordPassUsageUseCase.restore(request.toCommand(tenantId, passId)).toResponse()

    @GetMapping("/api/student-passes/{passId}/usage-logs")
    fun usageLogs(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @PathVariable passId: UUID,
    ): List<PassUsageLogResponse> =
        recordPassUsageUseCase.logs(tenantId, passId).map { it.toResponse() }
}

data class CreatePassProductRequest(
    @field:NotBlank
    val name: String,
    @field:Positive
    val totalCount: Int,
    @field:Positive
    val validDays: Int,
    @field:PositiveOrZero
    val price: Long,
) {
    fun toCommand(tenantId: UUID): CreatePassProductCommand =
        CreatePassProductCommand(
            tenantId = tenantId,
            name = name,
            totalCount = totalCount,
            validDays = validDays,
            price = price,
        )
}

data class IssueStudentPassRequest(
    val productId: UUID,
    val issuedOn: LocalDate?,
) {
    fun toCommand(tenantId: UUID, studentId: UUID): IssueStudentPassCommand =
        IssueStudentPassCommand(
            tenantId = tenantId,
            studentId = studentId,
            productId = productId,
            issuedOn = issuedOn,
        )
}

data class RecordPassUsageRequest(
    @field:Positive
    val count: Int,
    @field:NotBlank
    val reason: String,
) {
    fun toCommand(tenantId: UUID, passId: UUID): RecordPassUsageCommand =
        RecordPassUsageCommand(
            tenantId = tenantId,
            passId = passId,
            count = count,
            reason = reason,
        )
}

data class PassProductResponse(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val totalCount: Int,
    val validDays: Int,
    val price: Long,
    val createdAt: LocalDateTime,
)

data class StudentPassResponse(
    val id: UUID,
    val tenantId: UUID,
    val productId: UUID,
    val studentId: UUID,
    val totalCount: Int,
    val usedCount: Int,
    val remainingCount: Int,
    val issuedOn: LocalDate,
    val expiresOn: LocalDate,
    val expired: Boolean,
)

data class PassUsageLogResponse(
    val id: UUID,
    val passId: UUID,
    val studentId: UUID,
    val type: PassUsageType,
    val count: Int,
    val reason: String,
    val createdAt: LocalDateTime,
)

private fun PassProduct.toResponse(): PassProductResponse =
    PassProductResponse(
        id = id,
        tenantId = tenantId,
        name = name,
        totalCount = totalCount,
        validDays = validDays,
        price = price,
        createdAt = createdAt,
    )

private fun StudentPass.toResponse(): StudentPassResponse =
    StudentPassResponse(
        id = id,
        tenantId = tenantId,
        productId = productId,
        studentId = studentId,
        totalCount = totalCount,
        usedCount = usedCount,
        remainingCount = remainingCount,
        issuedOn = issuedOn,
        expiresOn = expiresOn,
        expired = isExpired(LocalDate.now()),
    )

private fun PassUsageLog.toResponse(): PassUsageLogResponse =
    PassUsageLogResponse(
        id = id,
        passId = passId,
        studentId = studentId,
        type = type,
        count = count,
        reason = reason,
        createdAt = createdAt,
    )

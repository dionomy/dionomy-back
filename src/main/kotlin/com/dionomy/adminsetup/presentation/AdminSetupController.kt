package com.dionomy.adminsetup.presentation

import com.dionomy.adminsetup.application.CreateTenantSetupCommand
import com.dionomy.adminsetup.application.CreateTenantSetupUseCase
import com.dionomy.adminsetup.application.ListTenantSetupsUseCase
import com.dionomy.adminsetup.application.UpdateTenantSetupStatusUseCase
import com.dionomy.adminsetup.domain.TenantSetup
import com.dionomy.adminsetup.domain.WhiteLabelBuildStatus
import com.dionomy.tenant.domain.TenantStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/admin/tenant-setups")
class AdminSetupController(
    private val createTenantSetupUseCase: CreateTenantSetupUseCase,
    private val listTenantSetupsUseCase: ListTenantSetupsUseCase,
    private val updateTenantSetupStatusUseCase: UpdateTenantSetupStatusUseCase,
) {
    @GetMapping
    fun list(): List<TenantSetupResponse> =
        listTenantSetupsUseCase.execute().map { it.toResponse() }

    @PostMapping
    fun create(@Valid @RequestBody request: CreateTenantSetupRequest): TenantSetupResponse =
        createTenantSetupUseCase.execute(request.toCommand()).toResponse()

    @PatchMapping("/{setupId}/status")
    fun updateStatus(
        @PathVariable setupId: UUID,
        @RequestBody request: UpdateTenantStatusRequest,
    ): TenantSetupResponse =
        updateTenantSetupStatusUseCase.execute(setupId, request.status).toResponse()
}

data class CreateTenantSetupRequest(
    @field:NotBlank
    val academyName: String,
    @field:NotBlank
    val ownerContact: String,
    @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    val mainColor: String,
) {
    fun toCommand(): CreateTenantSetupCommand =
        CreateTenantSetupCommand(
            academyName = academyName,
            ownerContact = ownerContact,
            mainColor = mainColor,
        )
}

data class UpdateTenantStatusRequest(
    val status: TenantStatus,
)

data class TenantSetupResponse(
    val id: UUID,
    val academyName: String,
    val ownerContact: String,
    val mainColor: String,
    val tenantStatus: TenantStatus,
    val buildStatus: WhiteLabelBuildStatus,
    val createdAt: LocalDateTime,
)

private fun TenantSetup.toResponse(): TenantSetupResponse =
    TenantSetupResponse(
        id = id,
        academyName = academyName,
        ownerContact = ownerContact,
        mainColor = mainColor,
        tenantStatus = tenantStatus,
        buildStatus = buildStatus,
        createdAt = createdAt,
    )

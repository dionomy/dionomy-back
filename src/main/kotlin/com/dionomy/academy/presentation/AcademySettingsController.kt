package com.dionomy.academy.presentation

import com.dionomy.academy.application.GetAcademySettingsUseCase
import com.dionomy.academy.application.UpdateAcademySettingsUseCase
import com.dionomy.academy.domain.AcademySettings
import com.dionomy.academy.domain.Branding
import com.dionomy.academy.domain.MakeupPolicy
import com.dionomy.academy.domain.PassPolicy
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/academy/settings")
class AcademySettingsController(
    private val getAcademySettingsUseCase: GetAcademySettingsUseCase,
    private val updateAcademySettingsUseCase: UpdateAcademySettingsUseCase,
) {
    @GetMapping
    fun get(@RequestHeader("X-Tenant-Id") tenantId: UUID): AcademySettingsResponse =
        getAcademySettingsUseCase.execute(tenantId).toResponse()

    @PutMapping
    fun update(
        @RequestHeader("X-Tenant-Id") tenantId: UUID,
        @Valid @RequestBody request: UpdateAcademySettingsRequest,
    ): AcademySettingsResponse =
        updateAcademySettingsUseCase.execute(request.toDomain(tenantId)).toResponse()
}

data class UpdateAcademySettingsRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val contact: String,
    @field:NotBlank
    val address: String,
    val logoUrl: String?,
    @field:Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    val mainColor: String,
    val extensionAllowed: Boolean,
    val refundAllowed: Boolean,
    val makeupEnabled: Boolean,
    @field:PositiveOrZero
    val makeupExpiresInDays: Int,
    @field:PositiveOrZero
    val makeupMaxCount: Int,
) {
    fun toDomain(tenantId: UUID): AcademySettings =
        AcademySettings(
            tenantId = tenantId,
            name = name,
            contact = contact,
            address = address,
            branding = Branding(
                logoUrl = logoUrl,
                mainColor = mainColor,
            ),
            passPolicy = PassPolicy(
                extensionAllowed = extensionAllowed,
                refundAllowed = refundAllowed,
            ),
            makeupPolicy = MakeupPolicy(
                enabled = makeupEnabled,
                expiresInDays = makeupExpiresInDays,
                maxCount = makeupMaxCount,
            ),
        )
}

data class AcademySettingsResponse(
    val tenantId: UUID,
    val name: String,
    val contact: String,
    val address: String,
    val logoUrl: String?,
    val mainColor: String,
    val extensionAllowed: Boolean,
    val refundAllowed: Boolean,
    val makeupEnabled: Boolean,
    val makeupExpiresInDays: Int,
    val makeupMaxCount: Int,
)

private fun AcademySettings.toResponse(): AcademySettingsResponse =
    AcademySettingsResponse(
        tenantId = tenantId,
        name = name,
        contact = contact,
        address = address,
        logoUrl = branding.logoUrl,
        mainColor = branding.mainColor,
        extensionAllowed = passPolicy.extensionAllowed,
        refundAllowed = passPolicy.refundAllowed,
        makeupEnabled = makeupPolicy.enabled,
        makeupExpiresInDays = makeupPolicy.expiresInDays,
        makeupMaxCount = makeupPolicy.maxCount,
    )

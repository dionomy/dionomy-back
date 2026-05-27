package com.dionomy.tenant.presentation

import com.dionomy.tenant.application.GetCurrentTenantUseCase
import com.dionomy.tenant.domain.TenantStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/tenant")
class TenantController(
    private val getCurrentTenantUseCase: GetCurrentTenantUseCase,
) {
    @GetMapping("/current")
    fun current(@RequestHeader("X-Tenant-Id") tenantId: UUID): TenantResponse {
        val tenant = getCurrentTenantUseCase.execute(tenantId)

        return TenantResponse(
            id = tenant.id,
            name = tenant.name,
            status = tenant.status,
        )
    }
}

data class TenantResponse(
    val id: UUID,
    val name: String,
    val status: TenantStatus,
)
